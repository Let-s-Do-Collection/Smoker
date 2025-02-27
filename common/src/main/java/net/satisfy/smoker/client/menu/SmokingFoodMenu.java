package net.satisfy.smoker.client.menu;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.satisfy.smoker.core.registry.ScreenHandlerTypeRegistry;
import org.jetbrains.annotations.NotNull;

public class SmokingFoodMenu extends AbstractContainerMenu {
    private final Container inventory;
    private final ContainerData propertyDelegate;

    public SmokingFoodMenu(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleContainer(3), new SimpleContainerData(4));
    }

    public SmokingFoodMenu(int syncId, Inventory playerInventory, Container inventory, ContainerData propertyDelegate) {
        super(ScreenHandlerTypeRegistry.SMOKING_FOOD_MENU.get(), syncId);

        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        this.addDataSlots(propertyDelegate);

        buildBlockEntityContainer(playerInventory);
        buildPlayerContainer(playerInventory);
    }

    private static boolean isFuel(ItemStack stack) {
        return AbstractFurnaceBlockEntity.isFuel(stack);
    }

    private void buildBlockEntityContainer(Inventory playerInventory) {
        this.addSlot(new Slot(inventory, 0, 56, 35));
        this.addSlot(new Slot(inventory, 1, 116, 35));
        this.addSlot(new Slot(inventory, 2, 80, 17));
    }

    private void buildPlayerContainer(Inventory playerInventory) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    public int getScaledProgress(int arrowWidth) {
        final int progress = this.propertyDelegate.get(2);
        final int totalProgress = this.propertyDelegate.get(3);
        if (progress > 0 && totalProgress > 0) {
            return progress * arrowWidth / totalProgress;
        }
        return 0;
    }

    public boolean isBeingBurned() {
        return propertyDelegate.get(1) != 0;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            ItemStack copy = stack.copy();

            if (index == 0) {
                if (!this.moveItemStackTo(stack, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, copy);
            } else if (index == 1 || index == 2) {
                if (!this.moveItemStackTo(stack, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (isFuel(stack)) {
                    if (!this.moveItemStackTo(stack, 2, 3, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(stack, 0, 2, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == copy.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
            return copy;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }
}
