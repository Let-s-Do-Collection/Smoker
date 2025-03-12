package net.satisfy.smoker.core.mixin;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SmokerBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.smoker.core.registry.CommonRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityType.class)
public abstract class BlockEntityTypeMixin {
    @Inject(method = "isValid", at = @At("HEAD"), cancellable = true)
    private void improvedSmoker_isValid(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if ((Object)this == CommonRegistry.IMPROVED_SMOKER_ENTITY.get()) {
            Block block = state.getBlock();
            if (block instanceof SmokerBlock) {
                cir.setReturnValue(true);
            }
        }
    }
}
