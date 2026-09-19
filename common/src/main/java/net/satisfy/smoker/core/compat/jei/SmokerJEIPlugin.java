package net.satisfy.smoker.core.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.satisfy.smoker.core.compat.jei.category.SmokerModifierCategory;
import net.satisfy.smoker.core.recipe.SmokerModifierRecipe;
import net.satisfy.smoker.core.registry.CommonRegistry;
import net.satisfy.smoker.core.util.SmokerIdentifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * JEI does its own classpath scanning for @JeiPlugin, so no fabric.mod.json/toml entrypoint is
 * needed for this class beyond it being present on the mod's classpath. Recipe-transfer handling
 * is intentionally not implemented here (out of scope for this pass).
 */
@JeiPlugin
public class SmokerJEIPlugin implements IModPlugin {

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return SmokerIdentifier.id("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new SmokerModifierCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
        List<RecipeHolder<SmokerModifierRecipe>> holders = recipeManager.getAllRecipesFor(CommonRegistry.SMOKER_RECIPE_TYPE.get());
        List<SmokerModifierRecipe> recipes = new ArrayList<>();
        holders.forEach(holder -> recipes.add(holder.value()));
        registration.addRecipes(SmokerModifierCategory.SMOKER_MODIFIER_TYPE, recipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(CommonRegistry.IMPROVED_SMOKER.get()), SmokerModifierCategory.SMOKER_MODIFIER_TYPE);
    }
}
