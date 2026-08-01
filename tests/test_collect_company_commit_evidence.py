from __future__ import annotations

import hashlib
import json
import os
import re
import subprocess
import sys
import tempfile
import unittest
from pathlib import Path


PROJECT_ROOT = Path(__file__).resolve().parents[1]
COLLECTOR = PROJECT_ROOT / "tools" / "collect_company_commit_evidence.py"


class CommitEvidenceCollectorTest(unittest.TestCase):
    def setUp(self) -> None:
        self.temporary_directory = tempfile.TemporaryDirectory()
        self.addCleanup(self.temporary_directory.cleanup)
        self.root = Path(self.temporary_directory.name)
        self.repository = self.root / "company-repository"
        self.repository.mkdir()
        self._git("init")
        self._git("branch", "-M", "main")
        self._git("config", "user.name", "Career User")
        self._git("config", "user.email", "career.user@example.com")
        self._git("config", "commit.gpgsign", "false")

    def _git(
        self, *arguments: str, author: str | None = None, date: str | None = None
    ) -> str:
        environment = os.environ.copy()
        if author is not None:
            name, email = author.split("|", maxsplit=1)
            environment.update(
                {
                    "GIT_AUTHOR_NAME": name,
                    "GIT_AUTHOR_EMAIL": email,
                    "GIT_COMMITTER_NAME": name,
                    "GIT_COMMITTER_EMAIL": email,
                }
            )
        if date is not None:
            environment["GIT_AUTHOR_DATE"] = date
            environment["GIT_COMMITTER_DATE"] = date
        process = subprocess.run(
            ["git", "-C", str(self.repository), *arguments],
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True,
            encoding="utf-8",
            check=False,
            env=environment,
        )
        if process.returncode != 0:
            self.fail(
                f"git {' '.join(arguments)} failed: {process.stdout}\n{process.stderr}"
            )
        return process.stdout.strip()

    def _commit(
        self,
        file_name: str,
        contents: str,
        subject: str,
        *,
        author: str,
        date: str,
    ) -> str:
        (self.repository / file_name).write_text(contents, encoding="utf-8")
        self._git("add", file_name)
        self._git(
            "-c",
            "commit.gpgsign=false",
            "commit",
            "-m",
            subject,
            author=author,
            date=date,
        )
        return self._git("rev-parse", "HEAD")

    def _run_collector(self, config: dict[str, object]) -> tuple[dict[str, object], str]:
        config_path = self.root / "collector-config.json"
        output_path = self.root / "evidence.json"
        config_path.write_text(json.dumps(config), encoding="utf-8")
        process = subprocess.run(
            [
                sys.executable,
                str(COLLECTOR),
                "--config",
                str(config_path),
                "--output",
                str(output_path),
            ],
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True,
            encoding="utf-8",
            check=False,
        )
        if process.returncode != 0:
            self.fail(f"collector failed: {process.stdout}\n{process.stderr}")
        serialized = output_path.read_text(encoding="utf-8")
        return json.loads(serialized), serialized

    def test_collects_filtered_deduplicated_all_and_default_ref_statistics(self) -> None:
        first_sha = self._commit(
            "confidential-customer-name.txt",
            "not exported",
            "feat(core): implement secret customer workflow",
            author="Career User|career.user@example.com",
            date="2026-01-01T09:00:00+09:00",
        )
        self._git("checkout", "-b", "private-feature")
        feature_sha = self._commit(
            "internal-model-name.py",
            "not exported either",
            "fix: repair private model integration",
            author="Career User|career.user@example.com",
            date="2026-01-03T09:00:00+09:00",
        )
        self._git("checkout", "main")
        self._commit(
            "other-author.txt",
            "excluded",
            "docs: another employee private work",
            author="Other Employee|other.employee@example.com",
            date="2026-01-02T09:00:00+09:00",
        )

        secret_remote = "ssh://example.invalid/private/company.git"
        self._git("remote", "add", "origin", secret_remote)
        report, serialized = self._run_collector(
            {
                "schema_version": 1,
                "include_verification_digests": True,
                "repositories": [
                    {
                        "path": str(self.repository),
                        "alias": "company-project-a",
                        "default_ref": "main",
                        # The first commit matches both patterns but must count once.
                        "author_patterns": [
                            "^Career User$",
                            "^career[.]user@example[.]com$",
                        ],
                    }
                ],
            }
        )

        repository = report["repositories"][0]
        self.assertEqual("company-git-local-author-aggregate-v1", report["schema_id"])
        all_refs = repository["all_refs"]
        default_ref = repository["default_ref"]
        self.assertEqual(2, all_refs["commit_count"])
        self.assertEqual({"feat": 1, "fix": 1}, all_refs["conventional_commit_type_counts"])
        self.assertEqual(
            {"first": "2026-01", "last": "2026-01"},
            all_refs["authored_period_month"],
        )
        self.assertEqual(1, default_ref["commit_count"])
        self.assertEqual({"feat": 1}, default_ref["conventional_commit_type_counts"])
        self.assertEqual("configured_default_ref", default_ref["scope"])
        self.assertNotIn("name", default_ref)

        expected_digest = "sha256:" + hashlib.sha256(
            "\n".join(sorted([first_sha, feature_sha])).encode("ascii")
        ).hexdigest()
        self.assertEqual(expected_digest, all_refs["commit_set_digest"])
        expected_source_fingerprint = "sha256:" + hashlib.sha256(
            b"remote-origin\x00" + secret_remote.encode("utf-8")
        ).hexdigest()
        self.assertEqual(expected_source_fingerprint, repository["source_fingerprint"])

        self.assertRegex(repository["source_fingerprint"], r"^sha256:[0-9a-f]{64}$")
        for secret in (
            secret_remote,
            str(self.repository),
            "career.user@example.com",
            "other.employee@example.com",
            "confidential-customer-name.txt",
            "internal-model-name.py",
            "implement secret customer workflow",
            "repair private model integration",
            first_sha,
            feature_sha,
        ):
            self.assertNotIn(secret, serialized)
        self.assertFalse(report["privacy"]["remote_urls_included"])
        self.assertFalse(report["privacy"]["raw_commit_messages_included"])
        self.assertTrue(report["privacy"]["verification_digests_included"])
        self.assertIn("does not prove the push actor", report["limitation"])
        self.assertIn("local refs", report["source_currency"])

    def test_no_matching_author_emits_empty_scopes_without_private_data(self) -> None:
        self._commit(
            "private.csv",
            "customer data",
            "not-conventional private subject",
            author="Other Employee|other.employee@example.com",
            date="2026-02-01T10:00:00+09:00",
        )
        report, serialized = self._run_collector(
            {
                "schema_version": 1,
                "repositories": [
                    {
                        "path": str(self.repository),
                        "alias": "company-project-empty",
                        "default_ref": "main",
                        "author_patterns": ["^Career User$"],
                    }
                ],
            }
        )

        repository = report["repositories"][0]
        for scope in (repository["all_refs"], repository["default_ref"]):
            self.assertEqual(0, scope["commit_count"])
            self.assertEqual(
                {"first": None, "last": None}, scope["authored_period_month"]
            )
            self.assertEqual({}, scope["conventional_commit_type_counts"])
            self.assertNotIn("commit_set_digest", scope)
        self.assertNotIn("source_fingerprint", repository)
        self.assertFalse(report["privacy"]["verification_digests_included"])
        self.assertNotIn("other.employee@example.com", serialized)
        self.assertNotIn("private.csv", serialized)
        self.assertNotIn("not-conventional private subject", serialized)

    def test_rejects_unsafe_alias_without_echoing_it(self) -> None:
        unsafe_alias = "https://private.example.invalid/customer/repository"
        config_path = self.root / "collector-config.json"
        output_path = self.root / "evidence.json"
        config_path.write_text(
            json.dumps(
                {
                    "schema_version": 1,
                    "repositories": [
                        {
                            "path": str(self.repository),
                            "alias": unsafe_alias,
                            "default_ref": "main",
                            "author_patterns": ["^Career User$"],
                        }
                    ],
                }
            ),
            encoding="utf-8",
        )
        process = subprocess.run(
            [
                sys.executable,
                str(COLLECTOR),
                "--config",
                str(config_path),
                "--output",
                str(output_path),
            ],
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True,
            encoding="utf-8",
            check=False,
        )

        self.assertEqual(2, process.returncode)
        self.assertNotIn(unsafe_alias, process.stderr)
        self.assertFalse(output_path.exists())


if __name__ == "__main__":
    unittest.main()
