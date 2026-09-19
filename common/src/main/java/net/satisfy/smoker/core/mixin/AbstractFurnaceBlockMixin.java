package net.satisfy.smoker.core.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.smoker.core.world.level.block.IImprovedSmoker;
import net.satisfy.smoker.platform.PlatformHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFurnaceBlock.class)
public abstract class AbstractFurnaceBlockMixin implements IImprovedSmoker {

    @Override
    public InteractionResult smoker$useImprovedSmoker(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (!world.isClientSide) {
            MenuProvider provider = state.getMenuProvider(world, pos);
            if (provider != null) {
                player.openMenu(provider);
            }
        }
        return InteractionResult.sidedSuccess(world.isClientSide);
    }

    @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true)
    private void abstractFurnaceUse(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (!PlatformHelper.isVanillaSmokerReplacementEnabled()) return;
        cir.setReturnValue(smoker$useImprovedSmoker(state, world, pos, player, hit));
    }
}
