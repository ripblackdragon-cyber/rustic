package rustic.fabric;

import java.util.Random;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Rustic 1.2.0's always-edible chili, including its server-side heat damage. */
public final class ChiliPepperItem extends Item {
    private final Random random = new Random();

    public ChiliPepperItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity consumer) {
        ItemStack result = super.finishUsingItem(stack, level, consumer);
        if (level instanceof ServerLevel server && consumer instanceof Player player) {
            player.addEffect(new MobEffectInstance(MobEffects.SPEED, 400));
            if (random.nextInt(24) == 0) {
                player.hurtServer(server, player.damageSources().onFire(), 1.0F);
            }
        }
        return result;
    }
}
