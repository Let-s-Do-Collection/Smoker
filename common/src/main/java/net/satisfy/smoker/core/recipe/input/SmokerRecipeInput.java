package net.satisfy.smoker.core.recipe.input;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

public record SmokerRecipeInput(ItemStack smokingMaterial, ItemStack food) implements RecipeInput {
    public static final int SMOKING_MATERIAL_SLOT = 0;
    public static final int FOOD_SLOT = 1;

    @Override
    public @NotNull ItemStack getItem(int index) {
        return switch (index) {
            case SMOKING_MATERIAL_SLOT -> smokingMaterial;
            case FOOD_SLOT -> food;
            default -> throw new IllegalArgumentException("No such slot " + index);
        };
    }

    @Override
    public int size() {
        return 2;
    }
}
