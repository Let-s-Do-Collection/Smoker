package net.satisfy.smoker.core.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Objects;

public class SmokerTooltip {
    private static final String SATURATION_KEY = "smoker_saturation";
    private static final String NUTRITION_KEY = "smoker_nutrition";
    private static final String HEAL_KEY = "smoker_heal_amount";
    private static final String EFFECT_KEY = "smoker_effect";
    private static final String EFFECT_DURATION_KEY = "smoker_effect_duration";

    public static boolean isSmokerProcessed(ItemStack itemStack) {
        if (!itemStack.hasTag()) return false;
        CompoundTag tag = Objects.requireNonNull(itemStack.getTag());

        return (tag.contains(SATURATION_KEY) && tag.getDouble(SATURATION_KEY) > 0.0) ||
                (tag.contains(NUTRITION_KEY) && tag.getDouble(NUTRITION_KEY) > 0.0) ||
                (tag.contains(HEAL_KEY) && tag.getInt(HEAL_KEY) > 0) ||
                (tag.contains(EFFECT_KEY) && tag.contains(EFFECT_DURATION_KEY) && tag.getInt(EFFECT_DURATION_KEY) > 0);
    }

    public static void addSmokerTooltip(ItemStack itemStack, List<Component> tooltip) {
        if (isSmokerProcessed(itemStack)) {
            CompoundTag tag = itemStack.getTag();
            assert tag != null;

            tooltip.add(Component.translatable("tooltip.smoker.item.processed").setStyle(Style.EMPTY.withColor(0xD28B46)));

            if (tag.contains(SATURATION_KEY)) {
                double saturation = tag.getDouble(SATURATION_KEY) * 100;
                String saturationText = (saturation % 1 == 0) ? String.format("+%.0f%%", saturation) : String.format("%.1f%%", saturation);
                tooltip.add(Component.translatable("tooltip.smoker.item.saturation").append(": " + saturationText).setStyle(Style.EMPTY.withColor(0x90EE90)));
            }

            if (tag.contains(NUTRITION_KEY)) {
                double nutrition = tag.getDouble(NUTRITION_KEY) * 100;
                String nutritionText = (nutrition % 1 == 0) ? String.format("+%.0f%%", nutrition) : String.format("%.1f%%", nutrition);
                tooltip.add(Component.translatable("tooltip.smoker.item.nutrition").append(": " + nutritionText).setStyle(Style.EMPTY.withColor(0x90EE90)));
            }

            if (tag.contains(HEAL_KEY)) {
                int healAmount = tag.getInt(HEAL_KEY);
                tooltip.add(Component.translatable("tooltip.smoker.item.heal").append(": " + (healAmount / 2) + "❤").setStyle(Style.EMPTY.withColor(0xFF5555)));
            }

            if (tag.contains(EFFECT_KEY) && tag.contains(EFFECT_DURATION_KEY) && tag.getInt(EFFECT_DURATION_KEY) > 0) {
                String effectName = tag.getString(EFFECT_KEY);
                String effectDuration = formatDuration(tag.getInt(EFFECT_DURATION_KEY));
                MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(new ResourceLocation(effectName));

                if (effect != null) {
                    tooltip.add(Component.translatable("tooltip.smoker.item.effect")
                            .append(": " + effect.getDisplayName().getString() + " (" + effectDuration + ")")
                            .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x5599FF))));
                }
            }
        }
    }

    private static String formatDuration(int durationTicks) {
        int totalSeconds = durationTicks / 20;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
