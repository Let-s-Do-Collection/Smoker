package net.satisfy.smoker.core.mixin;
/*
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.SmokerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.smoker.core.world.block.IImprovedSmoker;
import net.satisfy.smoker.core.world.block.entity.ImprovedSmokerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFurnaceBlock.class)
public abstract class AbstractFurnaceBlockMixin implements IImprovedSmoker {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void modifyUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        // Sicherstellen, dass wir nur Smoker beeinflussen
        if ((Object) this instanceof SmokerBlock) {
            if (!level.isClientSide()) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof ImprovedSmokerBlockEntity be) {
                    ItemStack heldItem = player.getItemInHand(hand);

                    // Falls der Spieler auf die Oberseite klickt und ein Item hält, versuchen zu kochen
                    if (!heldItem.isEmpty() && hit.getDirection() == Direction.UP) {
                        if (tryPlaceFood(player, be, heldItem)) {
                            cir.setReturnValue(InteractionResult.SUCCESS);
                            return;
                        }
                    }

                    // Falls kein Essen platziert wird, öffne das eigene Menü
                    player.openMenu(be);
                    cir.setReturnValue(InteractionResult.SUCCESS);
                }
            }
        }
    }
}

 */
