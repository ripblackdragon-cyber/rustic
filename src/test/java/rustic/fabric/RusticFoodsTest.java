package rustic.fabric;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
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
    void quickFoodDuration() {
        assertEquals(0.8F, RusticFoods.QUICK_FOOD.consumeSeconds(), 0.0001F);
    }
}
