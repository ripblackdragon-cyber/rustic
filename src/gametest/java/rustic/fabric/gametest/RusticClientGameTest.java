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
import net.minecraft.world.level.block.Blocks;
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
            context.takeScreenshot("rustic-olives-after-eating");
            checkSlatePlacementAndMining(context, world.getServer(), connection);
            checkCreativeTab(context, world.getServer());
        }
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

    private void checkSlatePlacementAndMining(ClientGameTestContext context, TestServerContext server,
                                              TestServerConnection connection) {
        BlockPos target = server.computeOnServer(minecraftServer -> {
            var player = connection.getServerPlayer();
            var level = player.level();
            BlockPos pos = player.blockPosition().east(2);
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            level.setBlockAndUpdate(pos.above(), Blocks.AIR.defaultBlockState());
            level.setBlockAndUpdate(pos.below(), Blocks.STONE.defaultBlockState());
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(RusticBlocks.SLATE));
            return pos;
        });
        context.waitFor(client -> client.player.getMainHandItem().is(RusticBlocks.SLATE.asItem())
                && client.level.getBlockState(target.below()).is(Blocks.STONE));
        context.getInput().lookAt(target.below());
        context.getInput().pressKey(options -> options.keyUse);
        connection.waitForServerboundPackets();
        server.waitFor(minecraftServer -> connection.getServerPlayer().level().getBlockState(target).is(RusticBlocks.SLATE));
        context.waitFor(client -> client.level.getBlockState(target).is(RusticBlocks.SLATE));
        context.takeScreenshot("rustic-placed-slate");
        server.runOnServer(minecraftServer -> connection.getServerPlayer()
                .setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.WOODEN_PICKAXE)));
        context.waitFor(client -> client.player.getMainHandItem().is(Items.WOODEN_PICKAXE));
        context.getInput().lookAt(target);
        context.getInput().holdKeyFor(options -> options.keyAttack, 80);
        connection.waitForServerboundPackets();
        server.waitFor(minecraftServer -> {
            var player = connection.getServerPlayer();
            var level = player.level();
            boolean dropped = !level.getEntitiesOfClass(ItemEntity.class, new AABB(target).inflate(3),
                    entity -> entity.getItem().is(RusticBlocks.SLATE.asItem())).isEmpty();
            return level.getBlockState(target).isAir()
                    && (dropped || player.getInventory().contains(new ItemStack(RusticBlocks.SLATE)));
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
                    RusticItems.GOLD_DUST, RusticItems.WILDBERRIES, RusticItems.GRAPES);
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
        context.getInput().holdKeyFor(options -> options.keyUse, 25);
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

    private void checkFoodAtFullHunger(ClientGameTestContext context, TestServerContext serverContext,
                                       TestServerConnection connection, Item food) {
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
                checkSlatePlacementAndMining(context, server, connection);
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
