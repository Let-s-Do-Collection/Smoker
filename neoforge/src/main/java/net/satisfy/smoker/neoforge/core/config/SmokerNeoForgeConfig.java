package net.satisfy.smoker.neoforge.core.config;

import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class SmokerNeoForgeConfig {
    public static boolean replaceVanillaSmoker = true;
    public static boolean everythingSmokable = false;
    public static boolean coloredSmoke = true;
    public static int perfectSmokingDelaySeconds = 720;
    public static int perfectSmokingRevertSeconds = 2160;

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue REPLACE_VANILLA_SMOKER = BUILDER.define("replaceVanillaSmoker", true);
    private static final ModConfigSpec.BooleanValue EVERYTHING_SMOKABLE = BUILDER.define("everythingSmokable", false);
    private static final ModConfigSpec.BooleanValue COLORED_SMOKE = BUILDER.define("coloredSmoke", true);
    private static final ModConfigSpec.IntValue PERFECT_SMOKING_DELAY_SECONDS = BUILDER.defineInRange("perfectSmokingDelaySeconds", 720, 1, 3600);
    private static final ModConfigSpec.IntValue PERFECT_SMOKING_REVERT_SECONDS = BUILDER.defineInRange("perfectSmokingRevertSeconds", 2160, 1, 7200);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static void onLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == SPEC) bake();
    }

    public static void onReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == SPEC) bake();
    }

    private static void bake() {
        replaceVanillaSmoker = REPLACE_VANILLA_SMOKER.get();
        everythingSmokable = EVERYTHING_SMOKABLE.get();
        coloredSmoke = COLORED_SMOKE.get();
        perfectSmokingDelaySeconds = PERFECT_SMOKING_DELAY_SECONDS.get();
        perfectSmokingRevertSeconds = PERFECT_SMOKING_REVERT_SECONDS.get();
        if (perfectSmokingRevertSeconds <= perfectSmokingDelaySeconds) {
            perfectSmokingRevertSeconds = perfectSmokingDelaySeconds + 1;
        }
    }
}
