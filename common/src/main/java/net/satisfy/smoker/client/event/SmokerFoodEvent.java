package net.satisfy.smoker.client.event;

import dev.architectury.event.events.common.TickEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class SmokerFoodEvent {
    public static void init() {
        TickEvent.PLAYER_POST.register(SmokerFoodEvent::onPlayerTick);
    }

    private static void onPlayerTick(Player player) {
        if (player.getUseItemRemainingTicks() != 1) return;

        ItemStack stack = player.getUseItem();
        if (!stack.isEdible() || !stack.hasTag()) return;

        CompoundTag tag = stack.getTag();
        if (tag != null) {
            if (tag.contains("smoker_heal_amount")) {
                int healAmount = tag.getInt("smoker_heal_amount");
                player.heal(healAmount);
            }

            if (tag.contains("smoker_effect") && tag.contains("smoker_effect_duration")) {
                String effectName = tag.getString("smoker_effect");
                int duration = tag.getInt("smoker_effect_duration");

                MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(new ResourceLocation(effectName));
                if (effect != null) {
                    player.addEffect(new MobEffectInstance(effect, duration, 0));
                }
            }
        }
    }
}
