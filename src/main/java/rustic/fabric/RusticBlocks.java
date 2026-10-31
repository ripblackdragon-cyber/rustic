package rustic.fabric;

import java.util.function.Function;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt;
import net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** Partial slate family: world generation, variants and configuration remain pending. */
public final class RusticBlocks {
    public static final Block SLATE = register("slate");
    public static final Block SLATE_ROOF = register("slate_roof");
    public static final Block SLATE_TILE = register("slate_tile");
    public static final Block SLATE_BRICK = register("slate_brick");
    public static final Block SLATE_CHISELED = register("slate_chiseled");

    public static final Block SLATE_PILLAR = register("slate_pillar", RotatedPillarBlock::new);
    public static final Block SLATE_ROOF_STAIRS = register("stairs_slate_roof", p -> new StairBlock(SLATE_ROOF.defaultBlockState(), p));
    public static final Block SLATE_BRICK_STAIRS = register("stairs_slate_brick", p -> new StairBlock(SLATE_BRICK.defaultBlockState(), p));
    public static final Block SLATE_ROOF_SLAB = register("slate_roof_slab", SlabBlock::new);
    public static final Block SLATE_BRICK_SLAB = register("slate_brick_slab", SlabBlock::new);

    public static final Block PAINTED_WOOD_WHITE = registerPainted("painted_wood_white");
    public static final Block PAINTED_WOOD_ORANGE = registerPainted("painted_wood_orange");
    public static final Block PAINTED_WOOD_MAGENTA = registerPainted("painted_wood_magenta");
    public static final Block PAINTED_WOOD_LIGHT_BLUE = registerPainted("painted_wood_light_blue");
    public static final Block PAINTED_WOOD_YELLOW = registerPainted("painted_wood_yellow");
    public static final Block PAINTED_WOOD_LIME = registerPainted("painted_wood_lime");
    public static final Block PAINTED_WOOD_PINK = registerPainted("painted_wood_pink");
    public static final Block PAINTED_WOOD_GRAY = registerPainted("painted_wood_gray");
    public static final Block PAINTED_WOOD_SILVER = registerPainted("painted_wood_silver");
    public static final Block PAINTED_WOOD_CYAN = registerPainted("painted_wood_cyan");
    public static final Block PAINTED_WOOD_PURPLE = registerPainted("painted_wood_purple");
    public static final Block PAINTED_WOOD_BLUE = registerPainted("painted_wood_blue");
    public static final Block PAINTED_WOOD_BROWN = registerPainted("painted_wood_brown");
    public static final Block PAINTED_WOOD_GREEN = registerPainted("painted_wood_green");
    public static final Block PAINTED_WOOD_RED = registerPainted("painted_wood_red");
    public static final Block PAINTED_WOOD_BLACK = registerPainted("painted_wood_black");

    public static final Block LOG_OLIVE = registerWood("log_olive", RotatedPillarBlock::new, true);
    public static final Block LOG_IRONWOOD = registerWood("log_ironwood", RotatedPillarBlock::new, true);
    public static final Block PLANKS_OLIVE = registerWood("planks_olive", Block::new, false);
    public static final Block PLANKS_IRONWOOD = registerWood("planks_ironwood", Block::new, false);

    public static final Block CLAY_WALL = registerClayWall("clay_wall", Block::new);
    public static final Block CLAY_WALL_CROSS = registerClayWall("clay_wall_cross", Block::new);
    public static final Block CLAY_WALL_DIAG = registerClayWall("clay_wall_diag", DiagonalClayWallBlock::new);

    private RusticBlocks() {
    }

    private static Block register(String name) {
        return register(name, Block::new);
    }

    private static Block register(String name, Function<BlockBehaviour.Properties, Block> factory) {
        var blockKey = ResourceKey.create(Registries.BLOCK, RusticFabric.id(name));
        var itemKey = ResourceKey.create(Registries.ITEM, RusticFabric.id(name));
        var block = factory.apply(BlockBehaviour.Properties.of().setId(blockKey)
                .strength(2.0F, 2.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());
        Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
        Registry.register(BuiltInRegistries.ITEM, itemKey,
                new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix()));
        return block;
    }

