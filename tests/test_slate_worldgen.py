import json
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[1]

class SlateWorldgenTests(unittest.TestCase):
    def test_default_definition(self):
        base = ROOT / 'src/main/resources/data/rustic/worldgen'
        feature = json.loads((base / 'feature/slate.json').read_text())
        placement = json.loads((base / 'placed_feature/slate.json').read_text())
        self.assertEqual(feature['type'], 'minecraft:ore')
        self.assertEqual(feature['size'], 20)
        self.assertEqual(feature['discard_chance_on_air_exposure'], 0.0)
        self.assertEqual(feature['targets'], [{'state':'rustic:slate', 'target':{'predicate_type':'minecraft:block_match', 'block':'minecraft:stone'}}])
        self.assertEqual(placement['feature'], 'rustic:slate')
        self.assertEqual(placement['placement'][0], {'type':'minecraft:count','count':5})
        height = placement['placement'][2]['height']
        self.assertEqual(height, {'type':'minecraft:uniform','min_inclusive':{'absolute':4},'max_inclusive':{'absolute':83}})
        self.assertEqual(placement['placement'][-1], {'type':'minecraft:biome'})
