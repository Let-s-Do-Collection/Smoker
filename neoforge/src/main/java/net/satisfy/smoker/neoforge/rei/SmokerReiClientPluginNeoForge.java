package net.satisfy.smoker.neoforge.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.forge.REIPluginClient;
import net.satisfy.smoker.core.compat.rei.SmokerReiClientPlugin;

@REIPluginClient
public class SmokerReiClientPluginNeoForge implements REIClientPlugin {

    @Override
    public void registerCategories(CategoryRegistry registry) {
        SmokerReiClientPlugin.registerCategories(registry);
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        SmokerReiClientPlugin.registerDisplays(registry);
    }
}
