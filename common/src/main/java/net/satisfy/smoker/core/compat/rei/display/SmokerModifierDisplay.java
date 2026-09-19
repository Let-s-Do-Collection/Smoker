package net.satisfy.smoker.core.compat.rei.display;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.ItemStack;
import net.satisfy.smoker.core.compat.SmokerCompatFoods;
import net.satisfy.smoker.core.compat.rei.category.SmokerModifierCategory;
import net.satisfy.smoker.core.recipe.SmokerModifierRecipe;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SmokerModifierDisplay extends BasicDisplay {
    private final SmokerModifierRecipe recipe;

    public SmokerModifierDisplay(SmokerModifierRecipe recipe) {
        super(
                List.of(
                        EntryIngredients.of(new ItemStack(recipe.getSmokingMaterial())),
                        EntryIngredients.ofItemStacks(SmokerCompatFoods.SAMPLE_FOODS)
                ),
                Collections.singletonList(EntryIngredients.ofItemStacks(SmokerCompatFoods.SAMPLE_FOODS)),
                Optional.empty()
        );
        this.recipe = recipe;
    }

    public SmokerModifierRecipe getRecipe() {
        return recipe;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return SmokerModifierCategory.SMOKER_MODIFIER_DISPLAY;
    }
}
