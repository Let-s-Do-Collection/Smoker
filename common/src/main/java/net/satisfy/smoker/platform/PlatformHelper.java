package net.satisfy.smoker.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;

public abstract class PlatformHelper {
    @ExpectPlatform
    public static boolean isVanillaSmokerReplacementEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getPerfectSmokingDelaySeconds() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getPerfectSmokingRevertSeconds() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isEverythingSmokable() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isColoredSmokeEnabled() {
        throw new AssertionError();
    }
}
