"""Trace painted wood resources and recipes back to Rustic 1.2.0."""
import json
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OLD = ROOT / 'original/rustic-pre-1.2.0/src/main'
RES = ROOT / 'src/main/resources'
COLORS = ('white','orange','magenta','light_blue','yellow','lime','pink','gray','silver','cyan','purple','blue','brown','green','red','black')
LINES = (385,387,389,391,393,395,397,399,401,403,405,407,409,411,413,415)

class PaintedWoodTests(unittest.TestCase):
    def test_assets_and_recipe_mappings(self):
        recipes_source = (OLD / 'java/rustic/common/crafting/Recipes.java').read_text().splitlines()
        blocks_source = (OLD / 'java/rustic/common/blocks/ModBlocks.java').read_text()
        manifest = json.loads((ROOT / 'audit/recipe-parity.json').read_text())
        entries = {e['id']:e for e in manifest['entries']}
        tag = json.loads((RES / 'data/minecraft/tags/block/mineable/axe.json').read_text())
        self.assertEqual(set(tag['values']), {f'rustic:painted_wood_{c}' for c in COLORS})
        plank = json.loads((RES / 'data/rustic/tags/item/plank_wood.json').read_text())
        self.assertIn('#minecraft:planks', plank['values'])
        for color, line in zip(COLORS, LINES):
            with self.subTest(color=color):
                name = f'painted_wood_{color}'
                self.assertIn(f'"{name}"', blocks_source)
                self.assertIn(f'PAINTED_WOOD_{color.upper()}, 8', recipes_source[line-1])
                entry = entries[f'java:{line}']
                self.assertEqual(entry['status'], 'in_progress')
                self.assertEqual(entry['targets'], [f'src/main/resources/data/rustic/recipe/{name}.json'])
                for category in ('models/item','models/block'):
                    self.assertEqual((RES / f'assets/rustic/{category}/{name}.json').read_bytes(),
                                     (OLD / f'resources/assets/rustic/{category}/{name}.json').read_bytes())
                self.assertEqual((RES / f'assets/rustic/textures/blocks/{name}.png').read_bytes(),
                                 (OLD / f'resources/assets/rustic/textures/blocks/{name}.png').read_bytes())
                lang = json.loads((RES / 'assets/rustic/lang/en_us.json').read_text())
                self.assertEqual(lang[f'block.rustic.{name}'], lang[f'tile.rustic.{name}.name'])
                recipe = json.loads((RES / f'data/rustic/recipe/{name}.json').read_text())
                self.assertEqual(recipe['pattern'], ['PPP','PDP','PPP'])
                self.assertEqual(recipe['key'], {'P':'#rustic:plank_wood', 'D':f'#rustic:dyes/{color}'})
                self.assertEqual(recipe['result'], {'id':f'rustic:{name}', 'count':8})
                self.assertIn(f'rustic:{name}', plank['values'])
        for color, item in [('white','bone_meal'),('black','ink_sac'),('blue','lapis_lazuli'),('brown','cocoa_beans')]:
            tag = json.loads((RES / f'data/rustic/tags/item/dyes/{color}.json').read_text())
            self.assertIn(f'minecraft:{item}', tag['values'])
