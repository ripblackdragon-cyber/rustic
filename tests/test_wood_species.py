"""Cross-check migrated wood against the original 1.12 metadata variants."""
import json
import unittest
from pathlib import Path
ROOT = Path(__file__).resolve().parents[1]
OLD = ROOT / 'original/rustic-pre-1.2.0/src/main/resources/assets/rustic'
RES = ROOT / 'src/main/resources'

class WoodSpeciesTests(unittest.TestCase):
    def test_wood_sources_and_assets(self):
        entries = {e['id']:e for e in json.loads((ROOT/'audit/recipe-parity.json').read_text())['entries']}
        for meta, kind in enumerate(('olive','ironwood')):
            source = json.loads((OLD/f'recipes/{kind}_planks.json').read_text())
            recipe = json.loads((RES/f'data/rustic/recipe/{kind}_planks.json').read_text())
            self.assertEqual(source['ingredients'][0]['data'], meta)
            self.assertEqual(source['result']['data'], meta)
            self.assertEqual(recipe['ingredients'], [f'rustic:log_{kind}'])
            self.assertEqual(recipe['result'], {'id':f'rustic:planks_{kind}', 'count':4})
            self.assertEqual(entries[f'json:{kind}_planks.json']['status'], 'in_progress')
            smelt = json.loads((RES/f'data/rustic/recipe/charcoal_from_{kind}_log.json').read_text())
            self.assertEqual((smelt['ingredient'], smelt['result']['id'], smelt['experience'], smelt['cookingtime']),
                             (f'rustic:log_{kind}', 'minecraft:charcoal', 0.15, 200))
            for name in (f'log_{kind}', f'planks_{kind}'):
                for category in ('models/block', 'models/item'):
                    self.assertEqual((OLD/f'{category}/{name}.json').read_bytes(), (RES/f'assets/rustic/{category}/{name}.json').read_bytes())
                lang=json.loads((RES/'assets/rustic/lang/en_us.json').read_text())
                self.assertEqual(lang[f'block.rustic.{name}'], lang[f'tile.rustic.{name}.name'])
                self.assertIn(f'rustic:{name}', json.loads((RES/'data/minecraft/tags/block/mineable/axe.json').read_text())['values'])
        for kind in ('block','item'):
            for tag,prefix in (('planks','planks'),('logs_that_burn','log')):
                values=json.loads((RES/f'data/minecraft/tags/{kind}/{tag}.json').read_text())['values']
                self.assertTrue({f'rustic:{prefix}_olive',f'rustic:{prefix}_ironwood'}.issubset(set(values)))
