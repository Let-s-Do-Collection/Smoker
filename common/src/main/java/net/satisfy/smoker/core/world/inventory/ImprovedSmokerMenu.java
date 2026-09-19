package net.satisfy.smoker.core.world.inventory;

import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.satisfy.smoker.core.registry.CommonRegistry;
import net.satisfy.smoker.core.world.level.block.entity.ImprovedSmokerBlockEntity;
import org.jetbrains.annotations.NotNull;

public class ImprovedSmokerMenu extends AbstractContainerMenu {
    private final ContainerData propertyDelegate;

    public ImprovedSmokerMenu(int syncId, Inventory playerInventory, ImprovedSmokerBlockEntity blockEntity, ContainerData propertyDelegate) {
        super(CommonRegistry.SMOKING_GUI_HANDLER.get(), syncId);
        this.propertyDelegate = propertyDelegate;
        buildBlockEntityContainer(playerInventory, blockEntity);
        buildPlayerContainer(playerInventory);
        addDataSlots(propertyDelegate);
    }

    public ImprovedSmokerMenu(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleContainer(3), new SimpleContainerData(4));
    }

    public ImprovedSmokerMenu(int syncId, Inventory playerInventory, Container inventory, ContainerData propertyDelegate) {
        super(CommonRegistry.SMOKING_GUI_HANDLER.get(), syncId);
        this.propertyDelegate = propertyDelegate;
        buildBlockEntityContainer(playerInventory, inventory);
        buildPlayerContainer(playerInventory);
        addDataSlots(propertyDelegate);
    }

    private void buildBlockEntityContainer(Inventory playerInventory, Container inventory) {
        this.addSlot(new FurnaceResultSlot(playerInventory.player, inventory, 0, 127, 20));
        this.addSlot(new Slot(inventory, 1, 57, 2));
        this.addSlot(new Slot(inventory, 2, 57, 37));
    }

    private void buildPlayerContainer(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    public int getSmokeXProgress() {
        int progress = this.propertyDelegate.get(0);
        int totalProgress = this.propertyDelegate.get(1);
        if (totalProgress == 0 || progress == 0) {
            return 0;
        }
        return progress * 10 / totalProgress;
    }

    public int getRemainingSmokeTime() {
        return this.propertyDelegate.get(1) - this.propertyDelegate.get(0);
    }

    public boolean isLit() {
        return this.propertyDelegate.get(3) == 1;
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack originalStack = stack.copy();
        final int outputSlot = 0;
        final int edibleSlot = 1;
        final int plankSlot = 2;
        final int playerInvStart = 3;
        final int hotbarStart = playerInvStart + 27;
        final int hotbarEnd = hotbarStart + 9;

        if (index == outputSlot) {
            if (!moveItemStackTo(stack, playerInvStart, hotbarEnd, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(stack, originalStack);
        } else if (index == edibleSlot || index == plankSlot) {
            if (!moveItemStackTo(stack, playerInvStart, hotbarEnd, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index >= playerInvStart) {
            boolean moved = false;
            if (stack.has(DataComponents.FOOD)) {
                moved = moveItemStackTo(stack, edibleSlot, edibleSlot + 1, false);
            } else if (stack.getItem().builtInRegistryHolder().is(ItemTags.PLANKS)) {
                moved = moveItemStackTo(stack, plankSlot, plankSlot + 1, false);
            }
            if (!moved) {
                return ItemStack.EMPTY;
            }
        }
        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (stack.getCount() == originalStack.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, stack);
        return originalStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
