package net.satisfy.smoker.core.util;

import net.minecraft.resources.ResourceLocation;
import net.satisfy.smoker.Smoker;

public final class SmokerIdentifier {
    private SmokerIdentifier() {
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(Smoker.MOD_ID, path);
    }
}
