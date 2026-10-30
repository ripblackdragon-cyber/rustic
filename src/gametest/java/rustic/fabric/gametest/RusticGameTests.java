package rustic.fabric.gametest;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import rustic.fabric.RusticFabric;
import rustic.fabric.RusticItems;

/** Runs in an isolated Minecraft GameTest world, not an interactive client. */
public class RusticGameTests {
    @GameTest
    public void itemsRegistered(GameTestHelper helper) {
        assertRegistered(helper, RusticItems.BEE, "bee");
        assertRegistered(helper, RusticItems.HONEYCOMB, "honeycomb");
        assertRegistered(helper, RusticItems.BEESWAX, "beeswax");
        assertRegistered(helper, RusticItems.TALLOW, "tallow");
        assertRegistered(helper, RusticItems.IRON_DUST_TINY, "dust_tiny_iron");
        assertRegistered(helper, RusticItems.GOLD_DUST, "dust_gold");
        assertRegistered(helper, RusticItems.WILDBERRIES, "wildberries");
        assertRegistered(helper, RusticItems.GRAPES, "grapes");
        helper.succeed();
    }

    private void assertRegistered(GameTestHelper helper, Item item, String path) {
        helper.assertTrue(RusticFabric.id(path).equals(BuiltInRegistries.ITEM.getKey(item)),
                "Missing or incorrect registry ID: " + path);
    }

    @GameTest
    public void consumingFoods(GameTestHelper helper) {
        assertFood(helper, RusticItems.WILDBERRIES, 12, 2.0F);
        assertFood(helper, RusticItems.GRAPES, 13, 1.8F);
        helper.succeed();
    }

    private void assertFood(GameTestHelper helper, Item food, int expectedHunger, float expectedSaturation) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack stack = new ItemStack(food, 2);
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        player.getFoodData().setFoodLevel(20);
        stack.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
        helper.assertTrue(!player.isUsingItem() && stack.getCount() == 2,
                "Food must not be used at full hunger");
        player.getFoodData().setFoodLevel(10);
        player.getFoodData().setSaturation(0.0F);
        stack.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
        helper.assertTrue(player.isUsingItem() && player.getUseItemRemainingTicks() == 16,
                "Food must start a 16-tick use when hungry");
        ItemStack remaining = stack.finishUsingItem(helper.getLevel(), player);
        helper.assertTrue(remaining.getCount() == 1, "Eating must consume exactly one item");
        helper.assertTrue(player.getFoodData().getFoodLevel() == expectedHunger, "Unexpected hunger after eating");
        helper.assertTrue(Math.abs(player.getFoodData().getSaturationLevel() - expectedSaturation) < 0.0001F,
                "Unexpected saturation after eating");
    }

    @GameTest(maxTicks = 500)
    public void furnaceRecipes(GameTestHelper helper) {
        FurnaceBlockEntity wax = prepareFurnace(helper, new BlockPos(1, 1, 1), RusticItems.HONEYCOMB);
        FurnaceBlockEntity iron = prepareFurnace(helper, new BlockPos(3, 1, 1), RusticItems.IRON_DUST_TINY);
        FurnaceBlockEntity gold = prepareFurnace(helper, new BlockPos(5, 1, 1), RusticItems.GOLD_DUST);
        helper.succeedWhen(() -> {
            assertOutput(helper, wax, RusticItems.BEESWAX);
            assertOutput(helper, iron, Items.IRON_NUGGET);
            assertOutput(helper, gold, Items.GOLD_INGOT);
        });
    }

    private FurnaceBlockEntity prepareFurnace(GameTestHelper helper, BlockPos pos, Item input) {
        helper.setBlock(pos, Blocks.FURNACE);
        FurnaceBlockEntity furnace = (FurnaceBlockEntity) helper.getLevel().getBlockEntity(helper.absolutePos(pos));
        helper.assertTrue(furnace != null, "Furnace block entity missing");
        furnace.setItem(0, new ItemStack(input));
        furnace.setItem(1, new ItemStack(Items.COAL));
        return furnace;
    }

    private void assertOutput(GameTestHelper helper, FurnaceBlockEntity furnace, Item output) {
        helper.assertTrue(furnace.getItem(0).isEmpty(), "Input not consumed");
        helper.assertTrue(furnace.getItem(2).is(output) && furnace.getItem(2).getCount() == 1,
                "Expected exactly one " + BuiltInRegistries.ITEM.getKey(output));
    }
}
