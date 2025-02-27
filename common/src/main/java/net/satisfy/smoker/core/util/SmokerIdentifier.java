package net.satisfy.smoker.core.util;

import net.minecraft.resources.ResourceLocation;
import net.satisfy.smoker.Smoker;

public class SmokerIdentifier extends ResourceLocation {

    public SmokerIdentifier(String path) {
        super(Smoker.MOD_ID, path);
    }

}
