package net.satisfy.smoker.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.satisfy.smoker.core.world.inventory.ImprovedSmokerMenu;
import net.satisfy.smoker.core.util.SmokerIdentifier;

public class ImprovedSmokerScreen extends AbstractContainerScreen<ImprovedSmokerMenu> {
    public static final ResourceLocation BG = SmokerIdentifier.id("textures/gui/smoking_station.png");

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

        renderProgressArrow(guiGraphics);
        renderBurnIcon(guiGraphics);
    }

    protected void renderBurnIcon(GuiGraphics guiGraphics) {
        if (menu.isLit()) {
            guiGraphics.blit(BG, leftPos + 42, topPos + 36, 176, 0, 46, 32);
        }
    }

    protected void renderProgressArrow(GuiGraphics guiGraphics) {
        int progressHeight = menu.getSmokeXProgress();
        if (progressHeight > 0) {
            int drawX = 60;
            int drawY = 23 + (10 - progressHeight);
            int textureX = 176;
            int textureY = 32 + (10 - progressHeight);
            int width = 10;
            guiGraphics.blit(BG, leftPos + drawX, topPos + drawY, textureX, textureY, width, progressHeight);
        }
    }
}
