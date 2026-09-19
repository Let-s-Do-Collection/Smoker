package net.satisfy.smoker.neoforge.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.satisfy.smoker.Smoker;
import net.satisfy.smoker.client.SmokerClient;
import net.satisfy.smoker.client.gui.screens.inventory.ImprovedSmokerScreen;
import net.satisfy.smoker.client.particle.TintedCampfireSmokeParticle;
import net.satisfy.smoker.client.particle.TintedSmokeParticle;
import net.satisfy.smoker.core.registry.CommonRegistry;
import net.satisfy.smoker.core.util.SmokerSmokeColors;
import net.satisfy.smoker.core.util.SmokerTooltip;

@EventBusSubscriber(modid = Smoker.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class SmokerClientNeoForge {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(SmokerClient::onInitializeClient);
        NeoForge.EVENT_BUS.addListener(SmokerClientNeoForge::onItemTooltip);
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(CommonRegistry.SMOKING_GUI_HANDLER.get(), ImprovedSmokerScreen::new);
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(CommonRegistry.DARK_SMOKE.get(),
                sprites -> TintedSmokeParticle.provider(sprites, SmokerSmokeColors.DARK_R, SmokerSmokeColors.DARK_G, SmokerSmokeColors.DARK_B));
        event.registerSpriteSet(CommonRegistry.WARM_SMOKE.get(),
                sprites -> TintedSmokeParticle.provider(sprites, SmokerSmokeColors.WARM_R, SmokerSmokeColors.WARM_G, SmokerSmokeColors.WARM_B));

        event.registerSpriteSet(CommonRegistry.DARK_SMOKE_LARGE.get(),
                sprites -> TintedCampfireSmokeParticle.provider(sprites, SmokerSmokeColors.DARK_R, SmokerSmokeColors.DARK_G, SmokerSmokeColors.DARK_B));
        event.registerSpriteSet(CommonRegistry.WARM_SMOKE_LARGE.get(),
                sprites -> TintedCampfireSmokeParticle.provider(sprites, SmokerSmokeColors.WARM_R, SmokerSmokeColors.WARM_G, SmokerSmokeColors.WARM_B));
    }

    private static void onItemTooltip(ItemTooltipEvent event) {
        SmokerTooltip.addSmokerTooltip(event.getItemStack(), event.getToolTip());
    }
}
