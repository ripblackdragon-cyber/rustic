#!/usr/bin/env python3
"""Preserve 1.12.2 language keys and values, adding explicitly audited modern aliases.

Most modern translation keys remain unmapped; in-game behavior is not validated.
"""
import argparse
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / 'original/rustic-pre-1.2.0/src/main/resources/assets/rustic/lang'
DESTINATION = ROOT / 'src/main/resources/assets/rustic/lang'

# Only aliases backed by a migrated registry identifier belong here.
ALIASES = {f'item.rustic.{name}.name': f'item.rustic.{name}' for name in (
    'bee', 'honeycomb', 'beeswax', 'tallow', 'dust_tiny_iron', 'dust_gold',
    'wildberries', 'grapes', 'olives', 'chili_pepper'
)}

# BlockItem names use their block translation key.
ALIASES.update({f'tile.rustic.{name}.name': f'block.rustic.{name}' for name in (
    'slate', 'slate_roof', 'slate_tile', 'slate_brick', 'slate_chiseled',
    'slate_pillar', 'stairs_slate_roof', 'stairs_slate_brick', 'slate_roof_slab', 'slate_brick_slab',
    'painted_wood_white', 'painted_wood_orange', 'painted_wood_magenta', 'painted_wood_light_blue', 'painted_wood_yellow', 'painted_wood_lime', 'painted_wood_pink', 'painted_wood_gray', 'painted_wood_silver', 'painted_wood_cyan', 'painted_wood_purple', 'painted_wood_blue', 'painted_wood_brown', 'painted_wood_green', 'painted_wood_red', 'painted_wood_black',
    'clay_wall', 'clay_wall_cross', 'clay_wall_diag'
)})


def parse_lang(raw, name='<input>'):
    # utf-8-sig accepts the BOM in two upstream files and preserves every value byte
    # as Unicode text; trailing spaces are significant in prefixes and separators.
    text = raw.decode('utf-8-sig')
    result = {}
    for line_no, line in enumerate(text.splitlines(), 1):
        if not line.strip() or line.lstrip().startswith('#'):
            continue
        if '=' not in line:
            raise ValueError(f'{name}:{line_no}: expected key=value')
        key, value = line.split('=', 1)
        if not key or key != key.strip():
            raise ValueError(f'{name}:{line_no}: invalid key {key!r}')
        if key in result:
            raise ValueError(f'{name}:{line_no}: duplicate key {key!r}')
        result[key] = value
    return result


def conversions(source):
    result = {}
    for path in sorted(source.glob('*.lang')):
        name = path.stem.lower() + '.json'
        if name in result:
            raise ValueError(f'locale name collision: {name}')
        values = parse_lang(path.read_bytes(), path.name)
        for old_key, new_key in ALIASES.items():
            if old_key in values:
                if new_key in values and values[new_key] != values[old_key]:
                    raise ValueError(f'{path.name}: alias conflicts with {new_key}')
                values[new_key] = values[old_key]
        result[name] = (json.dumps(values, ensure_ascii=False, indent=2) + '\n').encode('utf-8')
    if not result:
        raise ValueError(f'no .lang files found in {source}')
    return result


def convert(source, destination, write=False):
    expected = conversions(source)
    # Do all preflight checks before writing anything; never overwrite user changes.
    errors = []
    missing = []
    if destination.is_symlink():
        errors.append(f'symlink is not permitted: {destination}')
    for name, data in expected.items():
        path = destination / name
        if path.is_symlink():
            errors.append(f'symlink is not permitted: {path}')
        elif not path.exists():
            missing.append(name)
        elif not path.is_file() or path.read_bytes() != data:
            errors.append(f'different existing file: {path}')
    if not write:
        errors.extend(f'missing file: {destination / name}' for name in missing)
    if errors:
        raise ValueError('\n'.join(errors))
    if write and missing:
        destination.mkdir(parents=True, exist_ok=True)
        for name in missing:
            # Exclusive creation avoids silently overwriting a concurrent change.
            with (destination / name).open('xb') as file:
                file.write(expected[name])
    return len(expected), len(missing)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--write', action='store_true', help='create missing conversions; never overwrite existing files')
    args = parser.parse_args()
    try:
        count, missing = convert(SOURCE, DESTINATION, write=args.write)
    except (ValueError, OSError, UnicodeError) as error:
        parser.exit(1, f'{error}\n')
    print(f'{count} locales comprobados; {missing if args.write else 0} archivos creados.')
    print('Se han añadido alias modernos para diez objetos y veintinueve bloques; faltan las demás claves y pruebas en juego.')


if __name__ == '__main__':
    main()
