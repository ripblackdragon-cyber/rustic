package rustic.fabric.gametest;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Set;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Path;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestDedicatedServerConnection;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestDedicatedServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerConnection;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import rustic.fabric.RusticBlocks;
import rustic.fabric.RusticCreativeTabs;
import rustic.fabric.RusticItems;

/** Drives a real local client and its integrated server, without external accounts. */
public final class RusticClientGameTest implements FabricClientGameTest {
    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext world = context.worldBuilder().create()) {
            TestServerConnection connection = world.getConnection();
            connection.waitForChunksRender();
            world.getServer().runCommand("gamemode survival @a");
            context.getInput().lookAt(0.0F, -90.0F);
            checkFood(context, world.getServer(), connection, RusticItems.WILDBERRIES, 12, 2.0F);
            checkFoodAtFullHunger(context, world.getServer(), connection, RusticItems.WILDBERRIES);
            checkFood(context, world.getServer(), connection, RusticItems.GRAPES, 13, 1.8F);
            checkFoodAtFullHunger(context, world.getServer(), connection, RusticItems.GRAPES);
            checkFood(context, world.getServer(), connection, RusticItems.OLIVES, 11, 0.8F);
            checkFoodAtFullHunger(context, world.getServer(), connection, RusticItems.OLIVES);
            checkFood(context, world.getServer(), connection, RusticItems.CHILI_PEPPER, 13, 2.4F, 40);
            checkChiliAtFullHunger(context, world.getServer(), connection);
            context.takeScreenshot("rustic-olives-after-eating");
            checkBlockPlacementAndMining(context, world.getServer(), connection, RusticBlocks.SLATE, Items.WOODEN_PICKAXE);
            checkBlockPlacementAndMining(context, world.getServer(), connection, RusticBlocks.PAINTED_WOOD_SILVER, Items.WOODEN_AXE);
            checkBlockPlacementAndMining(context, world.getServer(), connection, RusticBlocks.CLAY_WALL_DIAG, Items.WOODEN_SHOVEL);
            checkBlockPlacementAndMining(context, world.getServer(), connection, RusticBlocks.LOG_OLIVE, Items.WOODEN_AXE);
            checkGateInteraction(context, world.getServer(), connection, RusticBlocks.OLIVE_FENCE_GATE);
            checkDoorInteraction(context, world.getServer(), connection, RusticBlocks.OLIVE_DOOR);
            checkDoorInteraction(context, world.getServer(), connection, RusticBlocks.IRONWOOD_DOOR);
            checkSlabMerging(context, world.getServer(), connection, RusticBlocks.SLATE_ROOF_SLAB, Items.WOODEN_PICKAXE);
            checkSlabMerging(context, world.getServer(), connection, RusticBlocks.SLATE_BRICK_SLAB, Items.WOODEN_PICKAXE);
            checkSlabMerging(context, world.getServer(), connection, RusticBlocks.OLIVE_SLAB, Items.WOODEN_AXE);
            checkSlabMerging(context, world.getServer(), connection, RusticBlocks.IRONWOOD_SLAB, Items.WOODEN_AXE);
            checkCreativeTab(context, world.getServer());
        }
        checkNaturalSlateAndSave(context);
        boolean dedicated = Boolean.getBoolean("rustic.testDedicatedServer");
        if (dedicated) {
            checkDedicatedServer(context);
        }
        try {
            Path report = Path.of(System.getProperty("rustic.client.report"));
            Files.createDirectories(report.getParent());
            Files.writeString(report, dedicated ? "rustic-client-dedicated-gametest-passed\n" : "rustic-client-food-gametest-passed\n");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Search naturally generated terrain, then reload the save and verify the same block persisted. */
    private void checkNaturalSlateAndSave(ClientGameTestContext context) {
        net.fabricmc.fabric.api.client.gametest.v1.world.TestWorldSave save;
        BlockPos found;
        try (TestSingleplayerContext world = context.worldBuilder().setUseConsistentSettings(false).create()) {
            save = world.getWorldSave();
            found = world.getServer().computeOnServer(server -> {
                var level = server.overworld();
                var spawn = world.getConnection().getServerPlayer().blockPosition();
                // Scan a small area of already generated normal terrain around spawn.
                for (int x = spawn.getX() - 24; x <= spawn.getX() + 24; x++) {
                    for (int z = spawn.getZ() - 24; z <= spawn.getZ() + 24; z++) {
                        for (int y = 1; y <= 90; y++) {
                            BlockPos pos = new BlockPos(x, y, z);
                            if (level.getBlockState(pos).is(RusticBlocks.SLATE)) return pos;
                        }
                    }
                }
                throw new AssertionError("No slate found in naturally generated terrain near spawn");
            });
        }
        try (TestSingleplayerContext reopened = save.open()) {
            boolean persisted = reopened.getServer().computeOnServer(server ->
                    server.overworld().getBlockState(found).is(RusticBlocks.SLATE));
            if (!persisted) throw new AssertionError("Naturally generated slate did not survive world reload");
        }
    }

    private void checkBlockPlacementAndMining(ClientGameTestContext context, TestServerContext server,
                                              TestServerConnection connection, Block block, Item tool) {
        BlockPos target = server.computeOnServer(minecraftServer -> {
            var player = connection.getServerPlayer();
            var level = player.level();
            BlockPos pos = player.blockPosition().east(2);
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            level.setBlockAndUpdate(pos.above(), Blocks.AIR.defaultBlockState());
            level.setBlockAndUpdate(pos.below(), Blocks.STONE.defaultBlockState());
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(block));
            return pos;
        });
        context.waitFor(client -> client.player.getMainHandItem().is(block.asItem())
                && client.level.getBlockState(target.below()).is(Blocks.STONE));
        context.getInput().lookAt(target.below());
        context.getInput().pressKey(options -> options.keyUse);
        connection.waitForServerboundPackets();
        server.waitFor(minecraftServer -> connection.getServerPlayer().level().getBlockState(target).is(block));
        context.waitFor(client -> client.level.getBlockState(target).is(block));
        context.takeScreenshot("rustic-placed-" + net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block).getPath());
        server.runOnServer(minecraftServer -> connection.getServerPlayer()
                .setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(tool)));
        context.waitFor(client -> client.player.getMainHandItem().is(tool));
        context.getInput().lookAt(target);
        context.getInput().holdKeyFor(options -> options.keyAttack, 80);
        connection.waitForServerboundPackets();
        server.waitFor(minecraftServer -> {
            var player = connection.getServerPlayer();
            var level = player.level();
            boolean dropped = !level.getEntitiesOfClass(ItemEntity.class, new AABB(target).inflate(3),
                    entity -> entity.getItem().is(block.asItem())).isEmpty();
            return level.getBlockState(target).isAir()
                    && (dropped || player.getInventory().contains(new ItemStack(block)));
        });
    }

    private void checkDoorInteraction(ClientGameTestContext context, TestServerContext server,
                                      TestServerConnection connection, Block door) {
        BlockPos pos = server.computeOnServer(minecraftServer -> {
            var player = connection.getServerPlayer();
            var level = player.level();
            var target = player.blockPosition().east(2);
            level.setBlockAndUpdate(target, Blocks.AIR.defaultBlockState());
            level.setBlockAndUpdate(target.above(), Blocks.AIR.defaultBlockState());
            level.setBlockAndUpdate(target.below(), Blocks.STONE.defaultBlockState());
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(door));
            return target;
        });
        context.waitFor(client -> client.player.getMainHandItem().is(door.asItem())
                && client.level.getBlockState(pos.below()).is(Blocks.STONE));
        context.getInput().lookAt(pos.below());
        context.getInput().pressKey(options -> options.keyUse);
        connection.waitForServerboundPackets();
        server.waitFor(minecraftServer -> {
            var level = connection.getServerPlayer().level();
            return level.getBlockState(pos).is(door) && level.getBlockState(pos.above()).is(door)
                    && level.getBlockState(pos).getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER
                    && level.getBlockState(pos.above()).getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER;
        });
        server.runOnServer(minecraftServer -> connection.getServerPlayer().setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY));
        context.waitFor(client -> client.level.getBlockState(pos).is(door)
                && client.level.getBlockState(pos.above()).is(door) && client.player.getMainHandItem().isEmpty());
        context.waitTicks(4);
        context.getInput().lookAt(pos);
        context.getInput().pressKey(options -> options.keyUse);
        connection.waitForServerboundPackets();
        server.waitFor(minecraftServer -> connection.getServerPlayer().level().getBlockState(pos).getValue(DoorBlock.OPEN));
        context.waitFor(client -> client.level.getBlockState(pos).getValue(DoorBlock.OPEN));
        context.waitTicks(4);
        context.getInput().pressKey(options -> options.keyUse);
        connection.waitForServerboundPackets();
        server.waitFor(minecraftServer -> !connection.getServerPlayer().level().getBlockState(pos).getValue(DoorBlock.OPEN));
        context.waitFor(client -> !client.level.getBlockState(pos).getValue(DoorBlock.OPEN));
    }

    private void checkGateInteraction(ClientGameTestContext context, TestServerContext server,
                                      TestServerConnection connection, Block gate) {
        BlockPos pos = server.computeOnServer(minecraftServer -> {
            var player = connection.getServerPlayer();
            var level = player.level();
            var target = player.blockPosition().east(2);
            level.setBlockAndUpdate(target, Blocks.AIR.defaultBlockState());
            level.setBlockAndUpdate(target.above(), Blocks.AIR.defaultBlockState());
            level.setBlockAndUpdate(target.below(), Blocks.STONE.defaultBlockState());
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(gate));
            return target;
        });
        context.waitFor(client -> client.player.getMainHandItem().is(gate.asItem())
                && client.level.getBlockState(pos.below()).is(Blocks.STONE));
        context.getInput().lookAt(pos.below());
        context.getInput().pressKey(options -> options.keyUse);
        connection.waitForServerboundPackets();
        server.waitFor(minecraftServer -> connection.getServerPlayer().level().getBlockState(pos).is(gate));
        server.runOnServer(minecraftServer -> connection.getServerPlayer().setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY));
        context.waitFor(client -> client.level.getBlockState(pos).is(gate) && client.player.getMainHandItem().isEmpty());
        context.waitTicks(4);
        context.getInput().lookAt(pos);
        context.getInput().pressKey(options -> options.keyUse);
        connection.waitForServerboundPackets();
        server.waitFor(minecraftServer -> connection.getServerPlayer().level().getBlockState(pos).getValue(FenceGateBlock.OPEN));
        context.waitFor(client -> client.level.getBlockState(pos).getValue(FenceGateBlock.OPEN));
        context.waitTicks(4);
        context.getInput().pressKey(options -> options.keyUse);
        connection.waitForServerboundPackets();
        server.waitFor(minecraftServer -> !connection.getServerPlayer().level().getBlockState(pos).getValue(FenceGateBlock.OPEN));
        context.waitFor(client -> !client.level.getBlockState(pos).getValue(FenceGateBlock.OPEN));
    }

    private void checkSlabMerging(ClientGameTestContext context, TestServerContext server,
                                  TestServerConnection connection, Block slab, Item tool) {
        BlockPos target = server.computeOnServer(minecraftServer -> {
            var player = connection.getServerPlayer();
            var level = player.level();
            BlockPos pos = player.blockPosition().east(2);
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            level.setBlockAndUpdate(pos.above(), Blocks.AIR.defaultBlockState());
            level.setBlockAndUpdate(pos.below(), Blocks.STONE.defaultBlockState());
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(slab, 2));
            return pos;
        });
        context.waitFor(client -> client.player.getMainHandItem().is(slab.asItem())
                && client.level.getBlockState(target.below()).is(Blocks.STONE)
                && client.level.getBlockState(target).isAir());
        context.getInput().lookAt(target.below());
        context.getInput().pressKey(options -> options.keyUse);
        connection.waitForServerboundPackets();
        server.waitFor(minecraftServer -> {
            var state = connection.getServerPlayer().level().getBlockState(target);
            return state.is(slab) && state.getValue(SlabBlock.TYPE) == SlabType.BOTTOM;
        });
        context.waitFor(client -> client.level.getBlockState(target).is(slab)
                && client.player.getMainHandItem().getCount() == 1);
        context.waitTicks(4);
        context.getInput().lookAt(target);
        context.getInput().pressKey(options -> options.keyUse);
        connection.waitForServerboundPackets();
        server.waitFor(minecraftServer -> {
            var state = connection.getServerPlayer().level().getBlockState(target);
            return state.is(slab) && state.getValue(SlabBlock.TYPE) == SlabType.DOUBLE;
        });
        context.waitFor(client -> client.level.getBlockState(target).is(slab)
                && client.level.getBlockState(target).getValue(SlabBlock.TYPE) == SlabType.DOUBLE);
        server.runOnServer(minecraftServer -> connection.getServerPlayer()
                .setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(tool)));
        context.waitFor(client -> client.player.getMainHandItem().is(tool));
        context.getInput().lookAt(target);
        context.getInput().holdKeyFor(options -> options.keyAttack, 80);
        connection.waitForServerboundPackets();
        server.waitFor(minecraftServer -> {
            var player = connection.getServerPlayer();
            var level = player.level();
            int drops = level.getEntitiesOfClass(ItemEntity.class, new AABB(target).inflate(3),
                    entity -> entity.getItem().is(slab.asItem())).stream().mapToInt(entity -> entity.getItem().getCount()).sum();
            return level.getBlockState(target).isAir()
                    && drops + player.getInventory().countItem(slab.asItem()) >= 2;
        });
    }

    private void checkCreativeTab(ClientGameTestContext context, TestServerContext server) {
        server.runCommand("gamemode creative @a");
        context.waitFor(client -> client.player.isCreative());
        context.getInput().pressKey(options -> options.keyInventory);
        context.waitForScreen(CreativeModeInventoryScreen.class);
        context.runOnClient(client -> {
            var tab = RusticCreativeTabs.FARMING;
            Set<Item> expected = Set.of(RusticItems.BEE, RusticItems.HONEYCOMB, RusticItems.BEESWAX,
                    RusticItems.TALLOW, RusticItems.OLIVES, RusticItems.IRON_DUST_TINY,
                    RusticItems.GOLD_DUST, RusticItems.CHILI_PEPPER, RusticItems.WILDBERRIES, RusticItems.GRAPES,
                    RusticBlocks.LOG_OLIVE.asItem(), RusticBlocks.LOG_IRONWOOD.asItem());
            Set<Item> displayed = tab.getDisplayItems().stream().map(ItemStack::getItem).collect(Collectors.toSet());
            var buildingTab = CreativeModeTabs.getDefaultTab();
            Item[] slateItems = {RusticBlocks.SLATE.asItem(), RusticBlocks.SLATE_ROOF.asItem(),
                    RusticBlocks.SLATE_TILE.asItem(), RusticBlocks.SLATE_BRICK.asItem(), RusticBlocks.SLATE_CHISELED.asItem(),
                    RusticBlocks.SLATE_PILLAR.asItem(), RusticBlocks.SLATE_ROOF_STAIRS.asItem(), RusticBlocks.SLATE_BRICK_STAIRS.asItem(),
                    RusticBlocks.SLATE_ROOF_SLAB.asItem(), RusticBlocks.SLATE_BRICK_SLAB.asItem()};
            String[] slateNames = {"Slate", "Slate Roof", "Slate Tile", "Slate Brick", "Chiseled Slate", "Slate Pillar",
                    "Slate Roof Stairs", "Slate Brick Stairs", "Slate Roof Slab", "Slate Brick Slab"};
            for (int i = 0; i < slateItems.length; i++) {
                ItemStack stack = new ItemStack(slateItems[i]);
                Item expectedItem = slateItems[i];
                if (buildingTab.getDisplayItems().stream().noneMatch(item -> item.is(expectedItem))
                        || !stack.getHoverName().getString().equals(slateNames[i])) {
                    throw new AssertionError("Slate item missing from building tab or not translated: " + stack.getHoverName());
                }
            }
            Item[] paintedItems = {RusticBlocks.PAINTED_WOOD_WHITE.asItem(), RusticBlocks.PAINTED_WOOD_ORANGE.asItem(), RusticBlocks.PAINTED_WOOD_MAGENTA.asItem(), RusticBlocks.PAINTED_WOOD_LIGHT_BLUE.asItem(), RusticBlocks.PAINTED_WOOD_YELLOW.asItem(), RusticBlocks.PAINTED_WOOD_LIME.asItem(), RusticBlocks.PAINTED_WOOD_PINK.asItem(), RusticBlocks.PAINTED_WOOD_GRAY.asItem(), RusticBlocks.PAINTED_WOOD_SILVER.asItem(), RusticBlocks.PAINTED_WOOD_CYAN.asItem(), RusticBlocks.PAINTED_WOOD_PURPLE.asItem(), RusticBlocks.PAINTED_WOOD_BLUE.asItem(), RusticBlocks.PAINTED_WOOD_BROWN.asItem(), RusticBlocks.PAINTED_WOOD_GREEN.asItem(), RusticBlocks.PAINTED_WOOD_RED.asItem(), RusticBlocks.PAINTED_WOOD_BLACK.asItem()};
            String[] paintedNames = {"White Painted Wood", "Orange Painted Wood", "Magenta Painted Wood", "Light Blue Painted Wood", "Yellow Painted Wood", "Lime Painted Wood", "Pink Painted Wood", "Gray Painted Wood", "Light Gray Painted Wood", "Cyan Painted Wood", "Purple Painted Wood", "Blue Painted Wood", "Brown Painted Wood", "Green Painted Wood", "Red Painted Wood", "Black Painted Wood"};
            for (int i = 0; i < paintedItems.length; i++) {
                Item expectedItem = paintedItems[i];
                ItemStack stack = new ItemStack(expectedItem);
                if (buildingTab.getDisplayItems().stream().noneMatch(item -> item.is(expectedItem))
                        || !stack.getHoverName().getString().equals(paintedNames[i])) {
                    throw new AssertionError("Painted wood missing or untranslated: " + stack.getHoverName());
                }
            }
            Item[] clayItems = {RusticBlocks.CLAY_WALL.asItem(), RusticBlocks.CLAY_WALL_CROSS.asItem(), RusticBlocks.CLAY_WALL_DIAG.asItem()};
            String[] clayNames = {"Clay Wall", "Crossed Clay Wall", "Diagonal Clay Wall"};
            for (int i = 0; i < clayItems.length; i++) {
                Item expectedItem = clayItems[i];
                ItemStack stack = new ItemStack(expectedItem);
                if (buildingTab.getDisplayItems().stream().noneMatch(item -> item.is(expectedItem))
                        || !stack.getHoverName().getString().equals(clayNames[i])) {
                    throw new AssertionError("Clay wall missing or untranslated: " + stack.getHoverName());
                }
            }
            Item[] woodItems = {RusticBlocks.LOG_OLIVE.asItem(), RusticBlocks.LOG_IRONWOOD.asItem(), RusticBlocks.PLANKS_OLIVE.asItem(), RusticBlocks.PLANKS_IRONWOOD.asItem()};
            String[] woodNames = {"Olive Wood", "Ironwood", "Olive Wood Planks", "Ironwood Planks"};
            for (int i = 0; i < woodItems.length; i++) {
                Item expectedItem = woodItems[i];
                ItemStack stack = new ItemStack(expectedItem);
                boolean inCorrectTab = i < 2 ? displayed.contains(expectedItem)
                        : buildingTab.getDisplayItems().stream().anyMatch(item -> item.is(expectedItem));
                if (!inCorrectTab || !stack.getHoverName().getString().equals(woodNames[i])) {
                    throw new AssertionError("Wood missing or untranslated: " + stack.getHoverName());
                }
            }
            Item[] woodShapeItems = {RusticBlocks.OLIVE_SLAB.asItem(), RusticBlocks.IRONWOOD_SLAB.asItem(), RusticBlocks.OLIVE_STAIRS.asItem(), RusticBlocks.IRONWOOD_STAIRS.asItem()};
            String[] woodShapeNames = {"Olive Wood Slab", "Ironwood Slab", "Olive Wood Stairs", "Ironwood Stairs"};
            for (int i = 0; i < woodShapeItems.length; i++) {
                Item expectedItem = woodShapeItems[i];
                ItemStack stack = new ItemStack(expectedItem);
                if (buildingTab.getDisplayItems().stream().noneMatch(item -> item.is(expectedItem))
                        || !stack.getHoverName().getString().equals(woodShapeNames[i])) {
                    throw new AssertionError("Wood shape missing or untranslated: " + stack.getHoverName());
                }
            }
            Item[] doors = {RusticBlocks.OLIVE_DOOR.asItem(), RusticBlocks.IRONWOOD_DOOR.asItem()};
            String[] doorNames = {"Olive Door", "Ironwood Door"};
            for (int i = 0; i < doors.length; i++) {
                Item expectedItem = doors[i];
                ItemStack stack = new ItemStack(expectedItem);
                if (buildingTab.getDisplayItems().stream().noneMatch(item -> item.is(expectedItem))
                        || !stack.getHoverName().getString().equals(doorNames[i])) {
                    throw new AssertionError("Door missing or untranslated: " + stack.getHoverName());
                }
            }
            Item[] fences = {RusticBlocks.OLIVE_FENCE.asItem(), RusticBlocks.IRONWOOD_FENCE.asItem(), RusticBlocks.OLIVE_FENCE_GATE.asItem(), RusticBlocks.IRONWOOD_FENCE_GATE.asItem()};
            String[] fenceNames = {"Olive Wood Fence", "Ironwood Fence", "Olive Wood Fence Gate", "Ironwood Fence Gate"};
            for (int i = 0; i < fences.length; i++) {
                Item expectedItem = fences[i];
                ItemStack stack = new ItemStack(expectedItem);
                if (buildingTab.getDisplayItems().stream().noneMatch(item -> item.is(expectedItem))
                        || !stack.getHoverName().getString().equals(fenceNames[i])) {
                    throw new AssertionError("Fence missing or untranslated: " + stack.getHoverName());
                }
            }
            if (!displayed.equals(expected) || tab.getDisplayItems().size() != expected.size()
                    || !tab.getIconItem().is(RusticItems.OLIVES)
                    || !tab.getDisplayName().getString().equals("Rustic Agriculture")) {
                throw new AssertionError("Partial farming tab contents, title or icon differ: " + displayed);
            }
        });
        context.takeScreenshot("rustic-creative-inventory");
        context.setScreen(() -> null);
    }

    private void checkFood(ClientGameTestContext context, TestServerContext serverContext,
                           TestServerConnection connection, Item food, int expectedHunger, float expectedSaturation) {
        checkFood(context, serverContext, connection, food, expectedHunger, expectedSaturation, 25);
    }

    private void checkFood(ClientGameTestContext context, TestServerContext serverContext,
                           TestServerConnection connection, Item food, int expectedHunger, float expectedSaturation, int ticks) {
        // A previously tested interactive block (such as a fence gate) can intercept use.
        context.getInput().lookAt(0.0F, -90.0F);
        serverContext.runOnServer(server -> {
            var player = connection.getServerPlayer();
            player.getFoodData().setFoodLevel(10);
            player.getFoodData().setSaturation(0.0F);
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(food, 2));
        });
        context.waitFor(client -> client.player.getMainHandItem().is(food)
                && client.player.getMainHandItem().getCount() == 2
                && client.player.getFoodData().getFoodLevel() == 10);
        // Releasing halfway must not consume anything.
        context.getInput().holdKeyFor(options -> options.keyUse, 8);
        connection.waitForServerboundPackets();
        serverContext.runOnServer(server -> {
            var player = connection.getServerPlayer();
            if (player.getMainHandItem().getCount() != 2 || player.getFoodData().getFoodLevel() != 10) {
                throw new AssertionError("Interrupted consumption changed server state for " + food);
            }
        });
        context.waitTicks(2);
        context.getInput().holdKeyFor(options -> options.keyUse, ticks);
        connection.waitForServerboundPackets();
        context.waitFor(client -> client.player.getMainHandItem().is(food)
                && client.player.getMainHandItem().getCount() == 1);
        serverContext.runOnServer(server -> {
            var player = connection.getServerPlayer();
            if (player.getFoodData().getFoodLevel() != expectedHunger
                    || Math.abs(player.getFoodData().getSaturationLevel() - expectedSaturation) > 0.0001F
                    || player.getMainHandItem().getCount() != 1) {
                throw new AssertionError("Unexpected server state after consuming " + food);
            }
        });
    }

    private void checkChiliAtFullHunger(ClientGameTestContext context, TestServerContext server,
                                        TestServerConnection connection) {
        context.getInput().lookAt(0.0F, -90.0F);
        server.runOnServer(minecraftServer -> {
            var player = connection.getServerPlayer();
            player.removeEffect(net.minecraft.world.effect.MobEffects.SPEED);
            player.getFoodData().setFoodLevel(20);
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(RusticItems.CHILI_PEPPER, 2));
        });
        context.waitFor(client -> client.player.getMainHandItem().is(RusticItems.CHILI_PEPPER)
                && client.player.getMainHandItem().getCount() == 2 && client.player.getFoodData().getFoodLevel() == 20);
        context.getInput().holdKeyFor(options -> options.keyUse, 40);
        connection.waitForServerboundPackets();
        context.waitFor(client -> client.player.getMainHandItem().getCount() == 1);
        server.runOnServer(minecraftServer -> {
            var player = connection.getServerPlayer();
            var speed = player.getEffect(net.minecraft.world.effect.MobEffects.SPEED);
            if (player.getFoodData().getFoodLevel() != 20 || speed == null || speed.getAmplifier() != 0
                    || speed.getDuration() <= 0 || speed.getDuration() > 400) {
                throw new AssertionError("Chili must be edible when full and apply speed I");
            }
        });
    }

    private void checkFoodAtFullHunger(ClientGameTestContext context, TestServerContext serverContext,
                                       TestServerConnection connection, Item food) {
        context.getInput().lookAt(0.0F, -90.0F);
        serverContext.runOnServer(server -> {
            var player = connection.getServerPlayer();
            player.getFoodData().setFoodLevel(20);
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(food, 2));
        });
        context.waitFor(client -> client.player.getMainHandItem().is(food)
                && client.player.getMainHandItem().getCount() == 2
                && client.player.getFoodData().getFoodLevel() == 20);
        context.getInput().holdKeyFor(options -> options.keyUse, 25);
        connection.waitForServerboundPackets();
        serverContext.runOnServer(server -> {
            var player = connection.getServerPlayer();
            if (player.getFoodData().getFoodLevel() != 20 || player.getMainHandItem().getCount() != 2) {
                throw new AssertionError("Food used with full hunger: " + food);
            }
        });
    }


    private void checkDedicatedServer(ClientGameTestContext context) {
        try (TestDedicatedServerContext server = context.worldBuilder().createServer()) {
            try (TestDedicatedServerConnection connection = server.connect()) {
                connection.waitForChunksRender();
                server.runCommand("gamemode survival @a");
                context.getInput().lookAt(0.0F, -90.0F);
                checkFood(context, server, connection, RusticItems.WILDBERRIES, 12, 2.0F);
                checkFoodAtFullHunger(context, server, connection, RusticItems.WILDBERRIES);
                checkFood(context, server, connection, RusticItems.GRAPES, 13, 1.8F);
                checkFood(context, server, connection, RusticItems.OLIVES, 11, 0.8F);
                checkFoodAtFullHunger(context, server, connection, RusticItems.OLIVES);
                checkBlockPlacementAndMining(context, server, connection, RusticBlocks.SLATE, Items.WOODEN_PICKAXE);
                checkBlockPlacementAndMining(context, server, connection, RusticBlocks.PAINTED_WOOD_SILVER, Items.WOODEN_AXE);
                checkBlockPlacementAndMining(context, server, connection, RusticBlocks.CLAY_WALL_DIAG, Items.WOODEN_SHOVEL);
                checkBlockPlacementAndMining(context, server, connection, RusticBlocks.LOG_OLIVE, Items.WOODEN_AXE);
                checkGateInteraction(context, server, connection, RusticBlocks.OLIVE_FENCE_GATE);
                checkDoorInteraction(context, server, connection, RusticBlocks.OLIVE_DOOR);
                checkFood(context, server, connection, RusticItems.CHILI_PEPPER, 13, 2.4F, 40);
                checkChiliAtFullHunger(context, server, connection);
                // Set up state, then let the server save it when the real client disconnects.
                server.runOnServer(minecraftServer -> {
                    var player = connection.getServerPlayer();
                    player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(RusticItems.GRAPES, 2));
                    player.getFoodData().setFoodLevel(11);
                });
                connection.waitForClientboundPackets();
            }
            try (TestDedicatedServerConnection connection = server.connect()) {
                connection.waitForChunksRender();
                context.waitFor(client -> client.player.getMainHandItem().is(RusticItems.GRAPES)
                        && client.player.getMainHandItem().getCount() == 2
                        && client.player.getFoodData().getFoodLevel() == 11);
                server.runOnServer(minecraftServer -> {
                    var player = connection.getServerPlayer();
                    if (!player.getMainHandItem().is(RusticItems.GRAPES)
                            || player.getMainHandItem().getCount() != 2
                            || player.getFoodData().getFoodLevel() != 11) {
                        throw new AssertionError("Dedicated server did not restore player inventory/hunger");
                    }
                });
                context.takeScreenshot("rustic-dedicated-after-reconnect");
            }
        }
    }

}
