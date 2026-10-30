package rustic.fabric;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;

/** Development entrypoint; gameplay systems are not yet implemented. */
public final class RusticFabric implements ModInitializer {
    public static final String MOD_ID = "rustic";

    @Override
    public void onInitialize() {
        RusticItems.initialize();
        RusticBlocks.initialize();
        RusticCreativeTabs.initialize();
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
