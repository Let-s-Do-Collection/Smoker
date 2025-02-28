package net.satisfy.smoker.core.world.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.satisfy.smoker.client.menu.SmokingFoodMenu;
import net.satisfy.smoker.core.registry.EntityTypeRegistry;
import net.satisfy.smoker.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public class SmokingFoodBlockEntity extends BlockEntity implements Clearable, MenuProvider {
    private final NonNullList<ItemStack> items;
    private final int[] cookingProgress;
    private final int[] cookingTime;
    private final RecipeManager.CachedCheck<Container, CampfireCookingRecipe> quickCheck;

    public SmokingFoodBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.SMOKER.get(), pos, state);
        this.items = NonNullList.withSize(4, ItemStack.EMPTY);
        this.cookingProgress = new int[4];
        this.cookingTime = new int[4];
        this.quickCheck = RecipeManager.createCheck(RecipeType.CAMPFIRE_COOKING);
    }

    public static void cookTick(Level level, BlockPos pos, BlockState state, SmokingFoodBlockEntity smoker) {
        boolean hasItems = false;

        for (int i = 0; i < smoker.items.size(); ++i) {
            ItemStack itemStack = smoker.items.get(i);
            if (!itemStack.isEmpty()) {
                hasItems = true;
                smoker.cookingProgress[i]++;
                if (smoker.cookingProgress[i] >= smoker.cookingTime[i]) {
                    Container container = new SimpleContainer(itemStack);
                    ItemStack result = smoker.quickCheck.getRecipeFor(container, level)
                            .map(recipe -> recipe.assemble(container, level.registryAccess()))
                            .orElse(itemStack);

                    if (result.isItemEnabled(level.enabledFeatures())) {
                        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), result);
                        smoker.items.set(i, ItemStack.EMPTY);
                        level.sendBlockUpdated(pos, state, state, 3);
                        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(state));
                    }
                }
            }
        }

        if (hasItems) {
            setChanged(level, pos, state);
            level.playSound(null, pos, SoundEvents.SMOKER_SMOKE, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    public static void particleTick(Level level, BlockPos pos, BlockState state, SmokingFoodBlockEntity smoker) {
        RandomSource random = level.random;

        if (smoker.items.stream().anyMatch(item -> !item.isEmpty()) && random.nextFloat() < 0.11F) {
            for (int i = 0; i < random.nextInt(2) + 2; ++i) {
                makeParticles(level, pos);
            }
        }

        for (int j = 0; j < smoker.items.size(); ++j) {
            if (!smoker.items.get(j).isEmpty() && random.nextFloat() < 0.2F) {
                double x = pos.getX() + 0.5 + random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1);
                double y = pos.getY() + 1.2;
                double z = pos.getZ() + 0.5 + random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1);
                level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 5.0E-4, 0.0);
            }
        }
    }

    private static void makeParticles(Level level, BlockPos pos) {
        RandomSource random = level.random;
        level.addAlwaysVisibleParticle(ParticleTypes.SMOKE, true,
                pos.getX() + 0.5 + random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1),
                pos.getY() + 1.3 + random.nextDouble() + random.nextDouble(),
                pos.getZ() + 0.5 + random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1),
                0.0, 0.07, 0.0);
    }

    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    public boolean insertItem(ItemStack stack) {
        for (int i = 0; i < this.items.size(); i++) {
            if (this.items.get(i).isEmpty()) {
                this.cookingProgress[i] = 0;
                this.cookingTime[i] = this.getCookingTimeFor(stack);
                this.items.set(i, stack.copyWithCount(1));
                setChanged();
                return true;
            }
        }
        return false;
    }

    private int getCookingTimeFor(ItemStack stack) {
        return this.quickCheck.getRecipeFor(new SimpleContainer(stack), this.level)
                .map(CampfireCookingRecipe::getCookingTime)
                .orElse(100);
    }

    public void load(CompoundTag tag) {
        super.load(tag);
        this.items.clear();
        ContainerHelper.loadAllItems(tag, this.items);
        if (tag.contains("CookingTimes", 11)) {
            int[] times = tag.getIntArray("CookingTimes");
            System.arraycopy(times, 0, this.cookingProgress, 0, Math.min(this.cookingTime.length, times.length));
        }
        if (tag.contains("CookingTotalTimes", 11)) {
            int[] totalTimes = tag.getIntArray("CookingTotalTimes");
            System.arraycopy(totalTimes, 0, this.cookingTime, 0, Math.min(this.cookingTime.length, totalTimes.length));
        }
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new SmokingFoodMenu(syncId, playerInventory, new SimpleContainer(4), new SimpleContainerData(4));
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.items, true);
        tag.putIntArray("CookingTimes", this.cookingProgress);
        tag.putIntArray("CookingTotalTimes", this.cookingTime);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }



    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag compoundTag = new CompoundTag();
        ContainerHelper.saveAllItems(compoundTag, this.items, true);
        return compoundTag;
    }

    public Optional<CampfireCookingRecipe> getCookableRecipe(ItemStack stack) {
        return this.items.stream().noneMatch(ItemStack::isEmpty) ? Optional.empty() : this.quickCheck.getRecipeFor(new SimpleContainer(stack), this.level);
    }

    public boolean placeFood(Entity entity, ItemStack stack, int cookTime) {
        for (int i = 0; i < this.items.size(); ++i) {
            ItemStack itemStack = this.items.get(i);
            if (itemStack.isEmpty()) {
                this.cookingTime[i] = cookTime;
                this.cookingProgress[i] = 0;
                this.items.set(i, stack.split(1));
                assert this.level != null;
                this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.Context.of(entity, this.getBlockState()));
                this.markUpdated();
                return true;
            }
        }
        return false;
    }
    private void markUpdated() {
        this.setChanged();
        Objects.requireNonNull(this.getLevel()).sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    public void clearContent() {
        this.items.clear();
    }

    @Override
    public @NotNull Component getDisplayName() {
        return ObjectRegistry.SMOKER.get().getName();
    }
}
