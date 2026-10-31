"""Static traceability checks for three Rustic clay walls."""
import json
import unittest
from pathlib import Path
ROOT = Path(__file__).resolve().parents[1]
OLD = ROOT / 'original/rustic-pre-1.2.0/src/main'
RES = ROOT / 'src/main/resources'
NAMES = ('clay_wall', 'clay_wall_cross', 'clay_wall_diag')

class ClayWallTests(unittest.TestCase):
    def test_models_translations_and_recipes(self):
        entries = {e['id']:e for e in json.loads((ROOT / 'audit/recipe-parity.json').read_text())['entries']}
        lang = json.loads((RES / 'assets/rustic/lang/en_us.json').read_text())
        tag = json.loads((RES / 'data/minecraft/tags/block/mineable/shovel.json').read_text())
        self.assertEqual(set(tag['values']), {f'rustic:{n}' for n in NAMES})
        cases = [(333,'clay_wall',[' P ','PCP',' P '],'minecraft:clay',8),
                 (335,'clay_wall_cross',['P P',' C ','P P'],'rustic:clay_wall',1),
                 (337,'clay_wall_diag',['P  ',' C ','  P'],'rustic:clay_wall',1)]
        for line,name,pattern,center,count in cases:
            with self.subTest(name=name):
                for category in ('models/item','models/block'):
                    self.assertEqual((RES / f'assets/rustic/{category}/{name}.json').read_bytes(),
                                     (OLD / f'resources/assets/rustic/{category}/{name}.json').read_bytes())
                self.assertEqual(lang[f'block.rustic.{name}'], lang[f'tile.rustic.{name}.name'])
                self.assertEqual(entries[f'java:{line}']['targets'], [f'src/main/resources/data/rustic/recipe/{name}.json'])
                recipe=json.loads((RES / f'data/rustic/recipe/{name}.json').read_text())
                self.assertEqual(recipe['pattern'], pattern)
                self.assertEqual(recipe['key'], {'P':'#rustic:plank_wood','C':center})
                self.assertEqual(recipe['result'], {'id':f'rustic:{name}','count':count})
        state=json.loads((RES/'assets/rustic/blockstates/clay_wall_diag.json').read_text())
        self.assertEqual(set(state['variants']), {'facing=north','facing=south','facing=east','facing=west'})
