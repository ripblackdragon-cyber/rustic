package rustic.fabric.gametest;

import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import rustic.fabric.DiagonalClayWallBlock;
import rustic.fabric.RusticBlocks;
import rustic.fabric.RusticCreativeTabs;
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
        assertRegistered(helper, RusticItems.OLIVES, "olives");
        assertRegistered(helper, RusticItems.CHILI_PEPPER, "chili_pepper");
        helper.assertTrue(RusticFabric.id("farming").equals(BuiltInRegistries.CREATIVE_MODE_TAB.getKey(RusticCreativeTabs.FARMING)),
                "Farming tab not registered");
        helper.assertTrue(RusticCreativeTabs.FARMING.getIconItem().is(RusticItems.OLIVES),
                "Farming tab icon must be olives");
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

    @GameTest
    public void olivesConsumption(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.getRandom().setSeed(123456L);
        int effectCount = 0;
        int noEffectCount = 0;
        for (int i = 0; i < 128; i++) {
            player.removeEffect(MobEffects.NAUSEA);
            player.getFoodData().setFoodLevel(10);
            player.getFoodData().setSaturation(0);
            ItemStack stack = new ItemStack(RusticItems.OLIVES, 2);
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            stack.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
            helper.assertTrue(player.isUsingItem() && player.getUseItemRemainingTicks() == 24,
                    "Olives must take 24 ticks to eat");
            ItemStack remaining = stack.finishUsingItem(helper.getLevel(), player);
            helper.assertTrue(remaining.getCount() == 1 && player.getFoodData().getFoodLevel() == 11,
                    "Olives must consume one item and restore one hunger point");
            helper.assertTrue(Math.abs(player.getFoodData().getSaturationLevel() - 0.8F) < 0.0001F,
                    "Olive saturation differs");
            var effect = player.getEffect(MobEffects.NAUSEA);
            if (effect == null) {
                noEffectCount++;
            } else {
                effectCount++;
                helper.assertTrue(effect.getDuration() == 200 && effect.getAmplifier() == 1,
                        "Olive nausea duration or strength differs");
                helper.assertTrue(!effect.isAmbient() && !effect.isVisible() && !effect.showIcon(),
                        "Olive nausea visibility differs");
            }
            player.stopUsingItem();
        }
        helper.assertTrue(effectCount > 0 && noEffectCount > 0,
                "Fixed-seed sample should include both nausea and no-effect cases");
        helper.succeed();
    }

    @GameTest
    public void chiliConsumption(GameTestHelper helper) {
        int damaged = 0;
        int unharmed = 0;
        for (int i = 0; i < 512; i++) {
            // Fresh mock per trial avoids damage immunity persisting between immediate finishes.
            Player player = helper.makeMockPlayer(GameType.SURVIVAL);
            player.getFoodData().setFoodLevel(20);
            player.getFoodData().setSaturation(0.0F);
            ItemStack stack = new ItemStack(RusticItems.CHILI_PEPPER, 2);
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            stack.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
            helper.assertTrue(player.isUsingItem() && player.getUseItemRemainingTicks() == 32,
                    "Chili must be edible at full hunger and take 32 ticks");
            ItemStack remaining = stack.finishUsingItem(helper.getLevel(), player);
            helper.assertTrue(remaining.getCount() == 1 && player.getFoodData().getFoodLevel() == 20,
                    "Chili must consume one item at full hunger");
            var speed = player.getEffect(MobEffects.SPEED);
            helper.assertTrue(speed != null && speed.getDuration() == 400 && speed.getAmplifier() == 0,
                    "Chili must apply speed I for 400 ticks");
            if (player.getHealth() < player.getMaxHealth()) {
                damaged++;
                helper.assertTrue(player.getHealth() == player.getMaxHealth() - 1.0F, "Unexpected chili damage");
            } else {
                unharmed++;
            }
            player.stopUsingItem();
        }
        helper.assertTrue(damaged > 0 && unharmed > 0, "Chili sample must include heat and no-heat outcomes");
        helper.succeed();
    }

    @GameTest
    public void slateBlocksAndRecipes(GameTestHelper helper) {
        Block[] blocks = {RusticBlocks.SLATE, RusticBlocks.SLATE_ROOF, RusticBlocks.SLATE_TILE,
                RusticBlocks.SLATE_BRICK, RusticBlocks.SLATE_CHISELED};
        String[] names = {"slate", "slate_roof", "slate_tile", "slate_brick", "slate_chiseled"};
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockPos pos = new BlockPos(1, 1, 1);
        for (int i = 0; i < blocks.length; i++) {
            Block block = blocks[i];
            helper.assertTrue(RusticFabric.id(names[i]).equals(BuiltInRegistries.BLOCK.getKey(block)),
                    "Incorrect slate block registry name");
            helper.assertTrue(RusticFabric.id(names[i]).equals(BuiltInRegistries.ITEM.getKey(block.asItem())),
                    "Incorrect slate item registry name");
            helper.setBlock(pos, block);
            var state = helper.getBlockState(pos);
            helper.assertTrue(state.is(BlockTags.MINEABLE_WITH_PICKAXE) && state.requiresCorrectToolForDrops(),
                    "Slate must require a pickaxe");
            helper.assertTrue(state.getDestroySpeed(helper.getLevel(), helper.absolutePos(pos)) == 2.0F,
                    "Slate hardness differs");
            var drops = Block.getDrops(state, helper.getLevel(), helper.absolutePos(pos), null, player,
                    new ItemStack(Items.WOODEN_PICKAXE));
            helper.assertTrue(drops.size() == 1 && drops.getFirst().is(block.asItem())
                    && drops.getFirst().getCount() == 1, "Slate loot differs for " + names[i]);
        }
        assertSlateRecipe(helper, RusticBlocks.SLATE, RusticBlocks.SLATE_ROOF, 2, 4);
        assertSlateRecipe(helper, RusticBlocks.SLATE, RusticBlocks.SLATE_TILE, 1, 1);
        assertSlateRecipe(helper, RusticBlocks.SLATE_BRICK, RusticBlocks.SLATE_TILE, 1, 1);
        assertSlateRecipe(helper, RusticBlocks.SLATE_TILE, RusticBlocks.SLATE_BRICK, 2, 4);
        assertSlateRecipe(helper, RusticBlocks.SLATE_BRICK, RusticBlocks.SLATE_CHISELED, 2, 4);
        helper.succeed();
    }

    private void assertSlateRecipe(GameTestHelper helper, Block input, Block output, int width, int count) {
        List<ItemStack> items = java.util.stream.IntStream.range(0, width * width)
                .mapToObj(i -> new ItemStack(input)).toList();
        var grid = CraftingInput.of(width, width, items);
        var recipe = helper.getLevel().recipeAccess().getRecipeFor(RecipeType.CRAFTING, grid, helper.getLevel());
        helper.assertTrue(recipe.isPresent(), "Missing crafting recipe for " + output);
        ItemStack result = recipe.orElseThrow().value().assemble(grid);
        helper.assertTrue(result.is(output.asItem()) && result.getCount() == count,
                "Unexpected crafting output for " + output);
    }

    @GameTest
    public void slateShapesAndRecipes(GameTestHelper helper) {
        Block[] blocks = {RusticBlocks.SLATE_PILLAR, RusticBlocks.SLATE_ROOF_STAIRS, RusticBlocks.SLATE_BRICK_STAIRS,
                RusticBlocks.SLATE_ROOF_SLAB, RusticBlocks.SLATE_BRICK_SLAB};
        String[] names = {"slate_pillar", "stairs_slate_roof", "stairs_slate_brick", "slate_roof_slab", "slate_brick_slab"};
        BlockPos pos = new BlockPos(1, 1, 1);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        for (int i = 0; i < blocks.length; i++) {
            Block block = blocks[i];
            helper.setBlock(pos, block);
            var state = helper.getBlockState(pos);
            helper.assertTrue(RusticFabric.id(names[i]).equals(BuiltInRegistries.BLOCK.getKey(block))
                    && RusticFabric.id(names[i]).equals(BuiltInRegistries.ITEM.getKey(block.asItem())), "Bad shape ID");
            helper.assertTrue(state.is(BlockTags.MINEABLE_WITH_PICKAXE) && state.requiresCorrectToolForDrops()
                    && state.getDestroySpeed(helper.getLevel(), helper.absolutePos(pos)) == 2.0F, "Bad shape hardness or tool");
            var drops = Block.getDrops(state, helper.getLevel(), helper.absolutePos(pos), null, player,
                    new ItemStack(Items.WOODEN_PICKAXE));
            helper.assertTrue(drops.size() == 1 && drops.getFirst().is(block.asItem())
                    && drops.getFirst().getCount() == 1, "Bad single-block drop for " + names[i]);
            if (block instanceof SlabBlock) {
                var doubled = state.setValue(SlabBlock.TYPE, SlabType.DOUBLE);
                helper.setBlock(pos, doubled);
                var doubleDrops = Block.getDrops(doubled, helper.getLevel(), helper.absolutePos(pos), null, player,
                        new ItemStack(Items.WOODEN_PICKAXE));
                helper.assertTrue(doubleDrops.size() == 1 && doubleDrops.getFirst().is(block.asItem())
                        && doubleDrops.getFirst().getCount() == 2, "Double slab must drop two slabs");
                helper.assertTrue(state.hasProperty(SlabBlock.WATERLOGGED), "Modern slab waterlogging missing");
            }
            if (block instanceof StairBlock) {
                helper.assertTrue(state.hasProperty(StairBlock.FACING) && state.hasProperty(StairBlock.HALF)
                        && state.hasProperty(StairBlock.SHAPE), "Stair properties missing");
            }
        }
        var pillar = RusticBlocks.SLATE_PILLAR.defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.X);
        helper.assertTrue(pillar.rotate(Rotation.CLOCKWISE_90).getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Z,
                "Pillar must rotate from X to Z");
        assertShapedRecipe(helper, RusticBlocks.SLATE, RusticBlocks.SLATE_PILLAR, List.of("SS", "SS", "SS"), 6);
        assertShapedRecipe(helper, RusticBlocks.SLATE_ROOF, RusticBlocks.SLATE_ROOF_STAIRS, List.of("S  ", "SS ", "SSS"), 4);
        assertShapedRecipe(helper, RusticBlocks.SLATE_BRICK, RusticBlocks.SLATE_BRICK_STAIRS, List.of("S  ", "SS ", "SSS"), 4);
        assertShapedRecipe(helper, RusticBlocks.SLATE_ROOF, RusticBlocks.SLATE_ROOF_SLAB, List.of("SSS"), 6);
        assertShapedRecipe(helper, RusticBlocks.SLATE_BRICK, RusticBlocks.SLATE_BRICK_SLAB, List.of("SSS"), 6);
        helper.succeed();
    }

    private void assertShapedRecipe(GameTestHelper helper, Block input, Block output, List<String> pattern, int count) {
        List<ItemStack> stacks = pattern.stream().flatMapToInt(String::chars)
                .mapToObj(c -> c == 'S' ? new ItemStack(input) : ItemStack.EMPTY).toList();
        var grid = CraftingInput.of(pattern.getFirst().length(), pattern.size(), stacks);
        var recipe = helper.getLevel().recipeAccess().getRecipeFor(RecipeType.CRAFTING, grid, helper.getLevel());
        helper.assertTrue(recipe.isPresent(), "Missing recipe for " + output);
        ItemStack result = recipe.orElseThrow().value().assemble(grid);
        helper.assertTrue(result.is(output.asItem()) && result.getCount() == count, "Wrong shaped recipe output for " + output);
    }

    @GameTest
    public void paintedWood(GameTestHelper helper) {
        Block[] blocks = {RusticBlocks.PAINTED_WOOD_WHITE, RusticBlocks.PAINTED_WOOD_ORANGE, RusticBlocks.PAINTED_WOOD_MAGENTA, RusticBlocks.PAINTED_WOOD_LIGHT_BLUE, RusticBlocks.PAINTED_WOOD_YELLOW, RusticBlocks.PAINTED_WOOD_LIME, RusticBlocks.PAINTED_WOOD_PINK, RusticBlocks.PAINTED_WOOD_GRAY, RusticBlocks.PAINTED_WOOD_SILVER, RusticBlocks.PAINTED_WOOD_CYAN, RusticBlocks.PAINTED_WOOD_PURPLE, RusticBlocks.PAINTED_WOOD_BLUE, RusticBlocks.PAINTED_WOOD_BROWN, RusticBlocks.PAINTED_WOOD_GREEN, RusticBlocks.PAINTED_WOOD_RED, RusticBlocks.PAINTED_WOOD_BLACK};
        String[] names = {"painted_wood_white", "painted_wood_orange", "painted_wood_magenta", "painted_wood_light_blue", "painted_wood_yellow", "painted_wood_lime", "painted_wood_pink", "painted_wood_gray", "painted_wood_silver", "painted_wood_cyan", "painted_wood_purple", "painted_wood_blue", "painted_wood_brown", "painted_wood_green", "painted_wood_red", "painted_wood_black"};
        String[] dyeNames = {"white_dye", "orange_dye", "magenta_dye", "light_blue_dye", "yellow_dye", "lime_dye", "pink_dye", "gray_dye", "light_gray_dye", "cyan_dye", "purple_dye", "blue_dye", "brown_dye", "green_dye", "red_dye", "black_dye"};
        BlockPos pos = new BlockPos(1, 1, 1);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        for (int i = 0; i < blocks.length; i++) {
            Block block = blocks[i];
            helper.setBlock(pos, block);
            var state = helper.getBlockState(pos);
            helper.assertTrue(RusticFabric.id(names[i]).equals(BuiltInRegistries.BLOCK.getKey(block))
                    && RusticFabric.id(names[i]).equals(BuiltInRegistries.ITEM.getKey(block.asItem())), "Bad painted wood registry ID");
            helper.assertTrue(state.is(BlockTags.MINEABLE_WITH_AXE) && !state.requiresCorrectToolForDrops()
                    && state.getDestroySpeed(helper.getLevel(), helper.absolutePos(pos)) == 2.0F, "Wrong painted wood mining properties");
            var fire = FlammableBlockRegistry.getDefaultInstance().get(block);
            helper.assertTrue(fire != null && fire.getIgniteOdds() == 5 && fire.getBurnOdds() == 20,
                    "Wrong painted wood fire values");
            var drops = Block.getDrops(state, helper.getLevel(), helper.absolutePos(pos), null, player, ItemStack.EMPTY);
            helper.assertTrue(drops.size() == 1 && drops.getFirst().is(block.asItem()) && drops.getFirst().getCount() == 1,
                    "Wrong painted wood drops");
            assertPaintedRecipe(helper, Items.OAK_PLANKS, vanillaItem(dyeNames[i]), block);
            assertPaintedRecipe(helper, RusticBlocks.PAINTED_WOOD_RED.asItem(), vanillaItem(dyeNames[i]), block);
        }
        assertPaintedRecipe(helper, Items.SPRUCE_PLANKS, Items.BONE_MEAL, RusticBlocks.PAINTED_WOOD_WHITE);
        assertPaintedRecipe(helper, Items.SPRUCE_PLANKS, Items.INK_SAC, RusticBlocks.PAINTED_WOOD_BLACK);
        assertPaintedRecipe(helper, Items.SPRUCE_PLANKS, Items.LAPIS_LAZULI, RusticBlocks.PAINTED_WOOD_BLUE);
        assertPaintedRecipe(helper, Items.SPRUCE_PLANKS, Items.COCOA_BEANS, RusticBlocks.PAINTED_WOOD_BROWN);
        helper.succeed();
    }

    private Item vanillaItem(String name) {
        return BuiltInRegistries.ITEM.getValue(net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", name));
    }

    private void assertPaintedRecipe(GameTestHelper helper, Item planks, Item dye, Block output) {
        List<ItemStack> items = java.util.stream.IntStream.range(0, 9)
                .mapToObj(i -> new ItemStack(i == 4 ? dye : planks)).toList();
        var grid = CraftingInput.of(3, 3, items);
        var recipe = helper.getLevel().recipeAccess().getRecipeFor(RecipeType.CRAFTING, grid, helper.getLevel());
        helper.assertTrue(recipe.isPresent(), "Missing painted wood recipe for " + output);
        ItemStack result = recipe.orElseThrow().value().assemble(grid);
        helper.assertTrue(result.is(output.asItem()) && result.getCount() == 8, "Wrong painted wood recipe output for " + output);
    }

    @GameTest
    public void clayWalls(GameTestHelper helper) {
        Block[] blocks = {RusticBlocks.CLAY_WALL, RusticBlocks.CLAY_WALL_CROSS, RusticBlocks.CLAY_WALL_DIAG};
        String[] names = {"clay_wall", "clay_wall_cross", "clay_wall_diag"};
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockPos pos = new BlockPos(1, 1, 1);
        for (int i = 0; i < blocks.length; i++) {
            Block block = blocks[i];
            helper.setBlock(pos, block);
            var state = helper.getBlockState(pos);
            helper.assertTrue(RusticFabric.id(names[i]).equals(BuiltInRegistries.BLOCK.getKey(block))
                    && RusticFabric.id(names[i]).equals(BuiltInRegistries.ITEM.getKey(block.asItem())), "Bad clay wall ID");
            helper.assertTrue(state.is(BlockTags.MINEABLE_WITH_SHOVEL) && !state.requiresCorrectToolForDrops()
                    && state.getDestroySpeed(helper.getLevel(), helper.absolutePos(pos)) == 1.0F, "Wrong clay wall properties");
            var drops = Block.getDrops(state, helper.getLevel(), helper.absolutePos(pos), null, player, ItemStack.EMPTY);
            helper.assertTrue(drops.size() == 1 && drops.getFirst().is(block.asItem()) && drops.getFirst().getCount() == 1,
                    "Wrong clay wall drops");
        }
        assertClayRecipe(helper, Blocks.CLAY.asItem(), RusticBlocks.CLAY_WALL, List.of(" P ", "PCP", " P "), 8);
        assertClayRecipe(helper, RusticBlocks.CLAY_WALL.asItem(), RusticBlocks.CLAY_WALL_CROSS, List.of("P P", " C ", "P P"), 1);
        assertClayRecipe(helper, RusticBlocks.CLAY_WALL.asItem(), RusticBlocks.CLAY_WALL_DIAG, List.of("P  ", " C ", "  P"), 1);
        var diagonal = (DiagonalClayWallBlock) RusticBlocks.CLAY_WALL_DIAG;
        BlockPos clicked = helper.absolutePos(new BlockPos(2, 1, 2));
        helper.setBlock(new BlockPos(2, 1, 2), Blocks.STONE);
        assertClayFacing(helper, diagonal, player, clicked, Direction.UP, 0.9, 1.0, 0.5, Direction.EAST);
        assertClayFacing(helper, diagonal, player, clicked, Direction.UP, 0.1, 1.0, 0.5, Direction.WEST);
        assertClayFacing(helper, diagonal, player, clicked, Direction.UP, 0.5, 1.0, 0.9, Direction.SOUTH);
        assertClayFacing(helper, diagonal, player, clicked, Direction.UP, 0.5, 1.0, 0.1, Direction.NORTH);
        assertClayFacing(helper, diagonal, player, clicked, Direction.NORTH, 0.9, 0.5, 0.0, Direction.SOUTH);
        var state = diagonal.defaultBlockState().setValue(DiagonalClayWallBlock.FACING, Direction.NORTH);
        helper.assertTrue(state.rotate(Rotation.CLOCKWISE_90).getValue(DiagonalClayWallBlock.FACING) == Direction.EAST,
                "Diagonal wall rotation differs");
        helper.succeed();
    }

    private void assertClayFacing(GameTestHelper helper, DiagonalClayWallBlock block, Player player, BlockPos pos,
                                  Direction face, double x, double y, double z, Direction expected) {
        var hit = new BlockHitResult(new Vec3(pos.getX() + x, pos.getY() + y, pos.getZ() + z), face, pos, false);
        var context = new BlockPlaceContext(player, InteractionHand.MAIN_HAND, new ItemStack(block), hit);
        var state = block.getStateForPlacement(context);
        helper.assertTrue(state.getValue(DiagonalClayWallBlock.FACING) == expected, "Diagonal wall orientation differs");
    }

    private void assertClayRecipe(GameTestHelper helper, Item center, Block output, List<String> pattern, int count) {
        List<ItemStack> stacks = pattern.stream().flatMapToInt(String::chars)
                .mapToObj(c -> c == 'P' ? new ItemStack(RusticBlocks.PAINTED_WOOD_RED) :
                        c == 'C' ? new ItemStack(center) : ItemStack.EMPTY).toList();
        var grid = CraftingInput.of(pattern.getFirst().length(), pattern.size(), stacks);
        var recipe = helper.getLevel().recipeAccess().getRecipeFor(RecipeType.CRAFTING, grid, helper.getLevel());
        helper.assertTrue(recipe.isPresent(), "Missing clay wall recipe");
        ItemStack result = recipe.orElseThrow().value().assemble(grid);
        helper.assertTrue(result.is(output.asItem()) && result.getCount() == count, "Wrong clay wall output");
    }

    @GameTest(maxTicks = 500)
    public void logsAndPlanks(GameTestHelper helper) {
        Block[] logs = {RusticBlocks.LOG_OLIVE, RusticBlocks.LOG_IRONWOOD};
        Block[] planks = {RusticBlocks.PLANKS_OLIVE, RusticBlocks.PLANKS_IRONWOOD};
        String[] names = {"olive", "ironwood"};
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockPos pos = new BlockPos(1, 1, 1);
        for (int i = 0; i < logs.length; i++) {
            for (Block block : new Block[]{logs[i], planks[i]}) {
                helper.setBlock(pos, block);
                var state = helper.getBlockState(pos);
                helper.assertTrue(state.is(BlockTags.MINEABLE_WITH_AXE) && !state.requiresCorrectToolForDrops()
                        && state.getDestroySpeed(helper.getLevel(), helper.absolutePos(pos)) == 2.0F, "Wood properties differ");
                var fire = FlammableBlockRegistry.getDefaultInstance().get(block);
                helper.assertTrue(fire != null && fire.getIgniteOdds() == 5 && fire.getBurnOdds() == (block == logs[i] ? 5 : 20), "Fire properties differ");
                var drops = Block.getDrops(state, helper.getLevel(), helper.absolutePos(pos), null, player, ItemStack.EMPTY);
                helper.assertTrue(drops.size() == 1 && drops.getFirst().is(block.asItem()) && drops.getFirst().getCount() == 1, "Wood drops differ");
            }
            helper.assertTrue(RusticFabric.id("log_" + names[i]).equals(BuiltInRegistries.BLOCK.getKey(logs[i])), "Log ID differs");
            helper.assertTrue(RusticFabric.id("planks_" + names[i]).equals(BuiltInRegistries.BLOCK.getKey(planks[i])), "Plank ID differs");
            var state = logs[i].defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.X);
            helper.assertTrue(state.rotate(Rotation.CLOCKWISE_90).getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Z, "Log rotation differs");
            var grid = CraftingInput.of(1, 1, List.of(new ItemStack(logs[i])));
            var recipe = helper.getLevel().recipeAccess().getRecipeFor(RecipeType.CRAFTING, grid, helper.getLevel());
            helper.assertTrue(recipe.isPresent(), "Missing log-to-planks recipe");
            ItemStack result = recipe.orElseThrow().value().assemble(grid);
            helper.assertTrue(result.is(planks[i].asItem()) && result.getCount() == 4, "Wrong log-to-planks recipe");
            assertPaintedRecipe(helper, planks[i].asItem(), Items.BONE_MEAL, RusticBlocks.PAINTED_WOOD_WHITE);
        }
        FurnaceBlockEntity first = prepareFurnace(helper, new BlockPos(1, 1, 1), logs[0].asItem());
        FurnaceBlockEntity second = prepareFurnace(helper, new BlockPos(3, 1, 1), logs[1].asItem());
        helper.succeedWhen(() -> {
            assertOutput(helper, first, Items.CHARCOAL);
            assertOutput(helper, second, Items.CHARCOAL);
        });
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
