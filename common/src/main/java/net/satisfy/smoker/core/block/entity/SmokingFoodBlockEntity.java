package net.satisfy.smoker.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.smoker.client.menu.SmokingFoodMenu;
import net.satisfy.smoker.core.registry.EntityTypeRegistry;
import net.satisfy.smoker.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

public class SmokingFoodBlockEntity extends BlockEntity implements MenuProvider {
    private final NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);

    public SmokingFoodBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.SMOKER.get(), pos, state);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return ObjectRegistry.SMOKER.get().getName();
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new SmokingFoodMenu(id, playerInventory, new SimpleContainer(3), new SimpleContainerData(4));
    }

    public NonNullList<ItemStack> getItems() {
        return items;
    }
}
