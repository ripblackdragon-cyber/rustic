package rustic.fabric;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;

/** Food components from Rustic 1.2.0's ModItems; cultivation and uses are separate. */
final class RusticFoods {
    static final FoodProperties WILDBERRIES = new FoodProperties.Builder()
            .nutrition(2).saturationModifier(0.5F).build();
    static final FoodProperties GRAPES = new FoodProperties.Builder()
            .nutrition(3).saturationModifier(0.3F).build();
    // Both original foods override getMaxItemUseDuration to 16 ticks (0.8 seconds).
    static final Consumable QUICK_FOOD = Consumables.defaultFood().consumeSeconds(0.8F).build();

    private RusticFoods() {
    }
}
