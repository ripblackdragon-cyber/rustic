"""Static checks for two food items against their 1.12.2 source, not gameplay tests."""
import json
import re
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ORIGINAL = ROOT / 'original/rustic-pre-1.2.0/src/main'
ASSETS = ROOT / 'src/main/resources/assets/rustic'
NEW_JAVA = ROOT / 'src/main/java/rustic/fabric/RusticItems.java'


class FoodMigrationTests(unittest.TestCase):
    def test_food_values_and_eating_time_are_backed_by_original(self):
        original = (ORIGINAL / 'java/rustic/common/items/ModItems.java').read_text()
        target = NEW_JAVA.read_text()
        for field, item_id, nutrition, saturation in (
            ('WILDBERRIES', 'wildberries', 2, '0.5'),
            ('GRAPES', 'grapes', 3, '0.3'),
        ):
            with self.subTest(item=item_id):
                pattern = (rf'{field} = new ItemFoodBase\("{item_id}", {nutrition}, '
                           rf'{re.escape(saturation)}F, false\)\s*\{{'
                           r'.*?getMaxItemUseDuration\(ItemStack stack\)\s*\{\s*return 16;')
                self.assertRegex(original, re.compile(pattern, re.DOTALL))
                self.assertIn(f'{field} = registerFood("{item_id}", {nutrition}, {saturation}F)', target)
                self.assertIn(f'output.accept({field})', target)
        self.assertIn('.nutrition(nutrition).saturationModifier(saturationModifier)', target)
        self.assertIn('Consumables.defaultFood().consumeSeconds(0.8F).build()', target)
        self.assertNotIn('.alwaysEdible()', target)

    def test_original_model_texture_and_translations(self):
        for name in ('wildberries', 'grapes'):
            with self.subTest(item=name):
                original_model = json.loads((ORIGINAL / f'resources/assets/rustic/models/item/{name}.json').read_text())
                model = json.loads((ASSETS / f'models/item/{name}.json').read_text())
                self.assertEqual(original_model['parent'], 'item/generated')
                self.assertEqual(model['parent'], 'minecraft:item/generated')
                self.assertEqual(original_model['textures'], model['textures'])
                old_tex = ORIGINAL / f'resources/assets/rustic/textures/items/{name}.png'
                self.assertEqual(old_tex.read_bytes(), (ASSETS / f'textures/items/{name}.png').read_bytes())
                for locale in (ASSETS / 'lang').glob('*.json'):
                    values = json.loads(locale.read_text())
                    old = f'item.rustic.{name}.name'
                    if old in values:
                        self.assertEqual(values[f'item.rustic.{name}'], values[old])


if __name__ == '__main__':
    unittest.main()
