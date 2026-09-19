package net.satisfy.smoker.core.compat;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

/**
 * The Smoker Modifier recipe (see SmokerModifierRecipe) doesn't have a fixed food ingredient -
 * any item with a FOOD data component can be fed into the smoking station's food slot. JEI/REI
 * need something concrete to render in that slot though, so this is a small curated list of
 * common vanilla foods used purely for recipe-viewer display. It has no effect on actual recipe
 * matching, which is still governed by ItemStack#has(DataComponents.FOOD) at runtime.
 */
public final class SmokerCompatFoods {
    private SmokerCompatFoods() {
    }

    public static final List<ItemStack> SAMPLE_FOODS = List.of(
            new ItemStack(Items.BREAD),
            new ItemStack(Items.COOKED_BEEF),
            new ItemStack(Items.APPLE),
            new ItemStack(Items.COOKED_PORKCHOP),
            new ItemStack(Items.POTATO)
    );
}
