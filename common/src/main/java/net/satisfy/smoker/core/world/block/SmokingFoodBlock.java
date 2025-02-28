package net.satisfy.smoker.core.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.Containers;
import net.satisfy.smoker.core.registry.EntityTypeRegistry;
import net.satisfy.smoker.core.world.block.entity.SmokingFoodBlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@SuppressWarnings("deprecation")
public class SmokingFoodBlock extends BaseEntityBlock implements EntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public SmokingFoodBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @NotNull BlockState rotate(BlockState state, net.minecraft.world.level.block.Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public @NotNull BlockState mirror(BlockState state, net.minecraft.world.level.block.Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }

    @Override
    public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {
            if (level.getBlockEntity(pos) instanceof SmokingFoodBlockEntity be) {
                ItemStack heldItem = player.getItemInHand(hand);
                if (!heldItem.isEmpty() && hit.getDirection() == Direction.UP) {
                    ItemStack itemStack = player.getItemInHand(hand);
                    Optional<CampfireCookingRecipe> optional = be.getCookableRecipe(itemStack);
                    if (optional.isPresent()) {
                        if (!level.isClientSide && be.placeFood(player, player.getAbilities().instabuild ? itemStack.copy() : itemStack, optional.get().getCookingTime())) {
                            return InteractionResult.SUCCESS;
                        }
                        return InteractionResult.CONSUME;
                    }
                } else {
                    player.openMenu(be);
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof SmokingFoodBlockEntity be) {
                Containers.dropContents(level, pos, be.getItems());
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, newState, moved);
        }
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public <T extends net.minecraft.world.level.block.entity.BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(Level level, BlockState state, net.minecraft.world.level.block.entity.BlockEntityType<T> type) {
        return createTickerHelper(type, EntityTypeRegistry.SMOKER.get(), level.isClientSide ? SmokingFoodBlockEntity::particleTick : SmokingFoodBlockEntity::cookTick);
    }

    @Override
    public SmokingFoodBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SmokingFoodBlockEntity(pos, state);
    }
}
