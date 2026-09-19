package net.satisfy.smoker.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.satisfy.smoker.client.SmokerClient;
import net.satisfy.smoker.client.particle.TintedCampfireSmokeParticle;
import net.satisfy.smoker.client.particle.TintedSmokeParticle;
import net.satisfy.smoker.core.registry.CommonRegistry;
import net.satisfy.smoker.core.util.SmokerSmokeColors;
import net.satisfy.smoker.core.util.SmokerTooltip;

import java.util.List;

public class SmokerClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SmokerClient.onInitializeClient();

        ItemTooltipCallback.EVENT.register(this::onItemTooltip);

        ParticleFactoryRegistry.getInstance().register(CommonRegistry.DARK_SMOKE.get(),
                sprites -> TintedSmokeParticle.provider(sprites, SmokerSmokeColors.DARK_R, SmokerSmokeColors.DARK_G, SmokerSmokeColors.DARK_B));
        ParticleFactoryRegistry.getInstance().register(CommonRegistry.WARM_SMOKE.get(),
                sprites -> TintedSmokeParticle.provider(sprites, SmokerSmokeColors.WARM_R, SmokerSmokeColors.WARM_G, SmokerSmokeColors.WARM_B));

        ParticleFactoryRegistry.getInstance().register(CommonRegistry.DARK_SMOKE_LARGE.get(),
                sprites -> TintedCampfireSmokeParticle.provider(sprites, SmokerSmokeColors.DARK_R, SmokerSmokeColors.DARK_G, SmokerSmokeColors.DARK_B));
        ParticleFactoryRegistry.getInstance().register(CommonRegistry.WARM_SMOKE_LARGE.get(),
                sprites -> TintedCampfireSmokeParticle.provider(sprites, SmokerSmokeColors.WARM_R, SmokerSmokeColors.WARM_G, SmokerSmokeColors.WARM_B));
    }

    private void onItemTooltip(ItemStack stack, Item.TooltipContext context, TooltipFlag flag, List<Component> tooltip) {
        SmokerTooltip.addSmokerTooltip(stack, tooltip);
    }
}
