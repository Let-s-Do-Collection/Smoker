package net.satisfy.smoker.core.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SmokerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.smoker.core.world.level.block.entity.ImprovedSmokerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SmokerBlock.class)
public abstract class SmokerBlockMixin {

    @Inject(method = "newBlockEntity", at = @At("HEAD"), cancellable = true)
    private void replaceBlockEntity(BlockPos pos, BlockState state, CallbackInfoReturnable<BlockEntity> cir) {
        cir.setReturnValue(new ImprovedSmokerBlockEntity(pos, state));
    }

    @Inject(method = "getTicker", at = @At("HEAD"), cancellable = true)
    private void overrideTicker(Level level, BlockState state, BlockEntityType<?> type, CallbackInfoReturnable<BlockEntityTicker<?>> cir) {
        if (type == net.satisfy.smoker.core.registry.CommonRegistry.IMPROVED_SMOKER_ENTITY.get()) {
            cir.setReturnValue((lvl, pos, blockState, be) -> {
                if (be instanceof ImprovedSmokerBlockEntity smoker) {
                    smoker.tick(lvl, pos, blockState, smoker);
                }
            });
        }
    }
}
