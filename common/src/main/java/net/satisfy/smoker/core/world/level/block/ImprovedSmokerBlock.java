package net.satisfy.smoker.core.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.smoker.core.util.SmokerSmokeColors;
import net.satisfy.smoker.core.world.level.block.entity.ImprovedSmokerBlockEntity;
import net.satisfy.smoker.platform.PlatformHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class ImprovedSmokerBlock extends BaseEntityBlock implements EntityBlock, IImprovedSmoker {
    public static final MapCodec<ImprovedSmokerBlock> CODEC = simpleCodec(ImprovedSmokerBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    /**
     * Cosmetic smoke-color category currently burning (SmokerSmokeColors.NORMAL/DARK/WARM), kept
     * as a blockstate property (rather than read from the block entity's inventory) so it syncs to
     * every nearby client for free, the same way LIT already does - block entity inventory contents
     * are not synced to bystanders.
     */
    public static final IntegerProperty SMOKE_KIND = IntegerProperty.create("smoke_kind", SmokerSmokeColors.NORMAL, SmokerSmokeColors.WARM);

    public ImprovedSmokerBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, net.minecraft.core.Direction.NORTH).setValue(LIT, false).setValue(SMOKE_KIND, SmokerSmokeColors.NORMAL));
    }

    @Override
    public @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(LIT, false).setValue(SMOKE_KIND, SmokerSmokeColors.NORMAL);
    }

    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT, SMOKE_KIND);
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ImprovedSmokerBlockEntity) {
                Containers.dropContents(world, pos, (ImprovedSmokerBlockEntity) blockEntity);
                world.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, world, pos, newState, moved);
        }
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        return smoker$useImprovedSmoker(state, world, pos, player, hit);
    }

    @Override
    public @NotNull InteractionResult smoker$useImprovedSmoker(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (!world.isClientSide) {
            MenuProvider screenHandlerFactory = state.getMenuProvider(world, pos);
            if (screenHandlerFactory != null) {
                player.openMenu(screenHandlerFactory);
            }
        }
        return InteractionResult.sidedSuccess(world.isClientSide);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ImprovedSmokerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return world.isClientSide ? null : (theWorld, pos, theState, blockEntity) -> {
            if (blockEntity instanceof ImprovedSmokerBlockEntity smoker) {
                smoker.tick(theWorld, pos, theState, smoker);
            }
        };
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT)) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 1.5;
            double z = pos.getZ() + 0.5;
            if (level.getBlockState(pos.above()).isAir()) {
                int smokeKind = state.getValue(SMOKE_KIND);
                boolean colored = smokeKind != SmokerSmokeColors.NORMAL && PlatformHelper.isColoredSmokeEnabled();
                net.minecraft.core.particles.SimpleParticleType coloredSmoke = colored ? SmokerSmokeColors.getSmokeParticle(smokeKind) : null;
                net.minecraft.core.particles.SimpleParticleType coloredLargeSmoke = colored ? SmokerSmokeColors.getLargeSmokeParticle(smokeKind) : null;
                if (coloredSmoke != null && coloredLargeSmoke != null) {
                    // Mirror vanilla's own two-particle mix exactly: a quick small puff plus a
                    // slower, much larger and longer-lived column, just tinted.
                    level.addParticle(coloredSmoke, x, y, z, 0.0, 0.05, 0.0);
                    level.addParticle(coloredLargeSmoke, x, y, z, 0.0, 0.02, 0.0);
                } else {
                    level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.05, 0.0);
                    level.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, x, y, z, 0.0, 0.02, 0.0);
                }
                if (random.nextInt(10) == 0) {
                    level.playLocalSound(x, y, z, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.5f, 1.0f, false);
                }
            }
        }
    }
}
