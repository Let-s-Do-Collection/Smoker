package net.satisfy.smoker.core.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public interface IImprovedSmoker {
    InteractionResult smoker$useImprovedSmoker(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit);
}
