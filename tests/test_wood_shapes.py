"""Trace wooden stairs and slabs to the original variant recipes and models."""
import json
import unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
OLD=ROOT/'original/rustic-pre-1.2.0/src/main/resources/assets/rustic'
RES=ROOT/'src/main/resources'
class WoodShapeTests(unittest.TestCase):
    def test_original_variants_and_resources(self):
        entries={e['id']:e for e in json.loads((ROOT/'audit/recipe-parity.json').read_text())['entries']}
        axes=json.loads((RES/'data/minecraft/tags/block/mineable/axe.json').read_text())['values']
        for meta,kind in enumerate(('olive','ironwood')):
            for shape in ('slab','stairs'):
                name=f'{kind}_slab' if shape=='slab' else f'stairs_{kind}'
                source=json.loads((OLD/f'recipes/{kind}_{shape}.json').read_text())
                recipe=json.loads((RES/f'data/rustic/recipe/{kind}_{shape}.json').read_text())
                self.assertEqual(source['key']['P']['data'],meta)
                self.assertEqual(recipe['pattern'],source['pattern'])
                self.assertEqual(recipe['key']['P'],f'rustic:planks_{kind}')
                self.assertEqual(recipe['result'],{'id':f'rustic:{name}','count':6 if shape=='slab' else 4})
                self.assertEqual(entries[f'json:{kind}_{shape}.json']['status'],'in_progress')
                self.assertIn(f'rustic:{name}',axes)
                self.assertEqual((OLD/f'models/item/{name}.json').read_bytes(),(RES/f'assets/rustic/models/item/{name}.json').read_bytes())
                model_names=([f'{name}_bottom',f'{name}_top',f'{name}_double'] if shape=='slab' else [name,f'{name}_inner',f'{name}_outer'])
                for model in model_names:
                    original=(OLD/f'models/block/{model}.json').read_text()
                    migrated=(RES/f'assets/rustic/models/block/{model}.json').read_text()
                    self.assertEqual(migrated,original.replace('"block/half_slab"','"minecraft:block/slab"').replace('"block/upper_slab"','"minecraft:block/slab_top"'))
