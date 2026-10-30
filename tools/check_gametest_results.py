#!/usr/bin/env python3
"""Require the named Minecraft server GameTests, not merely a successful process."""
from pathlib import Path
import sys
import xml.etree.ElementTree as ET

EXPECTED = {
    'rustic_gametest:rustic_game_tests_items_registered',
    'rustic_gametest:rustic_game_tests_consuming_foods',
    'rustic_gametest:rustic_game_tests_furnace_recipes',
}


def check_results(report):
    cases = {}
    root = ET.parse(report).getroot()
    for case in root.iter('testcase'):
        name = case.get('name', '')
        if name.startswith('rustic_gametest:'):
            if name in cases:
                raise ValueError(f'Duplicate Minecraft GameTest: {name}')
            cases[name] = case
    if set(cases) != EXPECTED:
        raise ValueError(f'GameTests differ: expected {sorted(EXPECTED)}, found {sorted(cases)}')
    for name, case in cases.items():
        if any(case.find(tag) is not None for tag in ('error', 'failure', 'skipped')):
            raise ValueError(f'Minecraft GameTest did not pass: {name}')
    return len(cases)


def main():
    try:
        count = check_results(Path('build/reports/rustic-gametest.xml'))
    except (OSError, ValueError, ET.ParseError) as error:
        print('::error::' + str(error).replace('%', '%25').replace('\r', '%0D').replace('\n', '%0A'))
        return 1
    print(f'::notice::{count} Rustic Minecraft server GameTests passed; no interactive client or multiplayer.')
    return 0


if __name__ == '__main__':
    sys.exit(main())
