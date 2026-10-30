package rustic.fabric;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

/** The original farming tab, currently containing only the items already migrated. */
public final class RusticCreativeTabs {
    public static final ResourceKey<CreativeModeTab> FARMING_KEY =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, RusticFabric.id("farming"));
    public static final CreativeModeTab FARMING = FabricCreativeModeTab.builder()
            .title(Component.translatable("itemGroup.rustic.farming"))
            .icon(() -> new ItemStack(RusticItems.OLIVES))
            .displayItems((parameters, output) -> {
                output.accept(RusticItems.BEE);
                output.accept(RusticItems.HONEYCOMB);
                output.accept(RusticItems.BEESWAX);
                output.accept(RusticItems.TALLOW);
                output.accept(RusticItems.OLIVES);
                output.accept(RusticItems.IRON_DUST_TINY);
                output.accept(RusticItems.GOLD_DUST);
                output.accept(RusticItems.WILDBERRIES);
                output.accept(RusticItems.GRAPES);
            }).build();

    private RusticCreativeTabs() {
    }

    public static void initialize() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, FARMING_KEY, FARMING);
    }
}
