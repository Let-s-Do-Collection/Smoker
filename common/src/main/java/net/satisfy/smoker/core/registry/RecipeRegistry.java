package net.satisfy.smoker.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.satisfy.smoker.Smoker;
import net.satisfy.smoker.core.recipe.SmokingFoodRecipe;

public class RecipeRegistry {

    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Smoker.MOD_ID, Registries.RECIPE_SERIALIZER);

    public static final RegistrySupplier<RecipeSerializer<?>> SMOKING = SERIALIZERS.register("smoking", SmokingFoodRecipe.Serializer::new);


    public static void init() {
        SERIALIZERS.register();
    }
}
