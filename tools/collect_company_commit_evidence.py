#!/usr/bin/env python3
"""Create a privacy-preserving summary of commits attributed to configured authors.

The collector reads local Git repositories only. It uses Git author metadata for
matching, removes duplicate commit IDs, and emits aggregate evidence. It does not
emit repository paths, remote URLs, commit IDs, messages, file names, or emails.

Git author metadata is useful portfolio evidence, but it does not prove who
pushed a commit and must not be described as push-actor evidence.
"""

from __future__ import annotations

import argparse
import hashlib
import json
import re
import subprocess
import sys
from collections import Counter
from dataclasses import dataclass
from datetime import datetime, timezone
from pathlib import Path
from typing import Any, Iterable, Sequence


SCHEMA_VERSION = 1
SCHEMA_ID = "company-git-local-author-aggregate-v1"
CONVENTIONAL_TYPE = re.compile(
    r"^([a-z][a-z0-9-]*)(?:\([^\r\n)]+\))?!?:[ \t]+", re.IGNORECASE
)
PUBLIC_ALIAS = re.compile(r"^[A-Za-z0-9][A-Za-z0-9-]{0,63}$")


class EvidenceError(RuntimeError):
    """An expected, sanitized collection error."""


@dataclass(frozen=True)
class CommitRecord:
    sha: str
    authored_at: datetime
    author_name: str
    author_email: str
    subject: str


def _run_git(
    repository: Path,
    arguments: Sequence[str],
    *,
    alias: str,
    check: bool = True,
) -> subprocess.CompletedProcess[bytes]:
    process = subprocess.run(
        [
            "git",
            "-c",
            f"safe.directory={repository.resolve().as_posix()}",
            "-C",
            str(repository),
            *arguments,
        ],
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        check=False,
    )
    if check and process.returncode != 0:
        # Do not echo stderr: it can contain a repository path or remote URL.
        raise EvidenceError(f"Git operation failed for repository alias {alias!r}")
    return process


def _parse_authored_at(value: str, *, alias: str) -> datetime:
    try:
        parsed = datetime.fromisoformat(value.replace("Z", "+00:00"))
    except ValueError as error:
        raise EvidenceError(
            f"Git returned an invalid author timestamp for repository alias {alias!r}"
        ) from error
    if parsed.tzinfo is None:
        raise EvidenceError(
            f"Git returned a timezone-free author timestamp for repository alias {alias!r}"
        )
    return parsed.astimezone(timezone.utc)


def _read_commits(
    repository: Path, revision_arguments: Sequence[str], *, alias: str
) -> list[CommitRecord]:
    # NUL separates fields. A commit subject cannot contain NUL, and %s is a
    # single line. Git adds a newline between formatted commit records; it is
    # removed only from the following commit ID field.
    result = _run_git(
        repository,
        [
            "log",
            *revision_arguments,
            "--no-show-signature",
            "--format=%H%x00%aI%x00%an%x00%ae%x00%s%x00",
        ],
        alias=alias,
    )
    fields = result.stdout.split(b"\x00")
    commits: dict[str, CommitRecord] = {}
    position = 0
    while position + 4 < len(fields):
        sha_bytes = fields[position].lstrip(b"\r\n")
        if not sha_bytes:
            break
        sha = sha_bytes.decode("ascii", errors="strict")
        authored_at_text = fields[position + 1].decode("utf-8", errors="replace")
        record = CommitRecord(
            sha=sha,
            authored_at=_parse_authored_at(authored_at_text, alias=alias),
            author_name=fields[position + 2].decode("utf-8", errors="replace"),
            author_email=fields[position + 3].decode("utf-8", errors="replace"),
            subject=fields[position + 4].decode("utf-8", errors="replace"),
        )
        # A commit reachable from multiple refs is counted once.
        commits.setdefault(record.sha, record)
        position += 5
    return list(commits.values())


def _compile_author_patterns(raw_patterns: Any, *, alias: str) -> list[re.Pattern[str]]:
    if not isinstance(raw_patterns, list) or not raw_patterns:
        raise EvidenceError(
            f"author_patterns must be a non-empty list for repository alias {alias!r}"
        )
    compiled: list[re.Pattern[str]] = []
    for raw_pattern in raw_patterns:
        if not isinstance(raw_pattern, str) or not raw_pattern:
            raise EvidenceError(
                f"author_patterns contains an invalid value for repository alias {alias!r}"
            )
        try:
            compiled.append(re.compile(raw_pattern, re.IGNORECASE))
        except re.error as error:
            # Do not echo the pattern because it can contain an email address.
            raise EvidenceError(
                f"author_patterns contains invalid regex for repository alias {alias!r}"
            ) from error
    return compiled


