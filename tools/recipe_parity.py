#!/usr/bin/env python3
"""Track and gate recipe mapping evidence, without pretending to evaluate Minecraft recipes.

The tracked set is an explicitly incomplete source index: JSON plus selected Java
expressions, NOT an expanded list of runtime recipes. The gate includes separate
coverage blockers and must not be treated as proof by itself.
"""
import argparse
import hashlib
import json
from pathlib import Path

import recipe_audit
import verify_upstream

ROOT = Path(__file__).resolve().parents[1]
MANIFEST = ROOT / 'audit/recipe-parity.json'
BASELINE = '88597f5ca4342a6b9d4509d93967ecbef6f8bafa'
RECIPE_DIR = ROOT / 'original/rustic-pre-1.2.0/src/main/resources/assets/rustic/recipes'
COVERAGE = (
    'published_jar_matches_baseline',
    'all_recipe_sources_and_runtime_expansions_audited',
    'all_recipe_behavior_tested_in_game',
    'jei_and_multiplayer_consistency_tested',
)
STATUSES = ('pending', 'in_progress', 'verified')


def sha(data):
    return hashlib.sha256(data).hexdigest()


def expected_entries():
    rows = []
    for path in sorted(RECIPE_DIR.glob('*.json')):
        rel = path.relative_to(ROOT).as_posix()
        digest = sha(path.read_bytes())
        rows.append({'id': f'json:{path.name}', 'source': rel, 'line': None,
                     'kind': 'json', 'file_sha256': digest, 'fragment_sha256': digest})
    path = recipe_audit.SOURCE
    raw = path.read_bytes()
    for line, kind, expression in recipe_audit.extract(raw.decode('utf-8')):
        rows.append({'id': f'java:{line}', 'source': path.relative_to(ROOT).as_posix(),
                     'line': line, 'kind': kind, 'file_sha256': sha(raw),
                     'fragment_sha256': sha(expression.encode('utf-8'))})
    ids = [r['id'] for r in rows]
    if not rows or len(ids) != len(set(ids)):
        raise ValueError('empty or duplicate source index')
    return rows


def new_manifest():
    entries = []
    for source in expected_entries():
        entries.append({**source, 'status': 'pending', 'targets': [], 'evidence': []})
    return {'schema_version': 1, 'baseline_commit': BASELINE, 'entries': entries,
            'coverage': [{'id': name, 'status': 'pending', 'evidence': []} for name in COVERAGE]}


def safe_file(root, value, *, target=False):
    if not isinstance(value, str) or not value or '\\' in value:
        return False
    relative = Path(value)
    if relative.is_absolute() or any(p in ('..', '.') for p in relative.parts):
        return False
    # Limit target files to the port source tree. Evidence may live elsewhere in the
    # workspace, except original reference copies and audit manifests.
    if target and (len(relative.parts) < 2 or relative.parts[0] != 'src'):
        return False
    if not target and relative.parts and relative.parts[0] in ('original', 'provenance', 'audit'):
        return False
    root = root.resolve()
    try:
        path = (root / relative).resolve(strict=True)
        if not path.is_relative_to(root) or not path.is_file():
            return False
        if target:
            return path.is_relative_to(root / 'src')
        return not any(path.is_relative_to(root / name) for name in ('original', 'provenance', 'audit'))
    except (OSError, RuntimeError):
        return False


