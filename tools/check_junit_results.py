#!/usr/bin/env python3
"""Fail CI if the intended JUnit checks did not actually run successfully."""
from pathlib import Path
import sys
import xml.etree.ElementTree as ET

EXPECTED = {'wildberryFoodValues', 'grapeFoodValues', 'quickFoodDuration', 'oliveFoodAndEffect', 'chiliFoodValues'}


def check_results(directory):
    cases = {}
    seen = []
    for path in Path(directory).glob('TEST-*.xml'):
        suite = ET.parse(path).getroot()
        for case in suite.findall('testcase'):
            seen.append((case.get('classname'), case.get('name')))
            if case.get('classname') == 'rustic.fabric.RusticFoodsTest':
                name = case.get('name', '').removesuffix('()')
                if name in cases:
                    raise ValueError(f'Duplicate JUnit case: {name}')
                cases[name] = case
    if set(cases) != EXPECTED:
        raise ValueError(f'JUnit cases differ: expected {sorted(EXPECTED)}, found {seen}')
    for name, case in cases.items():
        if any(case.find(tag) is not None for tag in ('failure', 'error', 'skipped')):
            raise ValueError(f'JUnit case did not pass: {name}')


def main():
    try:
        check_results('build/test-results/test')
    except (OSError, ValueError, ET.ParseError) as error:
        message = str(error).replace('%', '%25').replace('\r', '%0D').replace('\n', '%0A')
        print(f'::error::{message}')
        return 1
    print('::notice::Five Fabric Loader JUnit food component tests passed; no world/gameplay test.')
    return 0


if __name__ == '__main__':
    sys.exit(main())
