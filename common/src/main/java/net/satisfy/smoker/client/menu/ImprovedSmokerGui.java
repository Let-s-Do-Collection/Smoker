package net.satisfy.smoker.client.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.satisfy.smoker.core.util.SmokerIdentifier;
import org.joml.Vector2i;

public class ImprovedSmokerGui extends AbstractContainerScreen<ImprovedSmokerGuiHandler> {
    public static final ResourceLocation BG = new SmokerIdentifier("textures/gui/improved_smoker.png");
    public static final int ARROW_Y = 35;
    public static final int ARROW_X = 79;

    public ImprovedSmokerGui(ImprovedSmokerGuiHandler handler, Inventory inventory, Component title) {
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
        RenderSystem.setShaderTexture(0, BG);
        guiGraphics.blit(BG, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        renderProgressArrow(guiGraphics);
        renderBurnIcon(guiGraphics);
    }

    protected void renderBurnIcon(GuiGraphics guiGraphics) {
        int burnHeight = menu.getFuelProgress();
        if (burnHeight > 0) {
            Vector2i screenPos = new Vector2i(leftPos + 56, topPos + 36 + (14 - burnHeight));
            Vector2i texPos = new Vector2i(176, 14 - burnHeight);
            guiGraphics.blit(BG, screenPos.x(), screenPos.y(), texPos.x(), texPos.y(), 14, burnHeight);
        }
    }

    protected void renderProgressArrow(GuiGraphics guiGraphics) {
        int progressX = menu.getSmokeXProgress();
        guiGraphics.blit(BG, leftPos + ARROW_X, topPos + ARROW_Y, 177, 15, progressX, 16);
    }
}
