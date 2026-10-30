package rustic.fabric;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** Partial slate family: world generation, variants and configuration remain pending. */
public final class RusticBlocks {
    public static final Block SLATE = register("slate");
    public static final Block SLATE_ROOF = register("slate_roof");
    public static final Block SLATE_TILE = register("slate_tile");
    public static final Block SLATE_BRICK = register("slate_brick");
    public static final Block SLATE_CHISELED = register("slate_chiseled");

    private RusticBlocks() {
    }

    private static Block register(String name) {
        var blockKey = ResourceKey.create(Registries.BLOCK, RusticFabric.id(name));
        var itemKey = ResourceKey.create(Registries.ITEM, RusticFabric.id(name));
        var block = new Block(BlockBehaviour.Properties.of().setId(blockKey)
                .strength(2.0F, 2.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());
        Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
        Registry.register(BuiltInRegistries.ITEM, itemKey,
                new BlockItem(block, new Item.Properties().setId(itemKey)));
        return block;
    }

    public static void initialize() {
        // Temporary: the original decor tab uses a vase icon that is not ported yet.
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS).register(output -> {
            output.accept(SLATE);
            output.accept(SLATE_ROOF);
            output.accept(SLATE_TILE);
            output.accept(SLATE_BRICK);
            output.accept(SLATE_CHISELED);
        });
    }
}
