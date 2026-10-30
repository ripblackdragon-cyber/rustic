"""Offline source/resource checks; not evidence of gameplay parity."""
import json
import re
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OLD = ROOT / 'original/rustic-pre-1.2.0/src/main'
RES = ROOT / 'src/main/resources'
NAMES = ('slate', 'slate_roof', 'slate_tile', 'slate_brick', 'slate_chiseled')

class SlateMigrationTests(unittest.TestCase):
    def test_blocks_models_loot_and_translations(self):
        source = (OLD / 'java/rustic/common/blocks/ModBlocks.java').read_text()
        port = (ROOT / 'src/main/java/rustic/fabric/RusticBlocks.java').read_text()
        self.assertIn('.useBlockDescriptionPrefix()', port)
        lang = json.loads((RES / 'assets/rustic/lang/en_us.json').read_text())
        tag = json.loads((RES / 'data/minecraft/tags/block/mineable/pickaxe.json').read_text())
        self.assertFalse(tag['replace'])
        self.assertEqual(set(tag['values']), {f'rustic:{n}' for n in NAMES})
        for name in NAMES:
            with self.subTest(name=name):
                self.assertIn(f'new BlockBase(Material.ROCK, "{name}").setHardness(2.0F)', source)
                for category in ('models/block', 'models/item'):
                    self.assertEqual((RES / f'assets/rustic/{category}/{name}.json').read_bytes(),
                                     (OLD / f'resources/assets/rustic/{category}/{name}.json').read_bytes())
                self.assertEqual(lang[f'block.rustic.{name}'], lang[f'tile.rustic.{name}.name'])
                loot = json.loads((RES / f'data/rustic/loot_table/blocks/{name}.json').read_text())
                self.assertEqual(loot['pools'][0]['entries'][0]['name'], f'rustic:{name}')
                state = json.loads((RES / f'assets/rustic/blockstates/{name}.json').read_text())
                self.assertEqual(state['variants']['']['model'], f'rustic:block/{name}')

    def test_recipes_and_provenance(self):
        source = (OLD / 'java/rustic/common/crafting/Recipes.java').read_text().splitlines()
        manifest = json.loads((ROOT / 'audit/recipe-parity.json').read_text())
        entries = {e['id']: e for e in manifest['entries']}
        cases = [(313,'slate_roof','slate',4,['SS','SS']),
                 (323,'slate_tile','slate',1,['S']), (325,'slate_tile_1','slate_brick',1,['S']),
                 (327,'slate_brick','slate_tile',4,['SS','SS']),
                 (329,'slate_chiseled','slate_brick',4,['SS','SS'])]
        for line, name, ingredient, count, pattern in cases:
            with self.subTest(name=name):
                self.assertIn(f'"{name}"', source[line-1])
                data = json.loads((RES / f'data/rustic/recipe/{name}.json').read_text())
                self.assertEqual(data['type'], 'minecraft:crafting_shaped')
                self.assertEqual(data['key'], {'S':f'rustic:{ingredient}'})
                self.assertEqual(data['pattern'], pattern)
                self.assertEqual(data['result'], {'id':f'rustic:{name.removesuffix("_1")}', 'count':count})
                entry = entries[f'java:{line}']
                self.assertEqual(entry['status'], 'in_progress')
                self.assertEqual(entry['targets'], [f'src/main/resources/data/rustic/recipe/{name}.json'])
