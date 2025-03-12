package net.satisfy.smoker;

import net.satisfy.smoker.client.event.SmokerFoodEvent;
import net.satisfy.smoker.core.registry.CommonRegistry;

public class Smoker {
    public static final String MOD_ID = "smoker";

    public static void init() {
        CommonRegistry.init();
        SmokerFoodEvent.init();
    }
}
