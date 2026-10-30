"""Static scaffold and asset checks; no Minecraft or Java compilation is performed."""
import hashlib
import json
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / 'src/main/resources/assets/rustic'


class FabricScaffoldTests(unittest.TestCase):
    def test_metadata_points_to_existing_entrypoint_and_versions(self):
        metadata = json.loads((ROOT / 'src/main/resources/fabric.mod.json').read_text(encoding='utf-8'))
        self.assertEqual(metadata['id'], 'rustic')
        self.assertEqual(metadata['version'], '${version}')
        self.assertEqual(metadata['depends']['minecraft'], '=26.3')
        self.assertEqual(metadata['depends']['java'], '>=25')
        for class_name in metadata['entrypoints']['main']:
            source = ROOT / 'src/main/java' / (class_name.replace('.', '/') + '.java')
            self.assertTrue(source.is_file(), source)

    def test_ingredient_definitions_models_and_original_textures(self):
        original_dir = ROOT / 'original/rustic-pre-1.2.0/src/main/resources/assets/rustic/textures/items'
        for name in ('bee', 'honeycomb', 'beeswax', 'tallow', 'dust_tiny_iron', 'dust_gold'):
            with self.subTest(item=name):
                item = json.loads((ASSETS / f'items/{name}.json').read_text(encoding='utf-8'))
                self.assertEqual(item['model'], {'type': 'minecraft:model', 'model': f'rustic:item/{name}'})
                model = json.loads((ASSETS / f'models/item/{name}.json').read_text(encoding='utf-8'))
                self.assertEqual(model['parent'], 'minecraft:item/generated')
                self.assertEqual(model['textures']['layer0'], f'rustic:items/{name}')
                self.assertEqual((ASSETS / f'textures/items/{name}.png').read_bytes(),
                                 (original_dir / f'{name}.png').read_bytes())

    def test_three_smelting_recipes_have_expected_static_fields(self):
        recipes = ROOT / 'src/main/resources/data/rustic/recipe'
        cases = [
            ('beeswax_from_honeycomb', 'rustic:honeycomb', 'rustic:beeswax', 0.3),
            ('iron_nugget_from_tiny_dust', 'rustic:dust_tiny_iron', 'minecraft:iron_nugget', 0.15),
            ('gold_ingot_from_dust', 'rustic:dust_gold', 'minecraft:gold_ingot', 0.5),
        ]
        for name, ingredient, result, experience in cases:
            with self.subTest(recipe=name):
                data = json.loads((recipes / f'{name}.json').read_text(encoding='utf-8'))
                self.assertEqual(data, {'type': 'minecraft:smelting', 'ingredient': ingredient,
                                        'result': {'id': result}, 'experience': experience,
                                        'cookingtime': 200})

    def test_ingredient_name_aliases_preserve_available_original_translations(self):
        for path in (ASSETS / 'lang').glob('*.json'):
            lang = json.loads(path.read_text(encoding='utf-8'))
            for name in ('bee', 'honeycomb', 'beeswax', 'tallow', 'dust_tiny_iron', 'dust_gold'):
                with self.subTest(locale=path.name, item=name):
                    old = f'item.rustic.{name}.name'
                    if old in lang:
                        self.assertEqual(lang[f'item.rustic.{name}'], lang[old])

    def test_wrapper_binary_and_distribution_checksum_pinned(self):
        wrapper = (ROOT / 'gradle/wrapper/gradle-wrapper.jar').read_bytes()
        self.assertEqual(hashlib.sha256(wrapper).hexdigest(),
                         '7a9ce74cff467ca1bf60a4fcd9f05185acceda4d0f382434d393e17864262c5d')
        props = (ROOT / 'gradle/wrapper/gradle-wrapper.properties').read_text(encoding='utf-8')
        self.assertIn('gradle-9.7.1-bin.zip', props)
        self.assertIn('distributionSha256Sum=acd53f1edaf02f1a8ff99879f8a34b302661a057d9b063ae9e35b552f804d20a', props)
        self.assertTrue((ROOT / 'gradlew').stat().st_mode & 0o111)


if __name__ == '__main__':
    unittest.main()
