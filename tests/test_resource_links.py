"""Offline cross-reference checks. They do not replace a Minecraft/Fabric run."""
import json
import re
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / 'src/main/resources/assets/rustic'
RECIPE_DIR = ROOT / 'src/main/resources/data/rustic/recipe'
JAVA = ROOT / 'src/main/java/rustic/fabric/RusticItems.java'
ORIGINAL_RECIPES = ROOT / 'original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java'


def registered_item_ids():
    source = JAVA.read_text(encoding='utf-8')
    return re.findall(r'\bregister(?:Food|Chili)?\("([a-z0-9_]+)"', source)


class ResourceLinkTests(unittest.TestCase):
    def test_registered_items_have_unique_ids_and_exact_item_definitions(self):
        item_ids = registered_item_ids()
        self.assertEqual(len(item_ids), len(set(item_ids)), 'duplicate registration IDs')
        definitions = {p.stem for p in (ASSETS / 'items').glob('*.json')}
        block_ids = set(re.findall(r'\bregister(?:Painted|ClayWall)?\("([a-z0-9_]+)"', (JAVA.parent / 'RusticBlocks.java').read_text()))
        self.assertEqual(set(item_ids) | block_ids, definitions)
        self.assertFalse(set(item_ids) & block_ids)
        for name in item_ids:
            with self.subTest(item=name):
                item = json.loads((ASSETS / 'items' / f'{name}.json').read_text(encoding='utf-8'))
                self.assertEqual(item['model']['type'], 'minecraft:model')
                self.assertEqual(item['model']['model'], f'rustic:item/{name}')
                model = json.loads((ASSETS / 'models/item' / f'{name}.json').read_text(encoding='utf-8'))
                self.assertEqual(model['parent'], 'minecraft:item/generated')
                texture = model['textures']['layer0']
                self.assertTrue(texture.startswith('rustic:'))
                self.assertTrue((ASSETS / 'textures' / f'{texture.removeprefix("rustic:")}.png').is_file())

    def test_registered_items_have_en_us_fallback_and_preserved_translation(self):
        lang = json.loads((ASSETS / 'lang/en_us.json').read_text(encoding='utf-8'))
        for name in registered_item_ids():
            with self.subTest(item=name):
                self.assertTrue(lang[f'item.rustic.{name}'])
                self.assertEqual(lang[f'item.rustic.{name}'], lang[f'item.rustic.{name}.name'])

    def test_all_rustic_smelting_inputs_and_outputs_are_registered(self):
        item_ids = {f'rustic:{name}' for name in registered_item_ids()}
        recipes = list(RECIPE_DIR.glob('*.json'))
        self.assertTrue(recipes)
        for path in recipes:
            with self.subTest(recipe=path.name):
                data = json.loads(path.read_text(encoding='utf-8'))
                if data['type'] == 'minecraft:crafting_shaped':
                    continue  # independently checked by test_slate_migration.py
                self.assertEqual(data['type'], 'minecraft:smelting')
                self.assertEqual(data['cookingtime'], 200)
                self.assertIn(data['ingredient'], item_ids)
                result = data['result']['id']
                if result.startswith('rustic:'):
                    self.assertIn(result, item_ids)
                else:
                    # These two vanilla IDs were cross-checked with 26.3 data;
                    # this is not a full registry or a game loader test.
                    self.assertIn(result, {'minecraft:iron_nugget', 'minecraft:gold_ingot'})

    def test_mapped_recipes_point_to_original_smelting_calls(self):
        original = ORIGINAL_RECIPES.read_text(encoding='utf-8').splitlines()
        manifest = json.loads((ROOT / 'audit/recipe-parity.json').read_text(encoding='utf-8'))
        entries = {entry['id']: entry for entry in manifest['entries']}
        cases = [
            (176, 'HONEYCOMB', 'BEESWAX', '0.3F', 'beeswax_from_honeycomb.json'),
            (183, 'IRON_DUST_TINY', 'IRON_NUGGET', '0.15F', 'iron_nugget_from_tiny_dust.json'),
            (184, 'GOLD_DUST', 'GOLD_INGOT', '0.5F', 'gold_ingot_from_dust.json'),
        ]
        for line, input_name, output_name, xp, target in cases:
            with self.subTest(line=line):
                call = original[line - 1]
                self.assertIn('GameRegistry.addSmelting', call)
                self.assertIn(f'new ItemStack(ModItems.{input_name})', call)
                self.assertRegex(call, rf'new ItemStack\((?:ModItems|Items)\.{output_name}\)')
                self.assertIn(xp, call)
                entry = entries[f'java:{line}']
                self.assertEqual(entry['status'], 'in_progress')
                self.assertEqual(entry['targets'], [f'src/main/resources/data/rustic/recipe/{target}'])
                self.assertTrue((RECIPE_DIR / target).is_file())


if __name__ == '__main__':
    unittest.main()
