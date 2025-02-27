package net.satisfy.smoker.fabric;

import net.satisfy.smoker.Smoker;
import net.fabricmc.api.ModInitializer;

public class SmokerFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Smoker.init();
    }
}
