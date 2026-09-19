package net.satisfy.smoker.platform.neoforge;

import net.satisfy.smoker.neoforge.core.config.SmokerNeoForgeConfig;

public class PlatformHelperImpl {
    public static boolean isVanillaSmokerReplacementEnabled() {
        return SmokerNeoForgeConfig.replaceVanillaSmoker;
    }

    public static int getPerfectSmokingDelaySeconds() {
        return SmokerNeoForgeConfig.perfectSmokingDelaySeconds;
    }

    public static int getPerfectSmokingRevertSeconds() {
        return SmokerNeoForgeConfig.perfectSmokingRevertSeconds;
    }

    public static boolean isEverythingSmokable() {
        return SmokerNeoForgeConfig.everythingSmokable;
    }
}
