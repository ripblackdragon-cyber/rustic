"""Trace door resources and recipes; this is not a visual or in-game test."""
import json
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OLD = ROOT / 'original/rustic-pre-1.2.0/src/main/resources/assets/rustic'
RES = ROOT / 'src/main/resources'

class DoorTests(unittest.TestCase):
    def test_doors(self):
        entries = {e['id']: e for e in json.loads((ROOT / 'audit/recipe-parity.json').read_text())['entries']}
        for meta, wood in enumerate(('olive', 'ironwood')):
            name = f'{wood}_door'
            original = json.loads((OLD / f'recipes/{name}.json').read_text())
            recipe = json.loads((RES / f'data/rustic/recipe/{name}.json').read_text())
            self.assertEqual(original['key']['#']['data'], meta)
            self.assertEqual(original['pattern'], recipe['pattern'])
            self.assertEqual(recipe['key']['#'], f'rustic:planks_{wood}')
            self.assertEqual(recipe['result'], {'id':f'rustic:{name}', 'count':3})
            self.assertEqual(entries[f'json:{name}.json']['status'], 'in_progress')
            states = json.loads((RES / f'assets/rustic/blockstates/{name}.json').read_text())['variants']
            self.assertEqual(len(states), 32)
            self.assertEqual(set(states), set(json.loads((OLD / f'blockstates/{name}.json').read_text())['variants']))
            for variant in states.values():
                model = variant['model'].removeprefix('rustic:')
                self.assertTrue((RES / 'assets/rustic/models' / f'{model}.json').is_file())
            self.assertIn(f'rustic:{name}', json.loads((RES / 'data/minecraft/tags/block/mineable/axe.json').read_text())['values'])
