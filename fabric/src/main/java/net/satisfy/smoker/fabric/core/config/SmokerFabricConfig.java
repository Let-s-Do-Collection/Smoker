package net.satisfy.smoker.fabric.core.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.satisfy.smoker.Smoker;

@Config(name = Smoker.MOD_ID)
public class SmokerFabricConfig implements ConfigData {
    public boolean replaceVanillaSmoker = true;

    public boolean everythingSmokable = false;

    @ConfigEntry.BoundedDiscrete(min = 1, max = 3600)
    public int perfectSmokingDelaySeconds = 720;

    @ConfigEntry.BoundedDiscrete(min = 1, max = 7200)
    public int perfectSmokingRevertSeconds = 2160;

    @Override
    public void validatePostLoad() {
        if (perfectSmokingRevertSeconds <= perfectSmokingDelaySeconds) {
            perfectSmokingRevertSeconds = perfectSmokingDelaySeconds + 1;
        }
    }
}
