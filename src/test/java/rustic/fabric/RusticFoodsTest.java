package rustic.fabric;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Component checks under Fabric Loader JUnit, without a Minecraft server/world. */
class RusticFoodsTest {
    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void wildberryFoodValues() {
        assertEquals(2, RusticFoods.WILDBERRIES.nutrition());
        assertEquals(2.0F, RusticFoods.WILDBERRIES.saturation(), 0.0001F);
        assertFalse(RusticFoods.WILDBERRIES.canAlwaysEat());
    }

    @Test
    void grapeFoodValues() {
        assertEquals(3, RusticFoods.GRAPES.nutrition());
        assertEquals(1.8F, RusticFoods.GRAPES.saturation(), 0.0001F);
        assertFalse(RusticFoods.GRAPES.canAlwaysEat());
    }

    @Test
    void oliveFoodAndEffect() {
        assertEquals(1, RusticFoods.OLIVES.nutrition());
        assertEquals(0.8F, RusticFoods.OLIVES.saturation(), 0.0001F);
        assertFalse(RusticFoods.OLIVES.canAlwaysEat());
        assertEquals(1.2F, RusticFoods.OLIVES_CONSUMABLE.consumeSeconds(), 0.0001F);
        assertEquals(1, RusticFoods.OLIVES_CONSUMABLE.onConsumeEffects().size());
        var effect = assertInstanceOf(ApplyStatusEffectsConsumeEffect.class,
                RusticFoods.OLIVES_CONSUMABLE.onConsumeEffects().getFirst());
        assertEquals(0.95F, effect.probability(), 0.0001F);
        assertEquals(1, effect.effects().size());
        var nausea = effect.effects().getFirst();
        assertEquals(MobEffects.NAUSEA, nausea.getEffect());
        assertEquals(200, nausea.getDuration());
        assertEquals(1, nausea.getAmplifier());
        assertFalse(nausea.isAmbient());
        assertFalse(nausea.isVisible());
        assertFalse(nausea.showIcon());
    }

    @Test
    void quickFoodDuration() {
        assertEquals(0.8F, RusticFoods.QUICK_FOOD.consumeSeconds(), 0.0001F);
    }
}
