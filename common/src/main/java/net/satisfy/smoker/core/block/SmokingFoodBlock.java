package net.satisfy.smoker.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.smoker.core.block.entity.SmokingFoodBlockEntity;

public class SmokingFoodBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public SmokingFoodBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hit.getDirection() != Direction.UP) {
            if (!level.isClientSide()) {
                if (level.getBlockEntity(pos) instanceof SmokingFoodBlockEntity be) {
                    player.openMenu(be);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public SmokingFoodBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SmokingFoodBlockEntity(pos, state);
    }
}
