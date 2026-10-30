#!/usr/bin/env python3
"""Fail CI if the intended JUnit checks did not actually run successfully."""
from pathlib import Path
import sys
import xml.etree.ElementTree as ET

expected = {'wildberryFoodValues', 'grapeFoodValues', 'quickFoodDuration'}
cases = {}
for path in Path('build/test-results/test').glob('TEST-*.xml'):
    suite = ET.parse(path).getroot()
    for case in suite.findall('testcase'):
        if case.get('classname') == 'rustic.fabric.RusticFoodsTest':
            name = case.get('name')
            if name in cases:
                sys.exit(f'Duplicate JUnit case: {name}')
            cases[name] = case
if set(cases) != expected:
    sys.exit(f'JUnit cases differ: expected {sorted(expected)}, found {sorted(cases)}')
for name, case in cases.items():
    if any(case.find(tag) is not None for tag in ('failure', 'error', 'skipped')):
        sys.exit(f'JUnit case did not pass: {name}')
print('::notice::Three Fabric Loader JUnit food component tests passed; no world/gameplay test.')
