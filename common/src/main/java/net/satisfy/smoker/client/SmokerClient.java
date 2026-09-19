package net.satisfy.smoker.client;

import dev.architectury.platform.Platform;
import dev.architectury.registry.menu.MenuRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.satisfy.smoker.client.gui.screens.inventory.ImprovedSmokerScreen;
import net.satisfy.smoker.core.registry.CommonRegistry;

@Environment(EnvType.CLIENT)
public class SmokerClient {

    public static void onInitializeClient() {
        // Architectury's MenuRegistry.registerScreenFactory doesn't reliably wire up on NeoForge
        // (same issue the other Let's Do mods work around) - NeoForge registers its screen via
        // RegisterMenuScreensEvent in SmokerClientNeoForge instead.
        if (Platform.isFabric()) {
            MenuRegistry.registerScreenFactory(CommonRegistry.SMOKING_GUI_HANDLER.get(), ImprovedSmokerScreen::new);
        }
    }
}
