package rustic.fabric;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Consumable;

/** Initial ingredient registrations. Their uses and original creative tab remain incomplete. */
public final class RusticItems {
    public static final Item BEE = register("bee");
    public static final Item HONEYCOMB = register("honeycomb");
    public static final Item BEESWAX = register("beeswax");
    public static final Item TALLOW = register("tallow");
    public static final Item IRON_DUST_TINY = register("dust_tiny_iron");
    public static final Item GOLD_DUST = register("dust_gold");

    public static final Item CHILI_PEPPER = registerChili("chili_pepper");
    public static final Item OLIVES = registerFood("olives", RusticFoods.OLIVES, RusticFoods.OLIVES_CONSUMABLE);

    // ModItems.java:178-189: both have a 16-tick eating duration.
    public static final Item WILDBERRIES = registerFood("wildberries", RusticFoods.WILDBERRIES, RusticFoods.QUICK_FOOD);
    public static final Item GRAPES = registerFood("grapes", RusticFoods.GRAPES, RusticFoods.QUICK_FOOD);

    private RusticItems() {
    }

    private static Item register(String name) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, RusticFabric.id(name));
        return Registry.register(BuiltInRegistries.ITEM, key, new Item(new Item.Properties().setId(key)));
    }

    private static Item registerChili(String name) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, RusticFabric.id(name));
        return Registry.register(BuiltInRegistries.ITEM, key,
                new ChiliPepperItem(new Item.Properties().setId(key).food(RusticFoods.CHILI_PEPPER, RusticFoods.NORMAL_FOOD)));
    }

    private static Item registerFood(String name, FoodProperties food, Consumable consumable) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, RusticFabric.id(name));
        Item.Properties properties = new Item.Properties().setId(key).food(food, consumable);
        return Registry.register(BuiltInRegistries.ITEM, key, new Item(properties));
    }

    public static void initialize() {
        // Accessing this class initializes its static item registrations.
    }
}
