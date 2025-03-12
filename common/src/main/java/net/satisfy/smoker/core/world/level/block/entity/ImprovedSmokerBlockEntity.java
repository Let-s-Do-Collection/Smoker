package net.satisfy.smoker.core.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.smoker.core.registry.CommonRegistry;
import net.satisfy.smoker.core.world.inventory.ImprovedSmokerMenu;
import net.satisfy.smoker.core.recipe.SmokerModifierRecipe;
import net.satisfy.smoker.core.world.inventory.ImplementedInventory;
import net.satisfy.smoker.core.world.level.block.ImprovedSmokerBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ImprovedSmokerBlockEntity extends BlockEntity implements ImplementedInventory, BlockEntityTicker<ImprovedSmokerBlockEntity>, MenuProvider {
    public static final int CAPACITY = 3;
    private static final int OUTPUT_SLOT = 0;
    private static final int INPUT_SLOT = 1;
    private static final int FUEL_SLOT = 2;
    private static final int PLANKS_FUEL_TIME = 200;
    private int fuelTime = 0;
    private int smokingTime = 0;
    private int totalSmokingTime = 0;

    private SmokerModifierRecipe currentRecipe;
    private NonNullList<ItemStack> inventory;
    private final ContainerData propertyDelegate = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> smokingTime;
                case 1 -> totalSmokingTime;
                case 2 -> fuelTime;
                default -> 0;
            };
        }
        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> smokingTime = value;
                case 1 -> totalSmokingTime = value;
                case 2 -> fuelTime = value;
            }
        }
        @Override
        public int getCount() {
            return 3;
        }
    };

    public ImprovedSmokerBlockEntity(BlockPos pos, BlockState state) {
        super(CommonRegistry.IMPROVED_SMOKER_ENTITY.get(), pos, state);
        this.inventory = NonNullList.withSize(CAPACITY, ItemStack.EMPTY);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.inventory = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(nbt, this.inventory);
        smokingTime = nbt.getInt("SmokingTime");
        totalSmokingTime = nbt.getInt("TotalSmokingTime");
        fuelTime = nbt.getInt("FuelTime");
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        ContainerHelper.saveAllItems(nbt, this.inventory);
        nbt.putInt("SmokingTime", smokingTime);
        nbt.putInt("TotalSmokingTime", totalSmokingTime);
        nbt.putInt("FuelTime", fuelTime);
    }

    @Override
    public void tick(Level world, BlockPos pos, BlockState state, ImprovedSmokerBlockEntity blockEntity) {
        if (world.isClientSide) return;
        boolean dirty = false;
        boolean wasLit = state.getValue(ImprovedSmokerBlock.LIT);
        boolean isLit = false;
        if (fuelTime > 0) {
            fuelTime--;
            isLit = true;
        }
        if (fuelTime == 0 && !getItem(FUEL_SLOT).isEmpty() && !getItem(INPUT_SLOT).isEmpty()) {
            SimpleContainer container = new SimpleContainer(getItem(INPUT_SLOT));
            currentRecipe = world.getRecipeManager().getRecipeFor(CommonRegistry.SMOKER_RECIPE_TYPE.get(), container, world).orElse(null);
            if (currentRecipe != null) {
                fuelTime = PLANKS_FUEL_TIME;
                getItem(FUEL_SLOT).shrink(1);
                dirty = true;
                isLit = true;
            } else if (Ingredient.of(ItemTags.PLANKS).test(getItem(FUEL_SLOT))) {
                fuelTime = PLANKS_FUEL_TIME;
                getItem(FUEL_SLOT).shrink(1);
                dirty = true;
                isLit = true;
            }
        }
        if (fuelTime > 0) {
            if (currentRecipe != null && getItem(INPUT_SLOT).isEdible()) {
                if (smokingTime == 0) {
                    totalSmokingTime = currentRecipe.getCraftingTime();
                }
                smokingTime++;
                if (smokingTime >= totalSmokingTime) {
                    smokingTime = 0;
                    processInput(currentRecipe);
                    dirty = true;
                    currentRecipe = null;
                }
            } else if (!getItem(INPUT_SLOT).isEmpty() && Ingredient.of(ItemTags.PLANKS).test(getItem(FUEL_SLOT))) {
                if (smokingTime == 0) {
                    totalSmokingTime = 200;
                }
                smokingTime++;
                if (smokingTime >= totalSmokingTime) {
                    smokingTime = 0;
                    processFuelDefault();
                    dirty = true;
                }
            }
        } else {
            smokingTime = 0;
        }
        if (wasLit != isLit) {
            world.setBlock(pos, state.setValue(ImprovedSmokerBlock.LIT, isLit), 3);
        }
        if (dirty) {
            setChanged();
        }
    }

    private void processFuelDefault() {
        ItemStack inputStack = getItem(INPUT_SLOT);
        if (inputStack.isEmpty()) return;

        ItemStack output = new ItemStack(inputStack.getItem(), 1);
        CompoundTag tag = output.getOrCreateTag();
        tag.putDouble("smoker_saturation", 0.05);
        tag.putDouble("smoker_nutrition", 0.05);
        tag.putBoolean("SmokerProcessed", true);

        ItemStack currentOutput = getItem(OUTPUT_SLOT);
        if (currentOutput.isEmpty()) {
            setItem(OUTPUT_SLOT, output);
        } else if (ItemStack.isSameItemSameTags(currentOutput, output)) {
            currentOutput.grow(1);
        }

        inputStack.shrink(1);
        if (inputStack.isEmpty()) {
            setItem(INPUT_SLOT, ItemStack.EMPTY);
        }

        setChanged();
    }

    private void processInput(SmokerModifierRecipe recipe) {
        ItemStack input = getItem(INPUT_SLOT);
        if (input.isEmpty() || !input.isEdible()) return;

        ItemStack output = input.copy();
        output.setCount(1);
        CompoundTag tag = output.getOrCreateTag();
        if (recipe.getSaturation() > 0.0) {
            tag.putDouble("smoker_saturation", recipe.getSaturation());
        }

        if (recipe.getNutrition() > 0.0) {
            tag.putDouble("smoker_nutrition", recipe.getNutrition());
        }

        if (recipe.getHealAmount() > 0) {
            tag.putInt("smoker_heal_amount", recipe.getHealAmount());
        }

        if (recipe.hasEffect()) {
            tag.putString("smoker_effect", recipe.getEffectName());
            tag.putInt("smoker_effect_duration", recipe.getEffectDuration());
        }

        ItemStack currentOutput = getItem(OUTPUT_SLOT);
        if (ItemStack.isSameItemSameTags(currentOutput, output)) {
            currentOutput.grow(1);
        } else {
            setItem(OUTPUT_SLOT, output);
        }

        input.shrink(1);
        if (input.isEmpty()) {
            setItem(INPUT_SLOT, ItemStack.EMPTY);
        }
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public int @NotNull [] getSlotsForFace(Direction side) {
        return side == Direction.DOWN ? new int[]{OUTPUT_SLOT} : new int[]{INPUT_SLOT, FUEL_SLOT};
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot == INPUT_SLOT || slot == FUEL_SLOT;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return slot == OUTPUT_SLOT;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        inventory.set(slot, stack);
        if (slot == INPUT_SLOT) {
            smokingTime = 0;
            setChanged();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return level != null && level.getBlockEntity(worldPosition) == this && player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return CommonRegistry.IMPROVED_SMOKER.get().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        return new ImprovedSmokerMenu(syncId, inv, this, propertyDelegate);
    }
}
