package net.satisfy.smoker.core.registry;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.satisfy.smoker.Smoker;
import net.satisfy.smoker.core.recipe.SmokerModifierRecipe;
import net.satisfy.smoker.core.util.SmokerIdentifier;
import net.satisfy.smoker.core.world.inventory.ImprovedSmokerMenu;
import net.satisfy.smoker.core.world.level.block.ImprovedSmokerBlock;
import net.satisfy.smoker.core.world.level.block.entity.ImprovedSmokerBlockEntity;

import java.util.function.Supplier;

public class CommonRegistry {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Smoker.MOD_ID, Registries.ITEM);
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Smoker.MOD_ID, Registries.BLOCK);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Smoker.MOD_ID, Registries.BLOCK_ENTITY_TYPE);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Smoker.MOD_ID, Registries.RECIPE_SERIALIZER);
    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Smoker.MOD_ID, Registries.RECIPE_TYPE);
    private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Smoker.MOD_ID, Registries.MENU);
    private static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Smoker.MOD_ID, Registries.PARTICLE_TYPE);

    public static final RegistrySupplier<Block> IMPROVED_SMOKER = registerWithItem("smoking_station", () -> new ImprovedSmokerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));

    public static final RegistrySupplier<BlockEntityType<ImprovedSmokerBlockEntity>> IMPROVED_SMOKER_ENTITY = registerBlockEntity(() -> BlockEntityType.Builder.of(ImprovedSmokerBlockEntity::new, IMPROVED_SMOKER.get()).build(null));

    public static final RegistrySupplier<RecipeSerializer<SmokerModifierRecipe>> SMOKER_RECIPE_SERIALIZER =
        createRecipeSerializer(SmokerModifierRecipe.Serializer::new);

    public static final RegistrySupplier<RecipeType<SmokerModifierRecipe>> SMOKER_RECIPE_TYPE =
        createRecipeType();

    public static final RegistrySupplier<MenuType<ImprovedSmokerMenu>> SMOKING_GUI_HANDLER =
        createMenu(() -> new MenuType<>(ImprovedSmokerMenu::new, FeatureFlags.VANILLA_SET));

    /**
     * Cosmetic colored-smoke particle types spawned by the Smoking Station while lit, chosen based
     * on the category of wood currently fueling it (see SmokerSmokeColors). Both are data-less,
     * like vanilla's own ParticleTypes.SMOKE - color is baked into the client-side provider
     * registered for each type, not carried per-instance.
     */
    public static final RegistrySupplier<SimpleParticleType> DARK_SMOKE =
        PARTICLE_TYPES.register("dark_smoke", CommonRegistry::createSimpleParticleType);

    public static final RegistrySupplier<SimpleParticleType> WARM_SMOKE =
        PARTICLE_TYPES.register("warm_smoke", CommonRegistry::createSimpleParticleType);

    /**
     * Large, long-lived tinted counterparts to vanilla's CAMPFIRE_SIGNAL_SMOKE (see
     * TintedCampfireSmokeParticle) - spawned alongside DARK_SMOKE/WARM_SMOKE to give colored smoke
     * the same visual weight as vanilla's own two-particle mix, instead of just a small tinted dot.
     */
    public static final RegistrySupplier<SimpleParticleType> DARK_SMOKE_LARGE =
        PARTICLE_TYPES.register("dark_smoke_large", CommonRegistry::createSimpleParticleType);

    public static final RegistrySupplier<SimpleParticleType> WARM_SMOKE_LARGE =
        PARTICLE_TYPES.register("warm_smoke_large", CommonRegistry::createSimpleParticleType);

    public static void init() {
        BLOCKS.register();
        ITEMS.register();
        BLOCK_ENTITY_TYPES.register();
        RECIPE_SERIALIZERS.register();
        RECIPE_TYPES.register();
        MENU_TYPES.register();
        PARTICLE_TYPES.register();

        CreativeTabRegistry.append(CreativeModeTabs.FUNCTIONAL_BLOCKS, IMPROVED_SMOKER);
    }

    private static <T extends Block> RegistrySupplier<T> registerWithItem(String name, Supplier<T> block) {
        RegistrySupplier<T> registeredBlock = registerBlock(name, block);
        registerItem(name, () -> new BlockItem(registeredBlock.get(), new Item.Properties()));
        return registeredBlock;
    }

    private static <T extends Block> RegistrySupplier<T> registerBlock(String name, Supplier<T> block) {
        return BLOCKS.register(SmokerIdentifier.id(name), block);
    }

    private static <T extends Item> void registerItem(String name, Supplier<T> itemSupplier) {
        ITEMS.register(SmokerIdentifier.id(name), itemSupplier);
    }

    private static <T extends BlockEntityType<?>> RegistrySupplier<T> registerBlockEntity(final Supplier<T> type) {
        return BLOCK_ENTITY_TYPES.register(SmokerIdentifier.id("smoking_station_entity"), type);
    }

    private static <T extends Recipe<?>> RegistrySupplier<RecipeSerializer<T>> createRecipeSerializer(Supplier<RecipeSerializer<T>> serializer) {
        return RECIPE_SERIALIZERS.register("smoker_modifier", serializer);
    }

    private static <T extends Recipe<?>> RegistrySupplier<RecipeType<T>> createRecipeType() {
        return RECIPE_TYPES.register("smoker_modifier", () -> new RecipeType<>() {
            @Override
            public String toString() {
                return "smoker_modifier";
            }
        });
    }

    private static <T extends MenuType<?>> RegistrySupplier<T> createMenu(Supplier<T> type) {
        return MENU_TYPES.register("smoking_gui_handler", type);
    }

    /**
     * SimpleParticleType's own constructor is protected (vanilla builds ParticleTypes.SMOKE etc.
     * from within the same package) - an anonymous subclass is the simplest cross-platform way to
     * call it from here without pulling in a loader-specific helper like Fabric's
     * FabricParticleTypes.simple().
     */
    private static SimpleParticleType createSimpleParticleType() {
        return new SimpleParticleType(false) {
        };
    }
}
