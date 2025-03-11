package net.satisfy.smoker.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.satisfy.smoker.client.SmokerClient;
import net.satisfy.smoker.core.util.SmokerTooltip;

import java.util.List;

public class SmokerClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SmokerClient.onInitializeClient();

        ItemTooltipCallback.EVENT.register(this::onItemTooltip);
    }

    private void onItemTooltip(ItemStack stack, net.minecraft.world.item.TooltipFlag context, List<Component> tooltip) {
        SmokerTooltip.addSmokerTooltip(stack, tooltip);
    }
}
