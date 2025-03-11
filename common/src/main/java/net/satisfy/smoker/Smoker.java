package net.satisfy.smoker;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.satisfy.smoker.core.registry.EntityTypeRegistry;
import net.satisfy.smoker.core.registry.ObjectRegistry;
import net.satisfy.smoker.core.registry.RecipeTypeRegistry;
import net.satisfy.smoker.core.registry.ScreenHandlerTypeRegistry;

public class Smoker {
    public static final String MOD_ID = "smoker";
    private static final String SMOKER_PROCESSED_KEY = "SmokerProcessed";

    public static void init() {
        EntityTypeRegistry.init();
        ObjectRegistry.init();
        ScreenHandlerTypeRegistry.init();
        RecipeTypeRegistry.init();
    }

    public static ItemStack setSmokerProcessed(ItemStack itemStack) {
        CompoundTag tag = itemStack.getOrCreateTag();
        tag.putBoolean(SMOKER_PROCESSED_KEY, true);
        return itemStack;
    }
}
