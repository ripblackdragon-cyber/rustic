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
        tabs = (NEW_JAVA.parent / 'RusticCreativeTabs.java').read_text()
        components = (NEW_JAVA.parent / 'RusticFoods.java').read_text()
        for field, item_id, nutrition, saturation in (
            ('WILDBERRIES', 'wildberries', 2, '0.5'),
            ('GRAPES', 'grapes', 3, '0.3'),
        ):
            with self.subTest(item=item_id):
                pattern = (rf'{field} = new ItemFoodBase\("{item_id}", {nutrition}, '
                           rf'{re.escape(saturation)}F, false\)\s*\{{'
                           r'.*?getMaxItemUseDuration\(ItemStack stack\)\s*\{\s*return 16;')
                self.assertRegex(original, re.compile(pattern, re.DOTALL))
                self.assertIn(f'{field} = registerFood("{item_id}", RusticFoods.{field}, RusticFoods.QUICK_FOOD)', target)
                self.assertRegex(components, rf'{field} = new FoodProperties.Builder\(\)\s*\.nutrition\({nutrition}\)\.saturationModifier\({re.escape(saturation)}F\)')
                self.assertIn(f'output.accept(RusticItems.{field})', tabs)
        self.assertIn('.food(food, consumable)', target)
        self.assertIn('Consumables.defaultFood().consumeSeconds(0.8F).build()', components)
        self.assertNotIn('.alwaysEdible()', components.split('static final FoodProperties CHILI_PEPPER')[0])

    def test_olives_match_original_declaration_and_creative_tab(self):
        original = (ORIGINAL / 'java/rustic/common/items/ModItems.java').read_text()
        food = (NEW_JAVA.parent / 'RusticFoods.java').read_text()
        tab = (NEW_JAVA.parent / 'RusticCreativeTabs.java').read_text()
        self.assertIn('new ItemFoodBase("olives", 1, 0.4F, false)', original)
        self.assertIn('new PotionEffect(MobEffects.NAUSEA, 200, 1, false, false), 0.95F', original)
        self.assertRegex(original, re.compile(r'OLIVES = new ItemFoodBase.*?return 24;', re.DOTALL))
        self.assertIn('new MobEffectInstance(MobEffects.NAUSEA, 200, 1, false, false, false), 0.95F', food)
        self.assertIn('consumeSeconds(1.2F)', food)
        self.assertIn('itemGroup.rustic.farming', tab)
        self.assertIn('new ItemStack(RusticItems.OLIVES)', tab)
        self.assertIn('output.accept(RusticItems.OLIVES)', tab)

    def test_chili_has_original_server_side_effects(self):
        original = (ORIGINAL / 'java/rustic/common/items/ModItems.java').read_text()
        chili = (NEW_JAVA.parent / 'ChiliPepperItem.java').read_text()
        self.assertIn('new ItemFoodBase("chili_pepper", 3, 0.4F, false)', original)
        self.assertIn('new PotionEffect(MobEffects.SPEED, 400)', original)
        self.assertIn('rand.nextInt(24) == 0', original)
        self.assertIn('DamageSource.ON_FIRE, 1.0F', original)
        self.assertIn('new MobEffectInstance(MobEffects.SPEED, 400)', chili)
        self.assertIn('random.nextInt(24) == 0', chili)
        self.assertIn('player.damageSources().onFire(), 1.0F', chili)
        self.assertIn('level instanceof ServerLevel', chili)

    def test_original_model_texture_and_translations(self):
        for name in ('wildberries', 'grapes', 'olives', 'chili_pepper'):
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
