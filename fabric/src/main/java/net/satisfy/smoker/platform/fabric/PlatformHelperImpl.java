package net.satisfy.smoker.platform.fabric;

import me.shedaniel.autoconfig.AutoConfig;
import net.satisfy.smoker.fabric.core.config.SmokerFabricConfig;

public class PlatformHelperImpl {
    public static boolean isVanillaSmokerReplacementEnabled() {
        return AutoConfig.getConfigHolder(SmokerFabricConfig.class).getConfig().replaceVanillaSmoker;
    }

    public static int getPerfectSmokingDelaySeconds() {
        return AutoConfig.getConfigHolder(SmokerFabricConfig.class).getConfig().perfectSmokingDelaySeconds;
    }

    public static int getPerfectSmokingRevertSeconds() {
        return AutoConfig.getConfigHolder(SmokerFabricConfig.class).getConfig().perfectSmokingRevertSeconds;
    }

    public static boolean isEverythingSmokable() {
        return AutoConfig.getConfigHolder(SmokerFabricConfig.class).getConfig().everythingSmokable;
    }
}
