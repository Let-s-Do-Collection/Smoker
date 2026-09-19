package net.satisfy.smoker.core.compat.jei.category;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.satisfy.smoker.Smoker;
import net.satisfy.smoker.core.compat.SmokerCompatFoods;
import net.satisfy.smoker.core.recipe.SmokerModifierRecipe;
import net.satisfy.smoker.core.registry.CommonRegistry;
import net.satisfy.smoker.core.util.SmokerTooltip;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * JEI category for the Smoking Station's wood-modifier recipes. The recipe has no single fixed
 * output item (see SmokerModifierRecipe#assemble - it applies bonuses to whatever food was fed
 * in), so the food input/output slots show a curated list of common foods for display only. The
 * saturation/nutrition/heal/effect bonuses are shown as small icons (golden carrot, bread, golden
 * apple, the real potion effect icon) with a hover tooltip each, instead of an always-visible
 * wall of text that easily overflowed the category width.
 */
public class SmokerModifierCategory implements IRecipeCategory<SmokerModifierRecipe> {
    public static final RecipeType<SmokerModifierRecipe> SMOKER_MODIFIER_TYPE =
            RecipeType.create(Smoker.MOD_ID, "smoker_modifier", SmokerModifierRecipe.class);

    private static final int WIDTH = 122;
    private static final int HEIGHT = 46;
    private static final int ICON_SIZE = 18;

    private final IDrawable icon;
    private final Component title;

    public SmokerModifierCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CommonRegistry.IMPROVED_SMOKER.get()));
        this.title = CommonRegistry.IMPROVED_SMOKER.get().getName();
    }

    @NotNull
    @Override
    public RecipeType<SmokerModifierRecipe> getRecipeType() {
        return SMOKER_MODIFIER_TYPE;
    }

    @NotNull
    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SmokerModifierRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 4, 4)
                .addItemStack(new ItemStack(recipe.getSmokingMaterial()));

        builder.addSlot(RecipeIngredientRole.INPUT, 4, 24)
                .addItemStacks(SmokerCompatFoods.SAMPLE_FOODS);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 44, 14)
                .addItemStacks(SmokerCompatFoods.SAMPLE_FOODS);

        int gridX = 74;
        int gridY = 4;
        int gap = 22;

        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, gridX, gridY)
                .addItemStack(new ItemStack(Items.GOLDEN_CARROT))
                .addRichTooltipCallback((view, tooltip) -> {
                    tooltip.clear();
                    tooltip.add(SmokerTooltip.formatSaturationLine(recipe.getSaturation()));
                });

        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, gridX + gap, gridY)
                .addItemStack(new ItemStack(Items.BREAD))
                .addRichTooltipCallback((view, tooltip) -> {
                    tooltip.clear();
                    tooltip.add(SmokerTooltip.formatNutritionLine(recipe.getNutrition()));
                });

        if (recipe.getHealAmount() > 0) {
            builder.addSlot(RecipeIngredientRole.RENDER_ONLY, gridX, gridY + gap)
                    .addItemStack(new ItemStack(Items.GOLDEN_APPLE))
                    .addRichTooltipCallback((view, tooltip) -> {
                        tooltip.clear();
                        tooltip.add(SmokerTooltip.formatHealLine(recipe.getHealAmount()));
                    });
        }
        // The effect icon (if any) is drawn separately in draw()/getTooltip() below - a MobEffect
        // icon isn't an ItemStack, so it can't go through addSlot/addItemStack like the others.
    }

    @Override
    public void draw(SmokerModifierRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        if (!recipe.hasEffect()) return;
        MobEffect effect = SmokerTooltip.getEffect(recipe.getEffectName());
        if (effect == null) return;

        Holder<MobEffect> holder = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect);
        TextureAtlasSprite sprite = Minecraft.getInstance().getMobEffectTextures().get(holder);
        guiGraphics.blit(effectIconX(), effectIconY(), 0, ICON_SIZE, ICON_SIZE, sprite);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, SmokerModifierRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (!recipe.hasEffect()) return;
        if (mouseX < effectIconX() || mouseX >= effectIconX() + ICON_SIZE || mouseY < effectIconY() || mouseY >= effectIconY() + ICON_SIZE) {
            return;
        }
        Component line = SmokerTooltip.formatEffectLine(recipe.getEffectName(), recipe.getEffectDuration());
        if (line != null) {
            tooltip.add(line);
        }
    }

    private int effectIconX() {
        return 74 + 22;
    }

    private int effectIconY() {
        return 4 + 22;
    }
}
