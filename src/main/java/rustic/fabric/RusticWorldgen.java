package rustic.fabric;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/** Default overworld slate generation. Legacy dimension and config controls remain pending. */
public final class RusticWorldgen {
    public static final ResourceKey<PlacedFeature> SLATE = ResourceKey.create(Registries.PLACED_FEATURE, RusticFabric.id("slate"));

    private RusticWorldgen() {}

    public static void initialize() {
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Decoration.UNDERGROUND_ORES, SLATE);
    }
}
