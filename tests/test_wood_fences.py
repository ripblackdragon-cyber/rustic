import json
import unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
OLD=ROOT/'original/rustic-pre-1.2.0/src/main/resources/assets/rustic'
RES=ROOT/'src/main/resources'
class WoodFenceTests(unittest.TestCase):
    def test_recipes_models_and_tag(self):
        entries={e['id']:e for e in json.loads((ROOT/'audit/recipe-parity.json').read_text())['entries']}
        tag=json.loads((RES/'data/rustic/tags/item/wooden_sticks.json').read_text())
        self.assertIn('minecraft:stick',tag['values'])
        self.assertIn({'id':'#c:rods/wooden','required':False},tag['values'])
        for meta,wood in enumerate(('olive','ironwood')):
            for gate in (False,True):
                name=f'fence{"_gate" if gate else ""}_{wood}'
                source=f'{wood}_fence{"_gate" if gate else ""}.json'
                original=json.loads((OLD/f'recipes/{source}').read_text())
                migrated=json.loads((RES/f'data/rustic/recipe/{source}').read_text())
                self.assertEqual(original['key']['P']['data'],meta)
                self.assertEqual(original['pattern'],migrated['pattern'])
                self.assertEqual(migrated['key'],{'P':f'rustic:planks_{wood}','S':'#rustic:wooden_sticks'})
                self.assertEqual(migrated['result'],{'id':f'rustic:{name}','count':1 if gate else 3})
                self.assertEqual(entries[f'json:{source}']['status'],'in_progress')
                self.assertEqual((OLD/f'models/item/{name}.json').read_bytes(),(RES/f'assets/rustic/models/item/{name}.json').read_bytes())
                self.assertIn(f'rustic:{name}',json.loads((RES/'data/minecraft/tags/block/mineable/axe.json').read_text())['values'])
