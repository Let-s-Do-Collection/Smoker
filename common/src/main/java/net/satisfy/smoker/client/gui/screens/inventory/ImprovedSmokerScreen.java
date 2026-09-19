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
    public static final ResourceLocation BG = SmokerIdentifier.id("textures/gui/improved_smoker.png");
    private int drawX;
    private int drawY;

    public ImprovedSmokerScreen(ImprovedSmokerMenu handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Screen.render() already calls renderBackground(...) itself (which in turn calls our
        // renderBg()) - calling it again here doubled the dark transparent overlay and drew the
        // GUI texture/arrow/burn icon twice.
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
        int smokingSlotIndex = 2;
        ItemStack smokingStack = menu.getSlot(smokingSlotIndex).getItem();

        if (menu.isLit()) {
            guiGraphics.blit(BG, leftPos + 42, topPos + 36, 176, 0, 46, 32);
        }

        if (isMouseOverBurnIcon(mouseX, mouseY) && !smokingStack.isEmpty()) {
            Component tooltip = getSmokingTooltip(smokingStack);
            guiGraphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
        }
    }


    private boolean isMouseOverBurnIcon(int mouseX, int mouseY) {
        int burnIconX = leftPos + 56;
        int burnIconY = topPos + 36;
        return mouseX >= burnIconX && mouseX <= burnIconX + 14 &&
                mouseY >= burnIconY && mouseY <= burnIconY + 14;
    }

    private Component getSmokingTooltip(ItemStack smokingStack) {
        return Component.translatable("tooltip.smoker.block.current_smoking_wood", smokingStack.getHoverName());
    }

    protected void renderProgressArrow(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int progressHeight = menu.getSmokeXProgress();
        if (progressHeight > 0) {
            drawX = leftPos + 60;
            drawY = topPos + 23 + (10 - progressHeight);
            int textureX = 176;
            int textureY = 32 + (10 - progressHeight);
            int width = 10;
            guiGraphics.blit(BG, drawX, drawY, textureX, textureY, width, progressHeight);
        }
        if (isMouseOverArrow(mouseX, mouseY)) {
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
        int arrowX = leftPos + drawX;
        int arrowY = topPos + drawY;
        int arrowWidth = 25;
        int arrowHeight = 10;
        return mouseX >= arrowX && mouseX <= arrowX + arrowWidth &&
                mouseY >= arrowY && mouseY <= arrowY + arrowHeight;
    }
}
