package rustic.fabric.gametest;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerConnection;
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
            checkFood(context, world, RusticItems.WILDBERRIES, 12, 2.0F);
            checkFood(context, world, RusticItems.GRAPES, 13, 1.8F);
            context.takeScreenshot("rustic-grapes-after-eating");
        }
        try {
            Path report = Path.of(System.getProperty("rustic.client.report"));
            Files.createDirectories(report.getParent());
            Files.writeString(report, "rustic-client-food-gametest-passed\n");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void checkFood(ClientGameTestContext context, TestSingleplayerContext world,
                           Item food, int expectedHunger, float expectedSaturation) {
        TestServerConnection connection = world.getConnection();
        world.getServer().runOnServer(server -> {
            var player = connection.getServerPlayer();
            player.getFoodData().setFoodLevel(10);
            player.getFoodData().setSaturation(0.0F);
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(food, 2));
        });
        context.waitFor(client -> client.player.getMainHandItem().is(food)
                && client.player.getMainHandItem().getCount() == 2
                && client.player.getFoodData().getFoodLevel() == 10);
        context.getInput().holdKeyFor(options -> options.keyUse, 25);
        connection.waitForServerboundPackets();
        context.waitFor(client -> client.player.getMainHandItem().is(food)
                && client.player.getMainHandItem().getCount() == 1);
        world.getServer().runOnServer(server -> {
            var player = connection.getServerPlayer();
            if (player.getFoodData().getFoodLevel() != expectedHunger
                    || Math.abs(player.getFoodData().getSaturationLevel() - expectedSaturation) > 0.0001F
                    || player.getMainHandItem().getCount() != 1) {
                throw new AssertionError("Unexpected server state after consuming " + food);
            }
        });
    }
}
