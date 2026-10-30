package rustic.fabric;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

/** Initial ingredient registrations. Their uses and original creative tab remain incomplete. */
public final class RusticItems {
    public static final Item BEE = register("bee");
    public static final Item HONEYCOMB = register("honeycomb");
    public static final Item BEESWAX = register("beeswax");
    public static final Item TALLOW = register("tallow");
    public static final Item IRON_DUST_TINY = register("dust_tiny_iron");
    public static final Item GOLD_DUST = register("dust_gold");

    // ModItems.java:178-189: both have a 16-tick eating duration.
    public static final Item WILDBERRIES = registerFood("wildberries", RusticFoods.WILDBERRIES);
    public static final Item GRAPES = registerFood("grapes", RusticFoods.GRAPES);

    private RusticItems() {
    }

    private static Item register(String name) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, RusticFabric.id(name));
        return Registry.register(BuiltInRegistries.ITEM, key, new Item(new Item.Properties().setId(key)));
    }

    private static Item registerFood(String name, FoodProperties food) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, RusticFabric.id(name));
        Item.Properties properties = new Item.Properties().setId(key).food(food, RusticFoods.QUICK_FOOD);
        return Registry.register(BuiltInRegistries.ITEM, key, new Item(properties));
    }

    public static void initialize() {
        // The original farming tab and olive icon have not yet been migrated.
        // This visibility is a development-only deviation, tracked in PORT_PROGRESS.md.
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(output -> {
            output.accept(BEE);
            output.accept(HONEYCOMB);
            output.accept(BEESWAX);
            output.accept(TALLOW);
            output.accept(IRON_DUST_TINY);
            output.accept(GOLD_DUST);
        });
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(output -> {
            output.accept(WILDBERRIES);
            output.accept(GRAPES);
        });
    }
}
