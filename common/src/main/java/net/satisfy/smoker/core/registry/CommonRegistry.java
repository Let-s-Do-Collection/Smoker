package net.satisfy.smoker.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
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

    public static final RegistrySupplier<Block> IMPROVED_SMOKER = registerWithItem("improved_smoker", () -> new ImprovedSmokerBlock(BlockBehaviour.Properties.copy(Blocks.STONE)));

    public static final RegistrySupplier<BlockEntityType<ImprovedSmokerBlockEntity>> IMPROVED_SMOKER_ENTITY = registerBlockEntity(() -> BlockEntityType.Builder.of(ImprovedSmokerBlockEntity::new, IMPROVED_SMOKER.get()).build(null));

    public static final RegistrySupplier<RecipeSerializer<SmokerModifierRecipe>> SMOKER_RECIPE_SERIALIZER =
        createRecipeSerializer(SmokerModifierRecipe.Serializer::new);

    public static final RegistrySupplier<RecipeType<SmokerModifierRecipe>> SMOKER_RECIPE_TYPE =
        createRecipeType();

    public static final RegistrySupplier<MenuType<ImprovedSmokerMenu>> SMOKING_GUI_HANDLER =
        createMenu(() -> new MenuType<>(ImprovedSmokerMenu::new, FeatureFlags.VANILLA_SET));

    public static void init() {
        BLOCKS.register();
        ITEMS.register();
        BLOCK_ENTITY_TYPES.register();
        RECIPE_SERIALIZERS.register();
        RECIPE_TYPES.register();
        MENU_TYPES.register();
    }

    private static <T extends Block> RegistrySupplier<T> registerWithItem(String name, Supplier<T> block) {
        RegistrySupplier<T> registeredBlock = registerBlock(name, block);
        registerItem(name, () -> new BlockItem(registeredBlock.get(), new Item.Properties()));
        return registeredBlock;
    }

    private static <T extends Block> RegistrySupplier<T> registerBlock(String name, Supplier<T> block) {
        return BLOCKS.register(new SmokerIdentifier(name), block);
    }

    private static <T extends Item> void registerItem(String name, Supplier<T> itemSupplier) {
        ITEMS.register(new SmokerIdentifier(name), itemSupplier);
    }

    private static <T extends BlockEntityType<?>> RegistrySupplier<T> registerBlockEntity(final Supplier<T> type) {
        return BLOCK_ENTITY_TYPES.register(new SmokerIdentifier("improved_smoker_entity"), type);
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
}
