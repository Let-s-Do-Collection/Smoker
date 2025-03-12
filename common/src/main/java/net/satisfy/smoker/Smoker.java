package net.satisfy.smoker;

import net.satisfy.smoker.client.event.SmokerFoodEvent;
import net.satisfy.smoker.core.registry.EntityTypeRegistry;
import net.satisfy.smoker.core.registry.ObjectRegistry;
import net.satisfy.smoker.core.registry.RecipeTypeRegistry;
import net.satisfy.smoker.core.registry.ScreenHandlerTypeRegistry;

public class Smoker {
    public static final String MOD_ID = "smoker";

    public static void init() {
        EntityTypeRegistry.init();
        ObjectRegistry.init();
        ScreenHandlerTypeRegistry.init();
        RecipeTypeRegistry.init();
        SmokerFoodEvent.init();
    }
}