def validate(manifest, expected, root=ROOT):
    """Return (errors, pending_count). Existing evidence is NOT automatically trustworthy."""
    errors = []
    pending = 0
    if not isinstance(manifest, dict):
        return ['manifest must be an object'], 0
    if type(manifest.get('schema_version')) is not int or manifest['schema_version'] != 1:
        errors.append('invalid schema_version')
    if manifest.get('baseline_commit') != BASELINE:
        errors.append('incorrect baseline commit')
    expected_map = {r['id']: r for r in expected}
    entries = manifest.get('entries')
    if not isinstance(entries, list):
        errors.append('entries must be a list')
        entries = []
    seen = set()
    for entry in entries:
        if not isinstance(entry, dict) or not isinstance(entry.get('id'), str):
            errors.append('invalid entry')
            continue
        identifier = entry['id']
        if identifier in seen:
            errors.append(f'duplicate entry: {identifier}')
            continue
        seen.add(identifier)
        original = expected_map.get(identifier)
        if original is None:
            errors.append(f'unknown entry: {identifier}')
            continue
        for field, expected_value in original.items():
            if entry.get(field) != expected_value:
                errors.append(f'{identifier}: mismatched {field}')
        status = entry.get('status')
        if status not in STATUSES:
            errors.append(f'{identifier}: invalid status')
        elif status != 'verified':
            pending += 1
        for field in ('targets', 'evidence'):
            values = entry.get(field)
            if not isinstance(values, list) or any(not isinstance(v, str) for v in values):
                errors.append(f'{identifier}: {field} must be a list of paths')
                continue
            if status == 'verified' and not values:
                errors.append(f'{identifier}: verified requires {field}')
            for path in values:
                if not safe_file(root, path, target=(field == 'targets')):
                    errors.append(f'{identifier}: invalid {field} path {path!r}')
    for identifier in sorted(expected_map.keys() - seen):
        errors.append(f'missing entry: {identifier}')
    coverage = manifest.get('coverage')
    if not isinstance(coverage, list):
        errors.append('coverage must be a list')
        coverage = []
    seen_coverage = set()
    for item in coverage:
        if not isinstance(item, dict) or not isinstance(item.get('id'), str):
            errors.append('invalid coverage entry')
            continue
        name = item['id']
        if name in seen_coverage:
            errors.append(f'duplicate coverage: {name}')
            continue
        seen_coverage.add(name)
        if name not in COVERAGE:
            errors.append(f'unknown coverage: {name}')
        status = item.get('status')
        if status not in STATUSES:
            errors.append(f'{name}: invalid status')
        elif status != 'verified':
            pending += 1
        evidence = item.get('evidence')
        if not isinstance(evidence, list) or any(not isinstance(v, str) for v in evidence):
            errors.append(f'{name}: evidence must be a list of paths')
        else:
            if status == 'verified' and not evidence:
                errors.append(f'{name}: verified requires evidence')
            for path in evidence:
                if not safe_file(root, path):
                    errors.append(f'{name}: invalid evidence path {path!r}')
    for name in sorted(set(COVERAGE) - seen_coverage):
        errors.append(f'missing coverage: {name}')
    return errors, pending


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('command', choices=('init', 'check'))
    args = parser.parse_args()
    try:
        # Fail closed if the upstream copy does not match the recorded Git blobs.
        differences = verify_upstream.verify('rustic-1.2.0')
        if any(differences):
            raise ValueError('original snapshot differs from recorded upstream blobs')
        if args.command == 'init':
            manifest = new_manifest()
            MANIFEST.parent.mkdir(parents=True, exist_ok=True)
            with MANIFEST.open('x', encoding='utf-8') as file:
                json.dump(manifest, file, ensure_ascii=False, indent=2)
                file.write('\n')
            print(f'Creado {MANIFEST.relative_to(ROOT)} con {len(manifest["entries"])} entradas pendientes.')
            return 0
        manifest = json.loads(MANIFEST.read_text(encoding='utf-8'))
        errors, pending = validate(manifest, expected_entries())
        for error in errors:
            print('ERROR:', error)
        print(f'Pendientes: {pending}; errores de estructura: {len(errors)}.')
        print('El índice no cubre automáticamente todas las recetas dinámicas; la evidencia requiere revisión humana.')
        return 1 if errors or pending else 0
    except (ValueError, OSError, UnicodeError, json.JSONDecodeError) as error:
        parser.exit(2, f'Error: {error}\n')


if __name__ == '__main__':
    raise SystemExit(main())
