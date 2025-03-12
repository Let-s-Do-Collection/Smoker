package net.satisfy.smoker.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.satisfy.smoker.Smoker;
import net.satisfy.smoker.core.world.level.block.ImprovedSmokerBlock;
import net.satisfy.smoker.core.util.SmokerIdentifier;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ObjectRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Smoker.MOD_ID, Registries.ITEM);
    public static final Registrar<Item> ITEM_REGISTRAR = ITEMS.getRegistrar();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Smoker.MOD_ID, Registries.BLOCK);
    public static final Registrar<Block> BLOCK_REGISTRAR = BLOCKS.getRegistrar();

    public static final RegistrySupplier<Block> IMPROVED_SMOKER = registerWithItem("improved_smoker", () -> new ImprovedSmokerBlock(BlockBehaviour.Properties.copy(Blocks.STONE)));

    public static void init() {
        ITEMS.register();
        BLOCKS.register();
    }

    private static <T extends Block> RegistrySupplier<T> registerWithItem(String name, Supplier<T> block) {
        RegistrySupplier<T> registeredBlock = registerWithoutItem(name, block);
        registerItem(name, () -> new BlockItem(registeredBlock.get(), new Item.Properties()));
        return registeredBlock;
    }

    private static <T extends Block> RegistrySupplier<T> registerWithoutItem(String name, Supplier<T> block) {
        return BLOCK_REGISTRAR.register(new SmokerIdentifier(name), block);
    }

    private static <T extends Item> void registerItem(String name, Supplier<T> itemSupplier) {
        ITEM_REGISTRAR.register(new SmokerIdentifier(name), itemSupplier);
    }
}
