package rustic.fabric.gametest;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestDedicatedServerConnection;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestDedicatedServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerConnection;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
            context.takeScreenshot("rustic-grapes-after-eating");
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
