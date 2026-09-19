package net.satisfy.smoker.core.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.satisfy.smoker.core.util.SmokerIdentifier;
import net.satisfy.smoker.platform.PlatformHelper;

public class TagsRegistry {
    /**
     * Edible items that shouldn't be smokable even though they carry the FOOD component - already
     * finished desserts/pastries/drinks where "smoking" makes no sense. Datapacks can add to or
     * remove from this freely.
     */
    public static final TagKey<Item> NOT_SMOKABLE = TagKey.create(Registries.ITEM, SmokerIdentifier.id("not_smokable"));

    /**
     * Whether a food item is allowed to be smoked: always true if the "everythingSmokable" config
     * override is on, otherwise true unless the item is in NOT_SMOKABLE.
     */
    public static boolean isSmokable(ItemStack foodStack) {
        return PlatformHelper.isEverythingSmokable() || !foodStack.is(NOT_SMOKABLE);
    }
}
