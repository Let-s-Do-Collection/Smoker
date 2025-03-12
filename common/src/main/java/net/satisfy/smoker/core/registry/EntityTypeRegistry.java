package net.satisfy.smoker.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.satisfy.smoker.Smoker;
import net.satisfy.smoker.core.world.level.block.entity.ImprovedSmokerBlockEntity;
import net.satisfy.smoker.core.util.SmokerIdentifier;

import java.util.function.Supplier;

public class EntityTypeRegistry {
    private static final Registrar<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Smoker.MOD_ID, Registries.BLOCK_ENTITY_TYPE).getRegistrar();

    public static final RegistrySupplier<BlockEntityType<ImprovedSmokerBlockEntity>> IMPROVED_SMOKER = registerBlockEntity("improved_smoker", () -> BlockEntityType.Builder.of(ImprovedSmokerBlockEntity::new, ObjectRegistry.IMPROVED_SMOKER.get()).build(null));

    private static <T extends BlockEntityType<?>> RegistrySupplier<T> registerBlockEntity(final String path, final Supplier<T> type) {
        return BLOCK_ENTITY_TYPES.register(new SmokerIdentifier(path), type);
    }


    public static void init() {
    }
}
