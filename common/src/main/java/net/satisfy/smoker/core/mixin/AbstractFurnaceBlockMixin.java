package net.satisfy.smoker.core.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.smoker.core.world.level.block.IImprovedSmoker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFurnaceBlock.class)
public abstract class AbstractFurnaceBlockMixin implements IImprovedSmoker {

    @Override
    public InteractionResult smoker$useImprovedSmoker(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!world.isClientSide) {
            MenuProvider provider = state.getMenuProvider(world, pos);
            if (provider != null) {
                player.openMenu(provider);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void abstractFurnaceUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        cir.setReturnValue(smoker$useImprovedSmoker(state, world, pos, player, hand, hit));
    }
}
