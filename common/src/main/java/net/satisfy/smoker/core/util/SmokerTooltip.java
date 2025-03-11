package net.satisfy.smoker.core.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;

public class SmokerTooltip {
    private static final String SATURATION_KEY = "smoker_saturation";
    private static final String NUTRITION_KEY = "smoker_nutrition";

    public static boolean isSmokerProcessed(ItemStack itemStack) {
        return itemStack.hasTag() && Objects.requireNonNull(itemStack.getTag()).contains(SATURATION_KEY);
    }

    public static void addSmokerTooltip(ItemStack itemStack, List<Component> tooltip) {
        if (isSmokerProcessed(itemStack)) {
            CompoundTag tag = itemStack.getTag();
            assert tag != null;

            tooltip.add(Component.translatable("tooltip.smoker.item.processed").setStyle(Style.EMPTY.withColor(0xD28B46)));

            if (tag.contains(SATURATION_KEY)) {
                double saturation = tag.getDouble(SATURATION_KEY) * 100;
                String saturationText = (saturation % 1 == 0) ? String.format("%.0f%%", saturation) : String.format("%.1f%%", saturation);
                tooltip.add(Component.translatable("tooltip.smoker.item.saturation").append(": " + saturationText).setStyle(Style.EMPTY.withColor(0x90EE90)));
            }

            if (tag.contains(NUTRITION_KEY)) {
                double nutrition = tag.getDouble(NUTRITION_KEY) * 100;
                String nutritionText = (nutrition % 1 == 0) ? String.format("%.0f%%", nutrition) : String.format("%.1f%%", nutrition);
                tooltip.add(Component.translatable("tooltip.smoker.item.nutrition").append(": " + nutritionText).setStyle(Style.EMPTY.withColor(0x90EE90)));
            }
        }
    }
}
