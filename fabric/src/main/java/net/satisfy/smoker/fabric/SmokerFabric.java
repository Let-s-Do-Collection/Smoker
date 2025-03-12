package net.satisfy.smoker.fabric;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.item.CreativeModeTabs;
import net.satisfy.smoker.Smoker;
import net.fabricmc.api.ModInitializer;
import net.satisfy.smoker.core.registry.ObjectRegistry;

public class SmokerFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Smoker.init();
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(SmokerFabric::addItemsToCreativeTab);
    }

    private static void addItemsToCreativeTab(FabricItemGroupEntries entries) {
        entries.accept(ObjectRegistry.IMPROVED_SMOKER.get().asItem());
    }
}
