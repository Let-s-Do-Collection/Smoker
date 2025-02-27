package net.satisfy.smoker.client;

import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.satisfy.smoker.client.menu.SmokingFoodScreen;
import net.satisfy.smoker.client.renderer.SmokingFoodBlockRenderer;
import net.satisfy.smoker.core.registry.EntityTypeRegistry;
import net.satisfy.smoker.core.registry.ScreenHandlerTypeRegistry;

@Environment(EnvType.CLIENT)
public class SmokerClient {

    public static void onInitializeClient() {
        MenuRegistry.registerScreenFactory(ScreenHandlerTypeRegistry.SMOKING_FOOD_MENU.get(), SmokingFoodScreen::new);

        BlockEntityRendererRegistry.register(EntityTypeRegistry.SMOKER.get(), SmokingFoodBlockRenderer::new);

    }
}
