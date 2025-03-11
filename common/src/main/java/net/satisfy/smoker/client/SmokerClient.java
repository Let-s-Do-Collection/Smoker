package net.satisfy.smoker.client;

import dev.architectury.registry.menu.MenuRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.satisfy.smoker.client.menu.ImprovedSmokerGui;
import net.satisfy.smoker.core.registry.ScreenHandlerTypeRegistry;

@Environment(EnvType.CLIENT)
public class SmokerClient {

    public static void onInitializeClient() {
        MenuRegistry.registerScreenFactory(ScreenHandlerTypeRegistry.SMOKING_GUI_HANDLER.get(), ImprovedSmokerGui::new);
    }
}
