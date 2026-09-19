package net.satisfy.smoker.core.compat.rei;

import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.satisfy.smoker.core.compat.rei.category.SmokerModifierCategory;
import net.satisfy.smoker.core.compat.rei.display.SmokerModifierDisplay;
import net.satisfy.smoker.core.recipe.SmokerModifierRecipe;
import net.satisfy.smoker.core.registry.CommonRegistry;

/**
 * Shared REI registration logic, called from the thin per-platform REIClientPlugin wrappers
 * (fabric/.../SmokerREIClientPluginFabric, neoforge/.../SmokerReiClientPluginNeoForge).
 */
public class SmokerReiClientPlugin {

    public static void registerCategories(CategoryRegistry registry) {
        registry.add(new SmokerModifierCategory());
        registry.addWorkstations(SmokerModifierCategory.SMOKER_MODIFIER_DISPLAY, EntryStacks.of(CommonRegistry.IMPROVED_SMOKER.get()));
    }

    public static void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(
                SmokerModifierRecipe.class,
                CommonRegistry.SMOKER_RECIPE_TYPE.get(),
                holder -> new SmokerModifierDisplay(holder.value())
        );
    }
}
