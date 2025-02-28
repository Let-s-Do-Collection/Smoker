package net.satisfy.smoker.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.satisfy.smoker.core.world.block.SmokingFoodBlock;
import net.satisfy.smoker.core.world.block.entity.SmokingFoodBlockEntity;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public class SmokingFoodBlockRenderer implements BlockEntityRenderer<SmokingFoodBlockEntity> {
    private final ItemRenderer itemRenderer;

    public SmokingFoodBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    public void render(SmokingFoodBlockEntity smokingFoodBlockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
        Direction direction = smokingFoodBlockEntity.getBlockState().getValue(SmokingFoodBlock.FACING);
        NonNullList<ItemStack> nonNullList = smokingFoodBlockEntity.getItems();
        int k = (int) smokingFoodBlockEntity.getBlockPos().asLong();
        for (int l = 0; l < nonNullList.size(); ++l) {
            ItemStack itemStack = nonNullList.get(l);
            if (!itemStack.isEmpty()) {
                poseStack.pushPose();
                poseStack.translate(0.5F, 1.025, 0.5F);
                Direction direction2 = Direction.from2DDataValue((l + direction.get2DDataValue()) % 4);
                float g = -direction2.toYRot();
                poseStack.mulPose(new Quaternionf().rotateY((float)Math.toRadians(-Direction.from2DDataValue((l + direction.get2DDataValue()) % 4).toYRot())));
                poseStack.mulPose(new Quaternionf().rotateX((float)Math.toRadians(90.0F)));
                poseStack.translate(-0.3125F, -0.3125F, 0.0F);
                poseStack.scale(0.375F, 0.375F, 0.375F);
                this.itemRenderer.renderStatic(itemStack, ItemDisplayContext.FIXED, i, j, poseStack, multiBufferSource, smokingFoodBlockEntity.getLevel(), k + l);
                poseStack.popPose();
            }
        }
    }
}
