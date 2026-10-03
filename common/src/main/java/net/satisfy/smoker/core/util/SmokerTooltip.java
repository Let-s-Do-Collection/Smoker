package net.satisfy.smoker.core.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.satisfy.smoker.core.recipe.SmokerModifierRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SmokerTooltip {
    private static final int GREEN = 0x90EE90;
    private static final int RED = 0xFF5555;
    private static final int BLUE = 0x5599FF;

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
        if (!isSmokerProcessed(itemStack)) return;
        CompoundTag tag = Objects.requireNonNull(SmokerFoodData.getTag(itemStack));

        boolean perfect = SmokerFoodData.isPerfect(itemStack);
        double multiplier = perfect ? SmokerFoodData.PERFECT_BONUS_MULTIPLIER : 1.0;

        tooltip.add(Component.translatable("tooltip.smoker.item.processed").withStyle(Style.EMPTY.withColor(0xD28B46)));
        if (perfect) {
            tooltip.add(Component.translatable("tooltip.smoker.item.perfect").withStyle(Style.EMPTY.withColor(0xFFD700)));
        }

        if (tag.getDouble(NUTRITION_KEY) > 0.0) {
            FoodProperties food = itemStack.get(DataComponents.FOOD);
            String text;
            if (food != null && tag.contains(SmokerFoodData.BASE_NUTRITION_KEY)) {
                text = "+" + (food.nutrition() - tag.getInt(SmokerFoodData.BASE_NUTRITION_KEY));
            } else {
                text = formatPercent(tag.getDouble(NUTRITION_KEY) * multiplier);
            }
            tooltip.add(line("tooltip.smoker.item.nutrition", text, GREEN));
        }

        if (tag.getDouble(SATURATION_KEY) > 0.0) {
            tooltip.add(line("tooltip.smoker.item.saturation", formatPercent(tag.getDouble(SATURATION_KEY) * multiplier), GREEN));
        }

        if (tag.getInt(HEAL_KEY) > 0) {
            tooltip.add(line("tooltip.smoker.item.heal", formatHearts(tag.getInt(HEAL_KEY)), RED));
        }

        if (tag.contains(EFFECT_KEY) && tag.getInt(EFFECT_DURATION_KEY) > 0) {
            Component effectLine = formatEffectLine(tag.getString(EFFECT_KEY), tag.getInt(EFFECT_DURATION_KEY));
            if (effectLine != null) {
                tooltip.add(effectLine);
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
        return line("tooltip.smoker.item.saturation", formatPercent(saturation), GREEN);
    }

    public static Component formatNutritionLine(double nutrition) {
        return line("tooltip.smoker.item.nutrition", formatPercent(nutrition), GREEN);
    }

    public static Component formatHealLine(int healAmount) {
        return line("tooltip.smoker.item.heal", formatHearts(healAmount), RED);
    }

    public static Component formatEffectLine(String effectName, int effectDuration) {
        MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(ResourceLocation.parse(effectName));
        if (effect == null) return null;
        return Component.translatable("tooltip.smoker.item.effect").append(": " + effect.getDisplayName().getString() + " (" + formatDuration(effectDuration) + ")").withStyle(Style.EMPTY.withColor(BLUE));
    }

    private static MutableComponent line(String labelKey, String value, int color) {
        return Component.literal(value + " ").append(Component.translatable(labelKey)).withStyle(Style.EMPTY.withColor(color));
    }

    private static String formatPercent(double fraction) {
        double pct = fraction * 100;
        return (pct % 1 == 0) ? String.format("+%.0f%%", pct) : String.format("+%.1f%%", pct);
    }

    private static String formatHearts(int halfHearts) {
        double hearts = halfHearts / 2.0;
        return (hearts % 1 == 0) ? String.format("%.0f \u2764", hearts) : String.format("%.1f \u2764", hearts);
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
