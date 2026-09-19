package net.satisfy.smoker.neoforge.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.satisfy.smoker.Smoker;
import net.satisfy.smoker.client.SmokerClient;
import net.satisfy.smoker.client.gui.screens.inventory.ImprovedSmokerScreen;
import net.satisfy.smoker.core.registry.CommonRegistry;
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

    private static void onItemTooltip(ItemTooltipEvent event) {
        SmokerTooltip.addSmokerTooltip(event.getItemStack(), event.getToolTip());
    }
}
