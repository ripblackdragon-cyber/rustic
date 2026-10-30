#!/usr/bin/env python3
"""Verify build output packaging; does not execute the mod or prove parity."""
import hashlib
import json
from pathlib import Path
import sys
import zipfile

ROOT = Path(__file__).resolve().parents[1]


def check_jar(jar: Path, root: Path = ROOT) -> tuple[int, str]:
    resources = root / 'src/main/resources'
    with zipfile.ZipFile(jar) as archive:
        entries = archive.namelist()
        if len(entries) != len(set(entries)):
            raise ValueError('duplicate ZIP entries')
        for name in ('rustic/fabric/RusticFabric.class', 'rustic/fabric/RusticItems.class', 'rustic/fabric/RusticFoods.class', 'rustic/fabric/RusticCreativeTabs.class', 'rustic/fabric/RusticBlocks.class', 'rustic/fabric/ChiliPepperItem.class'):
            if archive.read(name)[:4] != b'\xca\xfe\xba\xbe':
                raise ValueError(f'invalid class file: {name}')
        if any(name.startswith('rustic/fabric/gametest/') for name in entries):
            raise ValueError('GameTest classes must not be included in the development JAR')
        metadata = json.loads(archive.read('fabric.mod.json'))
        if metadata.get('id') != 'rustic' or metadata.get('version') != '0.0.0-dev':
            raise ValueError('unexpected Fabric metadata')
        if metadata.get('entrypoints', {}).get('main') != ['rustic.fabric.RusticFabric']:
            raise ValueError('unexpected entrypoint')
        if metadata.get('depends', {}).get('minecraft') != '=26.3':
            raise ValueError('unexpected Minecraft target')
        count = 0
        for file in sorted(resources.rglob('*')):
            if file.is_file() and file.name != 'fabric.mod.json':
                name = file.relative_to(resources).as_posix()
                if archive.read(name) != file.read_bytes():
                    raise ValueError(f'resource differs: {name}')
                count += 1
        for source, name in (
            ('THIRD_PARTY_NOTICES.md', 'META-INF/THIRD_PARTY_NOTICES.md'),
            ('licenses/GRADLE-APACHE-2.0.txt', 'META-INF/licenses/GRADLE-APACHE-2.0.txt'),
        ):
            if archive.read(name) != (root / source).read_bytes():
                raise ValueError(f'notice differs: {name}')
    return count, hashlib.sha256(jar.read_bytes()).hexdigest()


def main() -> int:
    jars = sorted(p for p in (ROOT / 'build/libs').glob('*.jar') if not p.name.endswith('-sources.jar'))
    if len(jars) != 1:
        print(f'Expected one development runtime JAR, found: {jars}', file=sys.stderr)
        return 1
    try:
        count, digest = check_jar(jars[0])
    except (OSError, KeyError, ValueError, zipfile.BadZipFile) as exc:
        print(f'JAR packaging check failed: {exc}', file=sys.stderr)
        return 1
    print(f'::notice::Development JAR {jars[0].name}; SHA-256 {digest}; {count} resources checked. Packaging check only; see GameTest results separately.')
    return 0


if __name__ == '__main__':
    sys.exit(main())