    private static Block registerPainted(String name) {
        var blockKey = ResourceKey.create(Registries.BLOCK, RusticFabric.id(name));
        var itemKey = ResourceKey.create(Registries.ITEM, RusticFabric.id(name));
        var block = new Block(BlockBehaviour.Properties.of().setId(blockKey)
                .strength(2.0F, 2.0F).sound(SoundType.WOOD));
        Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
        Registry.register(BuiltInRegistries.ITEM, itemKey,
                new BlockItem(block, woodFuel(new Item.Properties().setId(itemKey).useBlockDescriptionPrefix())));
        FlammableBlockRegistry.getDefaultInstance().add(block, 5, 20);
        return block;
    }

    private static Block registerWood(String name, Function<BlockBehaviour.Properties, Block> factory, boolean log) {
        var blockKey = ResourceKey.create(Registries.BLOCK, RusticFabric.id(name));
        var itemKey = ResourceKey.create(Registries.ITEM, RusticFabric.id(name));
        var block = factory.apply(BlockBehaviour.Properties.of().setId(blockKey)
                .strength(2.0F, log ? 2.0F : 3.0F).sound(SoundType.WOOD));
        Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
        Registry.register(BuiltInRegistries.ITEM, itemKey,
                new BlockItem(block, woodFuel(new Item.Properties().setId(itemKey).useBlockDescriptionPrefix())));
        FlammableBlockRegistry.getDefaultInstance().add(block, 5, log ? 5 : 20);
        return block;
    }

    private static Item.Properties woodFuel(Item.Properties properties) {
        return properties.component(DataComponents.COOKING_FUEL, new CookingFuel(
                ResolvableInt.fromKey(ResourceKey.create(Registries.CONTEXT_INT_PROVIDER,
                        Identifier.fromNamespaceAndPath("minecraft", "cooking/time_wood_blocks"))),
                ResolvableFloat.fromKey(ContextFloatProviders.COOKING_DEFAULT_SPEED_MULTIPLIER)));
    }

    private static Block registerClayWall(String name, Function<BlockBehaviour.Properties, Block> factory) {
        var blockKey = ResourceKey.create(Registries.BLOCK, RusticFabric.id(name));
        var itemKey = ResourceKey.create(Registries.ITEM, RusticFabric.id(name));
        var block = factory.apply(BlockBehaviour.Properties.of().setId(blockKey).strength(1.0F, 1.0F).sound(SoundType.GRAVEL));
        Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
        Registry.register(BuiltInRegistries.ITEM, itemKey,
                new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix()));
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
            output.accept(SLATE_PILLAR);
            output.accept(SLATE_ROOF_STAIRS);
            output.accept(SLATE_BRICK_STAIRS);
            output.accept(SLATE_ROOF_SLAB);
            output.accept(SLATE_BRICK_SLAB);
            output.accept(PAINTED_WOOD_WHITE);
            output.accept(PAINTED_WOOD_ORANGE);
            output.accept(PAINTED_WOOD_MAGENTA);
            output.accept(PAINTED_WOOD_LIGHT_BLUE);
            output.accept(PAINTED_WOOD_YELLOW);
            output.accept(PAINTED_WOOD_LIME);
            output.accept(PAINTED_WOOD_PINK);
            output.accept(PAINTED_WOOD_GRAY);
            output.accept(PAINTED_WOOD_SILVER);
            output.accept(PAINTED_WOOD_CYAN);
            output.accept(PAINTED_WOOD_PURPLE);
            output.accept(PAINTED_WOOD_BLUE);
            output.accept(PAINTED_WOOD_BROWN);
            output.accept(PAINTED_WOOD_GREEN);
            output.accept(PAINTED_WOOD_RED);
            output.accept(PAINTED_WOOD_BLACK);
            output.accept(PLANKS_OLIVE);
            output.accept(PLANKS_IRONWOOD);
            output.accept(CLAY_WALL);
            output.accept(CLAY_WALL_CROSS);
            output.accept(CLAY_WALL_DIAG);
        });
    }
}