def _matches_author(record: CommitRecord, patterns: Iterable[re.Pattern[str]]) -> bool:
    display_identity = f"{record.author_name} <{record.author_email}>"
    return any(
        pattern.search(record.author_name)
        or pattern.search(record.author_email)
        or pattern.search(display_identity)
        for pattern in patterns
    )


def _month_text(value: datetime) -> str:
    return value.astimezone(timezone.utc).strftime("%Y-%m")


def _commit_set_digest(commits: Iterable[CommitRecord]) -> str:
    commit_ids = sorted({commit.sha for commit in commits})
    material = "\n".join(commit_ids).encode("ascii")
    return "sha256:" + hashlib.sha256(material).hexdigest()


def _summarize(
    commits: Iterable[CommitRecord], *, include_verification_digests: bool
) -> dict[str, Any]:
    unique = {commit.sha: commit for commit in commits}
    records = list(unique.values())
    timestamps = sorted(commit.authored_at for commit in records)
    type_counts: Counter[str] = Counter()
    for commit in records:
        match = CONVENTIONAL_TYPE.match(commit.subject)
        type_counts[match.group(1).lower() if match else "other"] += 1

    summary: dict[str, Any] = {
        "commit_count": len(records),
        "authored_period_month": {
            "first": _month_text(timestamps[0]) if timestamps else None,
            "last": _month_text(timestamps[-1]) if timestamps else None,
        },
        "conventional_commit_type_counts": dict(sorted(type_counts.items())),
    }
    if include_verification_digests:
        summary["commit_set_digest"] = _commit_set_digest(records)
    return summary


def _source_fingerprint(repository: Path, *, alias: str) -> str:
    remote = _run_git(
        repository,
        ["config", "--get", "remote.origin.url"],
        alias=alias,
        check=False,
    )
    if remote.returncode == 0 and remote.stdout.rstrip(b"\r\n"):
        source_material = b"remote-origin\x00" + remote.stdout.rstrip(b"\r\n")
    else:
        top_level = _run_git(
            repository, ["rev-parse", "--show-toplevel"], alias=alias
        ).stdout.decode("utf-8", errors="replace").strip()
        normalized_path = str(Path(top_level).resolve()).replace("\\", "/").casefold()
        source_material = b"local-path\x00" + normalized_path.encode("utf-8")
    return "sha256:" + hashlib.sha256(source_material).hexdigest()


def _resolve_default_ref(repository: Path, default_ref: str, *, alias: str) -> str:
    result = _run_git(
        repository,
        ["rev-parse", "--verify", "--quiet", "--end-of-options", f"{default_ref}^{{commit}}"],
        alias=alias,
        check=False,
    )
    if result.returncode != 0:
        raise EvidenceError(
            f"default_ref could not be resolved for repository alias {alias!r}"
        )
    return result.stdout.decode("ascii", errors="strict").strip()


def _repository_evidence(
    entry: Any,
    config_directory: Path,
    *,
    include_verification_digests: bool,
) -> dict[str, Any]:
    if not isinstance(entry, dict):
        raise EvidenceError("Each repositories entry must be an object")
    alias = entry.get("alias")
    if not isinstance(alias, str) or not alias.strip():
        raise EvidenceError("Each repository must have a non-empty alias")
    alias = alias.strip()
    if not PUBLIC_ALIAS.fullmatch(alias):
        # Do not echo an unsafe alias. It can contain a repository name, URL,
        # customer identifier, email address, or path.
        raise EvidenceError(
            "Each repository alias must be a non-identifying value containing "
            "only letters, digits, and hyphens"
        )

    raw_path = entry.get("path")
    default_ref = entry.get("default_ref")
    if not isinstance(raw_path, str) or not raw_path:
        raise EvidenceError(f"path is required for repository alias {alias!r}")
    if not isinstance(default_ref, str) or not default_ref:
        raise EvidenceError(f"default_ref is required for repository alias {alias!r}")
    patterns = _compile_author_patterns(entry.get("author_patterns"), alias=alias)

    repository = Path(raw_path).expanduser()
    if not repository.is_absolute():
        repository = config_directory / repository
    repository = repository.resolve()
    if not repository.is_dir():
        # Deliberately omit the path from the error.
        raise EvidenceError(f"Repository path is unavailable for alias {alias!r}")

    _run_git(repository, ["rev-parse", "--git-dir"], alias=alias)
    resolved_default = _resolve_default_ref(repository, default_ref, alias=alias)
    all_records = _read_commits(repository, ["--all"], alias=alias)
    default_records = _read_commits(repository, [resolved_default], alias=alias)

    matched_all = [record for record in all_records if _matches_author(record, patterns)]
    matched_default = [
        record for record in default_records if _matches_author(record, patterns)
    ]
    evidence = {
        "alias": alias,
        "all_refs": _summarize(
            matched_all,
            include_verification_digests=include_verification_digests,
        ),
        "default_ref": {
            "scope": "configured_default_ref",
            **_summarize(
                matched_default,
                include_verification_digests=include_verification_digests,
            ),
        },
    }
    if include_verification_digests:
        evidence["source_fingerprint"] = _source_fingerprint(
            repository, alias=alias
        )
    return evidence


