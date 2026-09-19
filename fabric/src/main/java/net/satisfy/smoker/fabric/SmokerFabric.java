package net.satisfy.smoker.fabric;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.satisfy.smoker.Smoker;
import net.satisfy.smoker.fabric.core.config.SmokerFabricConfig;

public class SmokerFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        AutoConfig.register(SmokerFabricConfig.class, GsonConfigSerializer::new);
        Smoker.init();
    }
}
