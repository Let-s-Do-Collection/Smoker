package net.satisfy.smoker.core.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.satisfy.smoker.core.recipe.SmokerModifierRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SmokerTooltip {
    private static final String SATURATION_KEY = SmokerFoodData.SATURATION_KEY;
    private static final String NUTRITION_KEY = SmokerFoodData.NUTRITION_KEY;
    private static final String HEAL_KEY = SmokerFoodData.HEAL_KEY;
    private static final String EFFECT_KEY = SmokerFoodData.EFFECT_KEY;
    private static final String EFFECT_DURATION_KEY = SmokerFoodData.EFFECT_DURATION_KEY;

    public static boolean isSmokerProcessed(ItemStack itemStack) {
        if (!SmokerFoodData.hasData(itemStack)) return false;
        CompoundTag tag = Objects.requireNonNull(SmokerFoodData.getTag(itemStack));

        return (tag.contains(SATURATION_KEY) && tag.getDouble(SATURATION_KEY) > 0.0) ||
                (tag.contains(NUTRITION_KEY) && tag.getDouble(NUTRITION_KEY) > 0.0) ||
                (tag.contains(HEAL_KEY) && tag.getInt(HEAL_KEY) > 0) ||
                (tag.contains(EFFECT_KEY) && tag.contains(EFFECT_DURATION_KEY) && tag.getInt(EFFECT_DURATION_KEY) > 0);
    }

    public static void addSmokerTooltip(ItemStack itemStack, List<Component> tooltip) {
        if (isSmokerProcessed(itemStack)) {
            CompoundTag tag = SmokerFoodData.getTag(itemStack);
            assert tag != null;

            boolean perfect = SmokerFoodData.isPerfect(itemStack);
            double bonusMultiplier = perfect ? SmokerFoodData.PERFECT_BONUS_MULTIPLIER : 1.0;

            tooltip.add(Component.translatable("tooltip.smoker.item.processed").setStyle(Style.EMPTY.withColor(0xD28B46)));

            if (perfect) {
                tooltip.add(Component.translatable("tooltip.smoker.item.perfect").setStyle(Style.EMPTY.withColor(0xFFD700)));
            }

            if (tag.contains(SATURATION_KEY)) {
                double saturation = tag.getDouble(SATURATION_KEY) * bonusMultiplier * 100;
                String saturationText = (saturation % 1 == 0) ? String.format("+%.0f%%", saturation) : String.format("%.1f%%", saturation);
                tooltip.add(Component.translatable("tooltip.smoker.item.saturation").append(": " + saturationText).setStyle(Style.EMPTY.withColor(0x90EE90)));
            }

            if (tag.contains(NUTRITION_KEY)) {
                double nutrition = tag.getDouble(NUTRITION_KEY) * bonusMultiplier * 100;
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
                MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(ResourceLocation.parse(effectName));

                if (effect != null) {
                    tooltip.add(Component.translatable("tooltip.smoker.item.effect").append(": " + effect.getDisplayName().getString() + " (" + effectDuration + ")").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x5599FF))));
                }
            }
        }
    }

    public static List<Component> formatModifierLines(SmokerModifierRecipe recipe) {
        List<Component> lines = new ArrayList<>();
        lines.add(formatSaturationLine(recipe.getSaturation()));
        lines.add(formatNutritionLine(recipe.getNutrition()));
        if (recipe.getHealAmount() > 0) {
            lines.add(formatHealLine(recipe.getHealAmount()));
        }
        if (recipe.hasEffect()) {
            Component effectLine = formatEffectLine(recipe.getEffectName(), recipe.getEffectDuration());
            if (effectLine != null) {
                lines.add(effectLine);
            }
        }
        return lines;
    }

    public static Component formatSaturationLine(double saturation) {
        double pct = saturation * 100;
        String text = (pct % 1 == 0) ? String.format("+%.0f%%", pct) : String.format("%.1f%%", pct);
        return Component.translatable("tooltip.smoker.item.saturation").append(": " + text).setStyle(Style.EMPTY.withColor(0x90EE90));
    }

    public static Component formatNutritionLine(double nutrition) {
        double pct = nutrition * 100;
        String text = (pct % 1 == 0) ? String.format("+%.0f%%", pct) : String.format("%.1f%%", pct);
        return Component.translatable("tooltip.smoker.item.nutrition").append(": " + text).setStyle(Style.EMPTY.withColor(0x90EE90));
    }

    public static Component formatHealLine(int healAmount) {
        return Component.translatable("tooltip.smoker.item.heal").append(": " + (healAmount / 2) + "❤").setStyle(Style.EMPTY.withColor(0xFF5555));
    }

    public static Component formatEffectLine(String effectName, int effectDuration) {
        MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(ResourceLocation.parse(effectName));
        if (effect == null) return null;
        String duration = formatDuration(effectDuration);
        return Component.translatable("tooltip.smoker.item.effect").append(": " + effect.getDisplayName().getString() + " (" + duration + ")").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x5599FF)));
    }

    public static MobEffect getEffect(String effectName) {
        return BuiltInRegistries.MOB_EFFECT.get(ResourceLocation.parse(effectName));
    }

    private static String formatDuration(int durationTicks) {
        int totalSeconds = durationTicks / 20;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
