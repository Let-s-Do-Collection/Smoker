package net.satisfy.smoker.core.compat;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public final class SmokerCompatFoods {
    private SmokerCompatFoods() {
    }

    public static final List<ItemStack> SAMPLE_FOODS = List.of(
            new ItemStack(Items.BREAD),
            new ItemStack(Items.COOKED_BEEF),
            new ItemStack(Items.APPLE),
            new ItemStack(Items.COOKED_PORKCHOP),
            new ItemStack(Items.POTATO)
    );
}
