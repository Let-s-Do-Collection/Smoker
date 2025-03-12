package net.satisfy.smoker.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.satisfy.smoker.core.world.inventory.ImprovedSmokerMenu;
import net.satisfy.smoker.core.util.SmokerIdentifier;

public class ImprovedSmokerScreen extends AbstractContainerScreen<ImprovedSmokerMenu> {
    public static final ResourceLocation BG = new SmokerIdentifier("textures/gui/improved_smoker.png");
    public static final int ARROW_Y = 35;
    public static final int ARROW_X = 79;

    public ImprovedSmokerScreen(ImprovedSmokerMenu handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BG, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        renderProgressArrow(guiGraphics, mouseX, mouseY);
        renderBurnIcon(guiGraphics, mouseX, mouseY);
    }

    protected void renderBurnIcon(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int burnHeight = menu.getFuelProgress();
        int fuelSlotIndex = 2;
        ItemStack fuelStack = menu.getSlot(fuelSlotIndex).getItem();

        int burnIconX = leftPos + 56;
        int burnIconY = topPos + 36 + (14 - burnHeight);

        if (burnHeight > 0) {
            guiGraphics.blit(BG, burnIconX, burnIconY, 176, 14 - burnHeight, 14, burnHeight);
        }

        if (isMouseOverBurnIcon(mouseX, mouseY) && !fuelStack.isEmpty()) {
            Component tooltip = getFuelTooltip(fuelStack);
            guiGraphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
        }
    }

    private boolean isMouseOverBurnIcon(int mouseX, int mouseY) {
        int burnIconX = leftPos + 56;
        int burnIconY = topPos + 36;
        return mouseX >= burnIconX && mouseX <= burnIconX + 14 &&
                mouseY >= burnIconY && mouseY <= burnIconY + 14;
    }

    private Component getFuelTooltip(ItemStack fuelStack) {
        return Component.translatable("tooltip.smoker.block.current_smoking_wood", fuelStack.getHoverName());
    }

    protected void renderProgressArrow(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int progressX = menu.getSmokeXProgress();
        guiGraphics.blit(BG, leftPos + ARROW_X, topPos + ARROW_Y, 177, 15, progressX, 16);
        if(isMouseOverArrow(mouseX, mouseY)) {
            int remainingTicks = menu.getRemainingSmokeTime();
            int totalSeconds = remainingTicks / 20;
            int hours = totalSeconds / 3600;
            int minutes = (totalSeconds % 3600) / 60;
            int seconds = totalSeconds % 60;
            String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
            Component tooltip = Component.translatable("tooltip.smoker.block.remaining_duration", formattedTime);
            guiGraphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
        }
    }

    private boolean isMouseOverArrow(int mouseX, int mouseY) {
        int arrowX = leftPos + ARROW_X;
        int arrowY = topPos + ARROW_Y;
        int arrowWidth = 24;
        int arrowHeight = 16;
        return mouseX >= arrowX && mouseX <= arrowX + arrowWidth &&
                mouseY >= arrowY && mouseY <= arrowY + arrowHeight;
    }
}
