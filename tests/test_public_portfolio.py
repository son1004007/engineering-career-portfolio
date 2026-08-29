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


def jekyll_page_defaults() -> dict[tuple[str, str | None], dict[str, str]]:
    content = (PROJECT_ROOT / "_config.yml").read_text(encoding="utf-8")
    defaults: dict[tuple[str, str | None], dict[str, str]] = {}
    pattern = re.compile(
        r'^  - scope:\n'
        r'      path: "([^"]*)"\n'
        r'(?:      type: "([^"]+)"\n)?'
        r'    values:\n'
        r'((?:      [a-z_]+: [^\n]+\n?)+)',
        re.MULTILINE,
    )

    for path, page_type, raw_values in pattern.findall(content):
        values = {
            key: value.strip().strip('"')
            for key, value in re.findall(
                r'^      ([a-z_]+): (.+)$', raw_values, re.MULTILINE
            )
        }
        defaults[(path, page_type or None)] = values

    return defaults


class PublicPortfolioTest(unittest.TestCase):
    def test_recruiter_pages_hide_internal_management_labels(self) -> None:
        home = (PROJECT_ROOT / "index.md").read_text(encoding="utf-8")
        blog = (PROJECT_ROOT / "blog.md").read_text(encoding="utf-8")
        badge = (PROJECT_ROOT / "_includes" / "status-badge.html").read_text(
            encoding="utf-8"
        )
        case_card = (PROJECT_ROOT / "_includes" / "case-card.html").read_text(
            encoding="utf-8"
        )

        for label in [
            "Current positioning",
            "Evidence map",
            "주장을 검증하는 경로",
            "근거와 현재 범위 보기",
        ]:
            with self.subTest(label=label):
                self.assertNotIn(label, home + blog)

        self.assertNotIn("<code>{{ badge_status }}</code>", badge)
        self.assertNotIn("case-card__id", case_card)
        self.assertNotIn("status-legend", blog)

    def test_mobile_baseline_and_published_status_are_user_facing(self) -> None:
        layout = (PROJECT_ROOT / "_layouts" / "default.html").read_text(
            encoding="utf-8"
        )
        css = (PROJECT_ROOT / "assets" / "css" / "main.css").read_text(
            encoding="utf-8"
        )
        badge = (PROJECT_ROOT / "_includes" / "status-badge.html").read_text(
            encoding="utf-8"
        )
        home = (PROJECT_ROOT / "index.md").read_text(encoding="utf-8")

        self.assertIn(
            '<meta name="viewport" content="width=device-width, initial-scale=1">',
            layout,
        )
        self.assertIn("@media (max-width: 52rem)", css)
        self.assertIn("@media (max-width: 32rem)", css)
        self.assertRegex(css, r"\.site-nav\s*\{[^}]*overflow-x:\s*auto;")
        self.assertRegex(css, r"\.prose pre\s*\{[^}]*overflow-x:\s*auto;")
        self.assertRegex(
            css,
            r"(?s)@media \(max-width: 32rem\).*?\.prose table\s*\{[^}]*overflow-x:\s*auto;",
        )
        self.assertIn("{% when 'published' %}", badge)
        self.assertIn("게시·검증 완료", badge)
        self.assertIn('id="CS-JAVA-01" status="published"', home)

    def test_internal_documents_are_noindex_without_hiding_public_pages(self) -> None:
        defaults = jekyll_page_defaults()

        for path, page_type in [
            ("AI_CONTEXT.md", None),
            ("README.md", None),
            ("TASKS.md", None),
            ("WORKFLOW.md", None),
            ("WORKS.md", None),
            ("01_profile", "pages"),
            ("03_portfolio", "pages"),
            ("evidence", "pages"),
        ]:
            with self.subTest(path=path):
                self.assertEqual(
                    defaults[(path, page_type)].get("robots"), "noindex,follow"
                )

        self.assertEqual(
            defaults[("03_portfolio/case-studies", "pages")].get("robots"),
            "index,follow",
        )
        for scope in [("", "pages"), ("", "posts"), ("02_projects", "pages")]:
            with self.subTest(public_scope=scope):
                self.assertEqual(
                    defaults[scope].get("robots", "index,follow"),
                    "index,follow",
                )

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

    def test_jekyll_internal_document_links_use_pretty_urls(self) -> None:
        documents = [
            PROJECT_ROOT / "index.md",
            PROJECT_ROOT / "llms.txt",
            *sorted((PROJECT_ROOT / "_includes").glob("*.html")),
            *sorted((PROJECT_ROOT / "_layouts").glob("*.html")),
            *sorted((PROJECT_ROOT / "_posts").glob("*.md")),
        ]
        for document in documents:
            content = document.read_text(encoding="utf-8")
            with self.subTest(document=document.name):
                self.assertIsNone(
                    re.search(
                        r"\{\{\s*'/[^']*\.html(?:#[^']*)?'\s*\|\s*relative_url",
                        content,
                    )
                )
                self.assertIsNone(
                    re.search(
                        r"https://son1004007\.github\.io/engineering-career-portfolio/\S*\.html(?:\s|$)",
                        content,
                    )
                )


if __name__ == "__main__":
    unittest.main()
