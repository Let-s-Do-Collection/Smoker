package net.satisfy.smoker.core.compat.rei.category;

import com.google.common.collect.Lists;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.satisfy.smoker.Smoker;
import net.satisfy.smoker.core.compat.rei.display.SmokerModifierDisplay;
import net.satisfy.smoker.core.recipe.SmokerModifierRecipe;
import net.satisfy.smoker.core.registry.CommonRegistry;
import net.satisfy.smoker.core.util.SmokerTooltip;

import java.util.List;

public class SmokerModifierCategory implements DisplayCategory<SmokerModifierDisplay> {
    public static final CategoryIdentifier<SmokerModifierDisplay> SMOKER_MODIFIER_DISPLAY = CategoryIdentifier.of(Smoker.MOD_ID, "smoker_modifier");
    private static final int ICON_SIZE = 18;

    @Override
    public CategoryIdentifier<SmokerModifierDisplay> getCategoryIdentifier() {
        return SMOKER_MODIFIER_DISPLAY;
    }

    @Override
    public Component getTitle() {
        return CommonRegistry.IMPROVED_SMOKER.get().getName();
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(CommonRegistry.IMPROVED_SMOKER.get());
    }

    @Override
    public int getDisplayHeight() {
        return 50;
    }

    @Override
    public List<Widget> setupDisplay(SmokerModifierDisplay display, Rectangle bounds) {
        SmokerModifierRecipe recipe = display.getRecipe();
        Point startPoint = new Point(bounds.getX() + 6, bounds.getY() + 4);
        List<Widget> widgets = Lists.newArrayList();

        widgets.add(Widgets.createRecipeBase(bounds));

        widgets.add(Widgets.createSlot(new Point(startPoint.x, startPoint.y))
                .entries(display.getInputEntries().get(0))
                .markInput());

        widgets.add(Widgets.createSlot(new Point(startPoint.x, startPoint.y + 20))
                .entries(display.getInputEntries().get(1))
                .markInput());

        widgets.add(Widgets.createArrow(new Point(startPoint.x + 24, startPoint.y + 10)).animationDurationTicks(200));

        widgets.add(Widgets.createSlot(new Point(startPoint.x + 54, startPoint.y + 10))
                .entries(display.getOutputEntries().get(0))
                .markOutput());

        int gridX = startPoint.x + 92;
        int gridY = startPoint.y;
        int gap = 22;

        addIcon(widgets, new Point(gridX, gridY), new ItemStack(Items.GOLDEN_CARROT), SmokerTooltip.formatSaturationLine(recipe.getSaturation()));
        addIcon(widgets, new Point(gridX + gap, gridY), new ItemStack(Items.BREAD), SmokerTooltip.formatNutritionLine(recipe.getNutrition()));

        if (recipe.getHealAmount() > 0) {
            addIcon(widgets, new Point(gridX, gridY + gap), new ItemStack(Items.GOLDEN_APPLE), SmokerTooltip.formatHealLine(recipe.getHealAmount()));
        }

        if (recipe.hasEffect()) {
            MobEffect effect = SmokerTooltip.getEffect(recipe.getEffectName());
            if (effect != null) {
                Point effectPos = new Point(gridX + gap, gridY + gap);
                Holder<MobEffect> holder = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect);
                widgets.add(Widgets.createDrawableWidget((graphics, mouseX, mouseY, delta) -> {
                    TextureAtlasSprite sprite = Minecraft.getInstance().getMobEffectTextures().get(holder);
                    graphics.blit(effectPos.x, effectPos.y, 0, ICON_SIZE, ICON_SIZE, sprite);
                }));
                Component effectLine = SmokerTooltip.formatEffectLine(recipe.getEffectName(), recipe.getEffectDuration());
                if (effectLine != null) {
                    widgets.add(Widgets.createTooltip(new Rectangle(effectPos.x, effectPos.y, ICON_SIZE, ICON_SIZE), effectLine));
                }
            }
        }

        return widgets;
    }

    private static void addIcon(List<Widget> widgets, Point point, ItemStack stack, Component tooltipLine) {
        widgets.add(Widgets.createDrawableWidget((graphics, mouseX, mouseY, delta) ->
                graphics.renderItem(stack, point.x + 1, point.y + 1)));
        widgets.add(Widgets.createTooltip(new Rectangle(point.x, point.y, ICON_SIZE, ICON_SIZE), tooltipLine));
    }
}
