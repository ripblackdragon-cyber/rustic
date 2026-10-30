#!/usr/bin/env python3
"""Copy original texture files byte-for-byte without overwriting existing files.

Preserves legacy paths; model/renderer adaptation and in-game validation are separate.
"""
import argparse
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / 'original/rustic-pre-1.2.0/src/main/resources/assets/rustic/textures'
DESTINATION = ROOT / 'src/main/resources/assets/rustic/textures'


def copy_textures(source, destination, write=False):
    if not source.is_dir():
        raise ValueError(f'no source directory: {source}')
    files = sorted(path for path in source.rglob('*') if path.is_file())
    if not files:
        raise ValueError('source directory contains no files')
    conflicts, missing = [], []
    for source_file in files:
        relative = source_file.relative_to(source)
        target = destination / relative
        # Refuse symlinked files and directories inside the output tree.
        ancestors = [target, *(destination / p for p in relative.parents if str(p) != '.') , destination]
        if any(path.is_symlink() for path in ancestors):
            conflicts.append(f'symlink is not permitted: {target}')
        elif not target.exists():
            missing.append((source_file, target))
        elif not target.is_file() or target.read_bytes() != source_file.read_bytes():
            conflicts.append(f'different existing file: {target}')
    if not write:
        conflicts.extend(f'missing file: {target}' for _, target in missing)
    if conflicts:
        raise ValueError('\n'.join(conflicts))
    if write:
        for source_file, target in missing:
            target.parent.mkdir(parents=True, exist_ok=True)
            # Exclusive creation prevents clobbering an existing or concurrently created file.
            with target.open('xb') as out:
                out.write(source_file.read_bytes())
    return len(files), len(missing)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--write', action='store_true', help='create missing files; never overwrite existing files')
    args = parser.parse_args()
    try:
        count, missing = copy_textures(SOURCE, DESTINATION, write=args.write)
    except (ValueError, OSError) as error:
        parser.exit(1, f'{error}\n')
    print(f'{count} archivos de textura comprobados; {missing if args.write else 0} creados.')
    print('Las rutas siguen siendo legacy; no se ha comprobado su uso ni apariencia en el juego.')


if __name__ == '__main__':
    main()
