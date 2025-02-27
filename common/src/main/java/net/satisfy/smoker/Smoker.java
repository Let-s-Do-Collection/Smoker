package net.satisfy.smoker;

import net.satisfy.smoker.core.registry.EntityTypeRegistry;
import net.satisfy.smoker.core.registry.ObjectRegistry;
import net.satisfy.smoker.core.registry.RecipeRegistry;
import net.satisfy.smoker.core.registry.ScreenHandlerTypeRegistry;

public class Smoker {
    public static final String MOD_ID = "smoker";


    public static void init() {
        EntityTypeRegistry.init();
        ObjectRegistry.init();
        ScreenHandlerTypeRegistry.init();
        RecipeRegistry.init();
    }
}
