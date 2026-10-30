#!/usr/bin/env python3
"""Compare preserved files with Git blob hashes recorded from the pinned upstream trees."""
from pathlib import Path
import hashlib
import json

ROOT = Path(__file__).resolve().parents[1]
SNAPSHOTS = {
    'rustic-1.2.0': 'original/rustic-pre-1.2.0',
    'rustic-master': 'original/rustic',
    'wiki': 'original/wiki',
}


def blob_hash(path):
    data = path.read_bytes()
    return hashlib.sha1(b'blob ' + str(len(data)).encode('ascii') + b'\0' + data).hexdigest()


def verify(name):
    manifest = json.loads((ROOT / 'provenance' / f'{name}.json').read_text(encoding='utf-8'))
    base = ROOT / SNAPSHOTS[name]
    expected = manifest['blobs']
    actual = {p.relative_to(base).as_posix(): blob_hash(p) for p in base.rglob('*') if p.is_file()}
    missing = sorted(expected.keys() - actual.keys())
    extra = sorted(actual.keys() - expected.keys())
    changed = sorted(p for p in expected.keys() & actual.keys() if expected[p] != actual[p])
    return missing, extra, changed


def main():
    failed = False
    for name in SNAPSHOTS:
        missing, extra, changed = verify(name)
        print(f'{name}: missing={len(missing)} extra={len(extra)} changed={len(changed)}')
        for kind, values in [('missing', missing), ('extra', extra), ('changed', changed)]:
            for value in values:
                print(f'  {kind}: {value}')
        failed |= bool(missing or extra or changed)
    return int(failed)


if __name__ == '__main__':
    raise SystemExit(main())
