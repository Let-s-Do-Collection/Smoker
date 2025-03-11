package net.satisfy.smoker.core.world.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.smoker.client.menu.ImprovedSmokerGuiHandler;
import net.satisfy.smoker.core.recipe.SmokerModifierRecipe;
import net.satisfy.smoker.core.registry.EntityTypeRegistry;
import net.satisfy.smoker.core.registry.ObjectRegistry;
import net.satisfy.smoker.core.registry.RecipeTypeRegistry;
import net.satisfy.smoker.core.world.ImplementedInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ImprovedSmokerBlockEntity extends BlockEntity implements ImplementedInventory, BlockEntityTicker<ImprovedSmokerBlockEntity>, MenuProvider {
    public static final int CAPACITY = 3;
    private static final int OUTPUT_SLOT = 0;
    private static final int INPUT_SLOT = 1;
    private static final int FUEL_SLOT = 2;
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
        super(EntityTypeRegistry.IMPROVED_SMOKER.get(), pos, state);
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
        if (fuelTime > 0) {
            fuelTime--;
        }
        if (fuelTime == 0 && !getItem(FUEL_SLOT).isEmpty()) {
            SimpleContainer container = new SimpleContainer(getItem(FUEL_SLOT));
            currentRecipe = world.getRecipeManager().getRecipeFor(RecipeTypeRegistry.SMOKER_RECIPE_TYPE.get(), container, world).orElse(null);
            if (currentRecipe != null) {
                fuelTime = 100;
                getItem(FUEL_SLOT).shrink(1);
                dirty = true;
            }
        }
        if (canProcessInput() && fuelTime > 0 && currentRecipe != null) {
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
        } else {
            smokingTime = 0;
        }
        if (dirty) {
            setChanged();
        }
    }

    private boolean canProcessInput() {
        ItemStack input = getItem(INPUT_SLOT);
        return !input.isEmpty() && input.isEdible();
    }

    private void processInput(SmokerModifierRecipe recipe) {
        ItemStack input = getItem(INPUT_SLOT);
        if (input.isEmpty() || !input.isEdible()) return;
        ItemStack output = input.copy();
        output.setCount(1);
        CompoundTag tag = output.getOrCreateTag();
        tag.putDouble("smoker_saturation", recipe.getSaturation());
        tag.putDouble("smoker_nutrition", recipe.getNutrition());
        tag.putBoolean("SmokerProcessed", true);

        setItem(OUTPUT_SLOT, output);
        input.shrink(1);
        if (input.getCount() <= 0) {
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
        return ObjectRegistry.IMPROVED_SMOKER.get().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        return new ImprovedSmokerGuiHandler(syncId, inv, this, propertyDelegate);
    }
}
