package rustic.fabric;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

/** Food components from Rustic 1.2.0's ModItems; cultivation and uses are separate. */
final class RusticFoods {
    static final FoodProperties WILDBERRIES = new FoodProperties.Builder()
            .nutrition(2).saturationModifier(0.5F).build();
    static final FoodProperties GRAPES = new FoodProperties.Builder()
            .nutrition(3).saturationModifier(0.3F).build();
    static final FoodProperties OLIVES = new FoodProperties.Builder()
            .nutrition(1).saturationModifier(0.4F).build();
    // Original: 95% chance of nausea II for 200 ticks, without particles/icon.
    static final Consumable OLIVES_CONSUMABLE = Consumables.defaultFood().consumeSeconds(1.2F)
            .onConsume(new ApplyStatusEffectsConsumeEffect(
                    new MobEffectInstance(MobEffects.NAUSEA, 200, 1, false, false, false), 0.95F))
            .build();
    static final FoodProperties CHILI_PEPPER = new FoodProperties.Builder()
            .nutrition(3).saturationModifier(0.4F).alwaysEdible().build();
    static final Consumable NORMAL_FOOD = Consumables.defaultFood().build();
    // Both original foods override getMaxItemUseDuration to 16 ticks (0.8 seconds).
    static final Consumable QUICK_FOOD = Consumables.defaultFood().consumeSeconds(0.8F).build();

    private RusticFoods() {
    }
}
