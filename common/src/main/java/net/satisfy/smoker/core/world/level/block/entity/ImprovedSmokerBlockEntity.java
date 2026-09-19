package net.satisfy.smoker.core.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.smoker.core.recipe.SmokerModifierRecipe;
import net.satisfy.smoker.core.recipe.input.SmokerRecipeInput;
import net.satisfy.smoker.core.registry.CommonRegistry;
import net.satisfy.smoker.core.registry.TagsRegistry;
import net.satisfy.smoker.core.util.SmokerFoodData;
import net.satisfy.smoker.core.world.inventory.ImplementedInventory;
import net.satisfy.smoker.core.world.inventory.ImprovedSmokerMenu;
import net.satisfy.smoker.core.world.level.block.ImprovedSmokerBlock;
import net.satisfy.smoker.platform.PlatformHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ImprovedSmokerBlockEntity extends BlockEntity implements ImplementedInventory, BlockEntityTicker<ImprovedSmokerBlockEntity>, MenuProvider {
    public static final int CAPACITY = 3;
    private static final int OUTPUT_SLOT = 0;
    private static final int INPUT_SLOT = 1;
    private static final int SMOKINGMATERIAL_SLOT = 2;
    private static final int MATERIAL_BURN_TIME = 200;
    private int burnTime = 0;
    private int smokingTime = 0;
    private int totalSmokingTime = 0;
    private int litValue = 0;
    private int restTime = 0;

    private SmokerModifierRecipe currentRecipe;
    private NonNullList<ItemStack> inventory;
    private final ContainerData propertyDelegate = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> smokingTime;
                case 1 -> totalSmokingTime;
                case 2 -> burnTime;
                case 3 -> litValue;
                default -> 0;
            };
        }
        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> smokingTime = value;
                case 1 -> totalSmokingTime = value;
                case 2 -> burnTime = value;
                case 3 -> litValue = value;
            }
        }
        @Override
        public int getCount() {
            return 4;
        }
    };

    public ImprovedSmokerBlockEntity(BlockPos pos, BlockState state) {
        super(CommonRegistry.IMPROVED_SMOKER_ENTITY.get(), pos, state);
        this.inventory = NonNullList.withSize(CAPACITY, ItemStack.EMPTY);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
        super.loadAdditional(nbt, provider);
        this.inventory = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(nbt, this.inventory, provider);

        smokingTime = nbt.getInt("SmokingTime");
        totalSmokingTime = nbt.getInt("TotalSmokingTime");
        burnTime = nbt.getInt("BurnTime");
        litValue = nbt.getInt("LitValue");
        restTime = nbt.getInt("RestTime");
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
        super.saveAdditional(nbt, provider);
        ContainerHelper.saveAllItems(nbt, this.inventory, provider);
        nbt.putInt("SmokingTime", smokingTime);
        nbt.putInt("TotalSmokingTime", totalSmokingTime);
        nbt.putInt("BurnTime", burnTime);
        nbt.putInt("LitValue", litValue);
        nbt.putInt("RestTime", restTime);
    }

    @Override
    public void tick(Level world, BlockPos pos, BlockState state, ImprovedSmokerBlockEntity blockEntity) {
        if (world.isClientSide) return;
        boolean dirty = false;
        boolean wasLit = state.getValue(ImprovedSmokerBlock.LIT);
        boolean isLit = false;
        if (burnTime > 0) {
            burnTime--;
            isLit = true;
        }
        if (burnTime == 0 && !getItem(SMOKINGMATERIAL_SLOT).isEmpty() && !getItem(INPUT_SLOT).isEmpty()) {
            SmokerRecipeInput input = new SmokerRecipeInput(getItem(SMOKINGMATERIAL_SLOT), getItem(INPUT_SLOT));
            currentRecipe = world.getRecipeManager().getRecipeFor(CommonRegistry.SMOKER_RECIPE_TYPE.get(), input, world).map(RecipeHolder::value).orElse(null);
            if (currentRecipe != null) {
                burnTime = MATERIAL_BURN_TIME;
                getItem(SMOKINGMATERIAL_SLOT).shrink(1);
                dirty = true;
                isLit = true;
            } else if (Ingredient.of(ItemTags.PLANKS).test(getItem(SMOKINGMATERIAL_SLOT))) {
                burnTime = MATERIAL_BURN_TIME;
                getItem(SMOKINGMATERIAL_SLOT).shrink(1);
                dirty = true;
                isLit = true;
            }
        }
        if (burnTime > 0) {
            if (currentRecipe != null) {
                if (smokingTime == 0) {
                    totalSmokingTime = currentRecipe.getCraftingTime();
                }
                smokingTime++;
                if (smokingTime >= totalSmokingTime) {
                    if (processInput(currentRecipe)) {
                        smokingTime = 0;
                        currentRecipe = null;
                    } else {
                        smokingTime = totalSmokingTime;
                    }
                    dirty = true;
                }
            } else if (!getItem(INPUT_SLOT).isEmpty() && TagsRegistry.isSmokable(getItem(INPUT_SLOT))
                    && Ingredient.of(ItemTags.PLANKS).test(getItem(SMOKINGMATERIAL_SLOT))) {
                if (smokingTime == 0) {
                    totalSmokingTime = 200;
                }
                smokingTime++;
                if (smokingTime >= totalSmokingTime) {
                    if (processSmokingMaterialDefault()) {
                        smokingTime = 0;
                    } else {
                        smokingTime = totalSmokingTime;
                    }
                    dirty = true;
                }
            }
        } else {
            smokingTime = 0;
        }
        if (updateMaturity(isLit)) {
            dirty = true;
        }
        if (wasLit != isLit) {
            world.setBlock(pos, state.setValue(ImprovedSmokerBlock.LIT, isLit), 3);
        }
        propertyDelegate.set(3, isLit ? 1 : 0);
        if (dirty) {
            setChanged();
        }
    }

    /**
     * A finished item left resting in the (still lit) smoker "perfects" after a while for a
     * small saturation/nutrition bonus and a glint, then quietly reverts to its normal smoked
     * stats if left for much longer. No fail state either way: taking it any time before that
     * still gives at least the standard result, and forgetting about it never makes it worse.
     */
    private boolean updateMaturity(boolean isLit) {
        ItemStack output = getItem(OUTPUT_SLOT);
        if (output.isEmpty() || !isLit) {
            boolean wasTicking = restTime != 0;
            restTime = 0;
            return wasTicking;
        }

        restTime++;
        int perfectAfterTicks = PlatformHelper.getPerfectSmokingDelaySeconds() * 20;
        int revertAfterTicks = PlatformHelper.getPerfectSmokingRevertSeconds() * 20;
        boolean isPerfect = SmokerFoodData.isPerfect(output);
        if (!isPerfect && restTime >= perfectAfterTicks && restTime < revertAfterTicks) {
            SmokerFoodData.setPerfect(output, true);
            return true;
        } else if (isPerfect && restTime >= revertAfterTicks) {
            SmokerFoodData.setPerfect(output, false);
            return true;
        }
        return false;
    }

    private boolean processSmokingMaterialDefault() {
        ItemStack inputStack = getItem(INPUT_SLOT);
        if (inputStack.isEmpty()) return true;

        ItemStack output = new ItemStack(inputStack.getItem(), 1);
        CompoundTag tag = SmokerFoodData.getOrCreateTag(output);
        tag.putDouble(SmokerFoodData.SATURATION_KEY, 0.05);
        tag.putDouble(SmokerFoodData.NUTRITION_KEY, 0.05);
        tag.putBoolean(SmokerFoodData.PROCESSED_KEY, true);
        SmokerFoodData.applyTag(output, tag);

        ItemStack currentOutput = getItem(OUTPUT_SLOT);
        if (currentOutput.isEmpty()) {
            setItem(OUTPUT_SLOT, output);
        } else if (ItemStack.isSameItemSameComponents(currentOutput, output) && currentOutput.getCount() < currentOutput.getMaxStackSize()) {
            currentOutput.grow(1);
        } else {
            // Output slot holds an incompatible or full stack (e.g. one still maturing towards
            // "perfect") - wait rather than overwrite it and lose that item.
            return false;
        }

        inputStack.shrink(1);
        if (inputStack.isEmpty()) {
            setItem(INPUT_SLOT, ItemStack.EMPTY);
        }

        setChanged();
        return true;
    }

    private boolean processInput(SmokerModifierRecipe recipe) {
        ItemStack input = getItem(INPUT_SLOT);
        if (input.isEmpty() || !input.has(DataComponents.FOOD)) return true;

        ItemStack output = input.copy();
        output.setCount(1);
        CompoundTag tag = SmokerFoodData.getOrCreateTag(output);
        if (recipe.getSaturation() > 0.0) {
            tag.putDouble(SmokerFoodData.SATURATION_KEY, recipe.getSaturation());
        }

        if (recipe.getNutrition() > 0.0) {
            tag.putDouble(SmokerFoodData.NUTRITION_KEY, recipe.getNutrition());
        }

        if (recipe.getHealAmount() > 0) {
            tag.putInt(SmokerFoodData.HEAL_KEY, recipe.getHealAmount());
        }

        if (recipe.hasEffect()) {
            tag.putString(SmokerFoodData.EFFECT_KEY, recipe.getEffectName());
            tag.putInt(SmokerFoodData.EFFECT_DURATION_KEY, recipe.getEffectDuration());
        }
        tag.putBoolean(SmokerFoodData.PROCESSED_KEY, true);
        SmokerFoodData.applyTag(output, tag);

        ItemStack currentOutput = getItem(OUTPUT_SLOT);
        if (currentOutput.isEmpty()) {
            setItem(OUTPUT_SLOT, output);
        } else if (ItemStack.isSameItemSameComponents(currentOutput, output) && currentOutput.getCount() < currentOutput.getMaxStackSize()) {
            currentOutput.grow(1);
        } else {
            // Output slot holds an incompatible or full stack (e.g. one still maturing towards
            // "perfect") - wait rather than overwrite it and lose that item.
            return false;
        }

        input.shrink(1);
        if (input.isEmpty()) {
            setItem(INPUT_SLOT, ItemStack.EMPTY);
        }
        return true;
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public int @NotNull [] getSlotsForFace(Direction side) {
        return side == Direction.DOWN ? new int[]{OUTPUT_SLOT} : new int[]{INPUT_SLOT, SMOKINGMATERIAL_SLOT};
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot == INPUT_SLOT || slot == SMOKINGMATERIAL_SLOT;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return slot == OUTPUT_SLOT;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        inventory.set(slot, stack);
        if (slot == INPUT_SLOT && stack.isEmpty()) {
            smokingTime = 0;
        }
        setChanged();
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
