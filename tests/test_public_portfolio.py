from __future__ import annotations

import re
import unittest
from pathlib import Path
from urllib.parse import unquote


PROJECT_ROOT = Path(__file__).resolve().parents[1]
CASE_STUDIES = PROJECT_ROOT / "03_portfolio" / "case-studies"
TEXT_SUFFIXES = {
    ".cmd",
    ".css",
    ".csv",
    ".html",
    ".java",
    ".js",
    ".json",
    ".md",
    ".properties",
    ".ps1",
    ".py",
    ".sh",
    ".svg",
    ".toml",
    ".txt",
    ".xml",
    ".yaml",
    ".yml",
}
TEXT_FILENAMES = {".env.example", "mvnw"}
EXCLUDED_PARTS = {".git", "_site", "target", "vendor", "__pycache__"}


def markdown_documents() -> list[Path]:
    return [
        path
        for path in PROJECT_ROOT.rglob("*.md")
        if not {".git", "_site", "vendor"}.intersection(path.parts)
    ]


def public_text_files() -> list[Path]:
    return [
        path
        for path in PROJECT_ROOT.rglob("*")
        if path.is_file()
        and not EXCLUDED_PARTS.intersection(path.parts)
        and (path.suffix.lower() in TEXT_SUFFIXES or path.name in TEXT_FILENAMES)
    ]


class PublicPortfolioTest(unittest.TestCase):
    def test_all_relative_markdown_links_resolve(self) -> None:
        pattern = re.compile(r"\[[^]]*\]\(([^)]+)\)")

        for document in markdown_documents():
            for raw_target in pattern.findall(document.read_text(encoding="utf-8")):
                target = raw_target.strip().strip("<>")
                if target.startswith(("http://", "https://", "#", "mailto:")):
                    continue
                if "{{" in target or "{%" in target:
                    continue

                relative_target = unquote(target.split("#", maxsplit=1)[0])
                if not relative_target:
                    continue

                resolved_target = document.parent / relative_target
                with self.subTest(document=document.relative_to(PROJECT_ROOT), target=target):
                    self.assertTrue(resolved_target.exists())
                    self.assertFalse(
                        resolved_target.is_dir(),
                        "link to an explicit document instead of relying on directory indexing",
                    )

    def test_case_studies_have_explicit_publication_state(self) -> None:
        allowed = {
            "candidate",
            "source-reviewed",
            "redaction-reviewed",
            "sample-implemented",
            "sample-verified",
            "published",
            "hold",
        }

        for document in CASE_STUDIES.glob("*.md"):
            if document.name == "README.md":
                continue

            content = document.read_text(encoding="utf-8")
            front_matter = re.match(r"^---\s*\n(.*?)\n---", content, re.DOTALL)
            with self.subTest(document=document.name):
                self.assertIsNotNone(front_matter)
                status = re.search(
                    r"^status:\s*([a-z-]+)\s*$",
                    front_matter.group(1),
                    re.MULTILINE,
                )
                self.assertIsNotNone(status)
                self.assertIn(status.group(1), allowed)

    def test_public_text_has_no_obvious_secret_or_local_path(self) -> None:
        forbidden = {
            "private-key": re.compile(r"BEGIN (?:RSA |OPENSSH |EC )?PRIVATE KEY"),
            "windows-user-path": re.compile(r"[A-Za-z]:\\Users\\"),
            "github-token": re.compile(r"gh[pousr]_[A-Za-z0-9]{20,}"),
            "github-fine-grained-token": re.compile(r"github_pat_[A-Za-z0-9_]{20,}"),
            "openai-style-key": re.compile(r"sk-[A-Za-z0-9_-]{20,}"),
            "aws-access-key": re.compile(r"AKIA[0-9A-Z]{16}"),
            "slack-token": re.compile(r"xox[baprs]-[A-Za-z0-9-]{20,}"),
            "private-ipv4": re.compile(
                r"(?<!\d)(?:10\.\d{1,3}\.\d{1,3}\.\d{1,3}|192\.168\.\d{1,3}\.\d{1,3}|172\.(?:1[6-9]|2\d|3[01])\.\d{1,3}\.\d{1,3})(?!\d)"
            ),
        }

        for document in public_text_files():
            content = document.read_text(encoding="utf-8")
            for label, pattern in forbidden.items():
                with self.subTest(document=document.relative_to(PROJECT_ROOT), check=label):
                    self.assertIsNone(pattern.search(content))

    def test_work_ledger_uses_supported_states(self) -> None:
        content = (PROJECT_ROOT / "WORKS.md").read_text(encoding="utf-8")
        states = re.findall(r"\| `W\d{2}` .*?\| `([a-z-]+)` \|", content)
        self.assertGreaterEqual(len(states), 9)
        self.assertLessEqual(
            set(states),
            {"pending", "in-progress", "blocked", "verified", "published"},
        )

    def test_jekyll_readme_links_use_directory_urls(self) -> None:
        for document in (PROJECT_ROOT / "index.md", PROJECT_ROOT / "llms.txt"):
            with self.subTest(document=document.name):
                self.assertNotIn("README.html", document.read_text(encoding="utf-8"))


if __name__ == "__main__":
    unittest.main()
