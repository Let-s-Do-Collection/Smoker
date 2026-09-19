package net.satisfy.smoker.core.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.satisfy.smoker.core.util.SmokerFoodData;
import net.satisfy.smoker.core.util.SmokerIdentifier;
import net.satisfy.smoker.platform.PlatformHelper;

public class TagsRegistry {
    public static final TagKey<Item> NOT_SMOKABLE = TagKey.create(Registries.ITEM, SmokerIdentifier.id("not_smokable"));

    public static boolean isSmokable(ItemStack foodStack) {
        return PlatformHelper.isEverythingSmokable() || (!foodStack.is(NOT_SMOKABLE) && !SmokerFoodData.isProcessed(foodStack));
    }
}
