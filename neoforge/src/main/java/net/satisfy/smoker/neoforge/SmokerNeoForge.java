package net.satisfy.smoker.neoforge;

import dev.architectury.platform.hooks.EventBusesHooks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.satisfy.smoker.Smoker;
import net.satisfy.smoker.neoforge.core.config.SmokerNeoForgeConfig;

import java.util.Objects;

@Mod(Smoker.MOD_ID)
public class SmokerNeoForge {
    public SmokerNeoForge(final ModContainer modContainer) {
        IEventBus modEventBus = Objects.requireNonNull(modContainer.getEventBus());
        EventBusesHooks.whenAvailable(Smoker.MOD_ID, IEventBus::start);

        modContainer.registerConfig(ModConfig.Type.COMMON, SmokerNeoForgeConfig.SPEC);
        modEventBus.addListener(SmokerNeoForgeConfig::onLoad);
        modEventBus.addListener(SmokerNeoForgeConfig::onReload);

        Smoker.init();
    }
}
