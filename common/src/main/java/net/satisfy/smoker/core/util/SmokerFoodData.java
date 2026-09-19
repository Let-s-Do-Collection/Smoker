package net.satisfy.smoker.core.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public final class SmokerFoodData {
    public static final String SATURATION_KEY = "smoker_saturation";
    public static final String NUTRITION_KEY = "smoker_nutrition";
    public static final String HEAL_KEY = "smoker_heal_amount";
    public static final String EFFECT_KEY = "smoker_effect";
    public static final String EFFECT_DURATION_KEY = "smoker_effect_duration";
    public static final String PROCESSED_KEY = "SmokerProcessed";
    public static final String PERFECT_KEY = "smoker_perfect";
    public static final double PERFECT_BONUS_MULTIPLIER = 1.5;

    private SmokerFoodData() {
    }

    public static boolean isPerfect(ItemStack stack) {
        CompoundTag tag = getTag(stack);
        return tag != null && tag.getBoolean(PERFECT_KEY);
    }

    public static boolean isProcessed(ItemStack stack) {
        CompoundTag tag = getTag(stack);
        return tag != null && tag.getBoolean(PROCESSED_KEY);
    }

    public static void setPerfect(ItemStack stack, boolean perfect) {
        CompoundTag tag = getOrCreateTag(stack);
        if (perfect) {
            tag.putBoolean(PERFECT_KEY, true);
        } else {
            tag.remove(PERFECT_KEY);
        }
        applyTag(stack, tag);

        if (perfect) {
            stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        } else {
            stack.remove(DataComponents.ENCHANTMENT_GLINT_OVERRIDE);
        }
    }

    public static CompoundTag getOrCreateTag(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null ? new CompoundTag() : data.copyTag();
    }

    public static boolean hasData(ItemStack stack) {
        return stack.has(DataComponents.CUSTOM_DATA);
    }

    public static CompoundTag getTag(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null ? null : data.copyTag();
    }

    public static void applyTag(ItemStack stack, CompoundTag tag) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
}
