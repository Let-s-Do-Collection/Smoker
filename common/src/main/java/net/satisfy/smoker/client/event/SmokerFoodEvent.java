package net.satisfy.smoker.client.event;

import dev.architectury.event.events.common.TickEvent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.satisfy.smoker.core.util.SmokerFoodData;

public class SmokerFoodEvent {
    public static void init() {
        TickEvent.PLAYER_POST.register(SmokerFoodEvent::onPlayerTick);
    }

    private static void onPlayerTick(Player player) {
        if (player.getUseItemRemainingTicks() != 1) return;

        ItemStack stack = player.getUseItem();
        if (!stack.has(DataComponents.FOOD) || !SmokerFoodData.hasData(stack)) return;

        CompoundTag tag = SmokerFoodData.getTag(stack);
        if (tag != null) {
            if (tag.contains(SmokerFoodData.HEAL_KEY)) {
                int healAmount = tag.getInt(SmokerFoodData.HEAL_KEY);
                player.heal(healAmount);
            }

            if (tag.contains(SmokerFoodData.EFFECT_KEY) && tag.contains(SmokerFoodData.EFFECT_DURATION_KEY)) {
                String effectName = tag.getString(SmokerFoodData.EFFECT_KEY);
                int duration = tag.getInt(SmokerFoodData.EFFECT_DURATION_KEY);

                MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(ResourceLocation.parse(effectName));
                if (effect != null) {
                    player.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect), duration, 0));
                }
            }
        }
    }
}