def collect(config: dict[str, Any], config_directory: Path) -> dict[str, Any]:
    if config.get("schema_version") != SCHEMA_VERSION:
        raise EvidenceError(f"schema_version must be {SCHEMA_VERSION}")
    repositories = config.get("repositories")
    if not isinstance(repositories, list) or not repositories:
        raise EvidenceError("repositories must be a non-empty list")

    include_verification_digests = config.get(
        "include_verification_digests", False
    )
    if not isinstance(include_verification_digests, bool):
        raise EvidenceError("include_verification_digests must be true or false")

    evidence: list[dict[str, Any]] = []
    aliases: set[str] = set()
    for entry in repositories:
        item = _repository_evidence(
            entry,
            config_directory,
            include_verification_digests=include_verification_digests,
        )
        if item["alias"] in aliases:
            raise EvidenceError(f"Duplicate repository alias {item['alias']!r}")
        aliases.add(item["alias"])
        evidence.append(item)

    return {
        "schema_version": SCHEMA_VERSION,
        "schema_id": SCHEMA_ID,
        "generated_on_utc": datetime.now(timezone.utc).date().isoformat(),
        "evidence_basis": (
            "Configured Git author metadata matched by regular expression; "
            "duplicate commit IDs removed within each configured repository."
        ),
        "aggregation_note": (
            "Repository counts can overlap when clones or forks share commits; "
            "do not sum them unless overlap has been ruled out."
        ),
        "source_currency": (
            "The report reflects local refs at generation time. It does not prove "
            "that every local clone was synchronized with its remote."
        ),
        "limitation": (
            "This aggregate does not prove the push actor, code ownership, "
            "employment, or work performed outside Git."
        ),
        "privacy": {
            "remote_urls_included": False,
            "repository_paths_included": False,
            "commit_ids_included": False,
            "raw_commit_messages_included": False,
            "file_names_included": False,
            "author_emails_included": False,
            "verification_digests_included": include_verification_digests,
        },
        "repositories": evidence,
    }


def _load_config(path: Path) -> dict[str, Any]:
    try:
        value = json.loads(path.read_text(encoding="utf-8"))
    except (OSError, UnicodeError, json.JSONDecodeError) as error:
        # Do not include the path or malformed content in the error.
        raise EvidenceError("Unable to read a valid JSON configuration") from error
    if not isinstance(value, dict):
        raise EvidenceError("Configuration root must be an object")
    return value


def _write_report(report: dict[str, Any], destination: str) -> None:
    serialized = json.dumps(report, ensure_ascii=False, indent=2, sort_keys=True) + "\n"
    if destination == "-":
        sys.stdout.write(serialized)
        return
    try:
        Path(destination).write_text(serialized, encoding="utf-8")
    except OSError as error:
        raise EvidenceError("Unable to write the evidence report") from error


def _parse_args(arguments: Sequence[str] | None) -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Collect privacy-preserving Git author evidence from local repositories."
    )
    parser.add_argument("--config", required=True, help="Path to the JSON configuration")
    parser.add_argument(
        "--output", default="-", help="Output JSON path, or - for standard output"
    )
    return parser.parse_args(arguments)


def main(arguments: Sequence[str] | None = None) -> int:
    args = _parse_args(arguments)
    try:
        config_path = Path(args.config).resolve()
        report = collect(_load_config(config_path), config_path.parent)
        _write_report(report, args.output)
    except EvidenceError as error:
        print(f"error: {error}", file=sys.stderr)
        return 2
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
