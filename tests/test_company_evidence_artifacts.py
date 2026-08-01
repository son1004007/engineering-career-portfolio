from __future__ import annotations

import csv
import json
import re
import unittest
from pathlib import Path


PROJECT_ROOT = Path(__file__).resolve().parents[1]
EVIDENCE_ROOT = PROJECT_ROOT / "evidence" / "company-github"


class CompanyEvidenceArtifactsTest(unittest.TestCase):
    def test_authenticated_summary_totals_are_consistent(self) -> None:
        path = (
            EVIDENCE_ROOT
            / "snapshots"
            / "authenticated-account-summary-2026-08-01.json"
        )
        report = json.loads(path.read_text(encoding="utf-8"))

        self.assertEqual("company-github-account-summary-v1", report["schema_id"])

        self.assertEqual(
            report["commit_count"], sum(report["monthly_commit_counts"].values())
        )
        workstreams = report["sanitized_workstreams"]
        self.assertEqual(
            report["commit_count"],
            sum(item["commit_count"] for item in workstreams),
        )
        for item in workstreams:
            self.assertEqual(
                item["commit_count"], sum(item["commit_type_counts"].values())
            )

        llm = next(
            item
            for item in workstreams
            if item["alias"] == "company-ai-llm-applications"
        )
        self.assertEqual(303, llm["commit_count"])
        self.assertEqual(
            303,
            sum(item["commit_count"] for item in report["sanitized_llm_project_counts"]),
        )
        self.assertEqual(
            {"WORK-AI-01", "WORK-AI-02", "WORK-AI-04"},
            {item["project_id"] for item in report["sanitized_llm_project_counts"]},
        )
        self.assertIn("does not prove the push actor", report["limitation"])
        self.assertIn("cross-repository SHA deduplication", report["count_semantics"])

    def test_career_claims_have_unique_ids_and_supported_states(self) -> None:
        with (EVIDENCE_ROOT / "career-claims.csv").open(
            encoding="utf-8", newline=""
        ) as source:
            rows = list(csv.DictReader(source))

        claim_ids = [row["claim_id"] for row in rows]
        self.assertEqual(len(claim_ids), len(set(claim_ids)))
        self.assertTrue(all(claim_ids))
        self.assertTrue(
            {row["evidence_level"] for row in rows} <= {"E0", "E1", "E2", "E3"}
        )
        self.assertTrue(
            {row["implementation_status"] for row in rows}
            <= {"designed", "implemented", "tested-component", "integrated", "operated", "planned"}
        )

    def test_local_public_snapshot_excludes_private_verification_fields(self) -> None:
        path = (
            EVIDENCE_ROOT
            / "snapshots"
            / "local-author-metadata-2026-08-01.json"
        )
        report = json.loads(path.read_text(encoding="utf-8"))
        serialized = json.dumps(report, ensure_ascii=False)

        self.assertEqual("company-git-local-author-aggregate-v1", report["schema_id"])
        self.assertEqual("2026-08-01", report["generated_on_utc"])
        self.assertFalse(any(report["privacy"].values()))
        self.assertNotIn("source_fingerprint", serialized)
        self.assertNotIn("commit_set_digest", serialized)
        self.assertNotIn("remote.origin", serialized)
        self.assertNotIn("refs/heads", serialized)
        self.assertIn("does not prove the push actor", report["limitation"])
        for item in report["repositories"]:
            self.assertRegex(item["alias"], r"^[A-Za-z0-9][A-Za-z0-9-]{0,63}$")

    def test_changed_markdown_relative_links_exist(self) -> None:
        documents = [
            PROJECT_ROOT / "README.md",
            PROJECT_ROOT / "AI_CONTEXT.md",
            PROJECT_ROOT / "03_portfolio" / "evidence-index.md",
            EVIDENCE_ROOT / "README.md",
            EVIDENCE_ROOT / "projects.md",
            EVIDENCE_ROOT / "monthly" / "2026-08.md",
        ]
        pattern = re.compile(r"\[[^]]*\]\(([^)]+)\)")

        for document in documents:
            for target in pattern.findall(document.read_text(encoding="utf-8")):
                if target.startswith(("http://", "https://", "#", "mailto:")):
                    continue
                relative_target = target.split("#", maxsplit=1)[0]
                with self.subTest(document=document.name, target=relative_target):
                    self.assertTrue((document.parent / relative_target).exists())


if __name__ == "__main__":
    unittest.main()
