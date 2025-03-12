package net.satisfy.smoker.core.event;

import dev.architectury.event.events.common.TickEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class SmokerFoodEvent {
    public static void register() {
        TickEvent.PLAYER_POST.register(SmokerFoodEvent::onPlayerTick);
    }

    private static void onPlayerTick(Player player) {
        if (!player.isUsingItem()) return; 

        ItemStack stack = player.getUseItem();
        if (!stack.isEdible() || !stack.hasTag()) return; 

        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("smoker_heal_amount")) {
            int healAmount = tag.getInt("smoker_heal_amount");

            if (player.getUseItemRemainingTicks() == 1) {
                player.heal(healAmount); 
            }
        }
    }
}
