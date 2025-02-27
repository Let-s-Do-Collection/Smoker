package net.smoker.forge;

import dev.architectury.platform.forge.EventBuses;
import net.satisfy.smoker.Smoker;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Smoker.MOD_ID)
public class SmokerForge {
    public SmokerForge() {
        EventBuses.registerModEventBus(Smoker.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        Smoker.init();
    }
}
