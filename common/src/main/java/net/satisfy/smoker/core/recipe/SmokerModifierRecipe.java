package net.satisfy.smoker.core.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.satisfy.smoker.core.recipe.input.SmokerRecipeInput;
import net.satisfy.smoker.core.registry.CommonRegistry;
import net.satisfy.smoker.core.registry.TagsRegistry;
import net.satisfy.smoker.core.util.SmokerFoodData;
import org.jetbrains.annotations.NotNull;

public class SmokerModifierRecipe implements Recipe<SmokerRecipeInput> {
    private final Item smokingMaterial;
    private final double saturation;
    private final double nutrition;
    private final int craftingTime;
    private final int healAmount;
    private final String effectName;
    private final int effectDuration;

    public SmokerModifierRecipe(Item smokingMaterial, double saturation, double nutrition, int craftingTime, int healAmount, String effectName, int effectDuration) {
        this.smokingMaterial = smokingMaterial;
        this.saturation = saturation;
        this.nutrition = nutrition;
        this.craftingTime = craftingTime;
        this.healAmount = healAmount;
        this.effectName = effectName;
        this.effectDuration = effectDuration;
    }

    @Override
    public boolean matches(SmokerRecipeInput input, Level level) {
        ItemStack smokingMaterialStack = input.smokingMaterial();
        ItemStack foodStack = input.food();
        return !smokingMaterialStack.isEmpty() && smokingMaterialStack.is(smokingMaterial)
                && !foodStack.isEmpty() && TagsRegistry.isSmokable(foodStack);
    }

    @Override
    public @NotNull ItemStack assemble(SmokerRecipeInput input, HolderLookup.Provider provider) {
        ItemStack food = input.food();
        if (food.isEmpty() || !food.has(DataComponents.FOOD)) return ItemStack.EMPTY;
        ItemStack result = food.copy();
        result.setCount(1);
        CompoundTag tag = SmokerFoodData.getOrCreateTag(result);
        tag.putDouble(SmokerFoodData.SATURATION_KEY, getSaturation());
        tag.putDouble(SmokerFoodData.NUTRITION_KEY, getNutrition());
        tag.putInt(SmokerFoodData.HEAL_KEY, getHealAmount());

        if (hasEffect()) {
            tag.putString(SmokerFoodData.EFFECT_KEY, getEffectName());
            tag.putInt(SmokerFoodData.EFFECT_DURATION_KEY, getEffectDuration());
        }
        SmokerFoodData.applyTag(result, tag);

        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return CommonRegistry.SMOKER_RECIPE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return CommonRegistry.SMOKER_RECIPE_TYPE.get();
    }

    public Item getSmokingMaterial() {
        return smokingMaterial;
    }

    public double getSaturation() {
        return saturation;
    }

    public double getNutrition() {
        return nutrition;
    }

    public int getCraftingTime() {
        return craftingTime;
    }

    public int getHealAmount() {
        return healAmount;
    }

    public boolean hasEffect() {
        return !effectName.isEmpty();
    }

    public String getEffectName() {
        return effectName;
    }

    public int getEffectDuration() {
        return effectDuration;
    }

    private record Modifiers(double saturation, double nutrition, int healAmount, String effectName, int effectDuration) {
        static final MapCodec<Modifiers> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.DOUBLE.fieldOf("saturation").forGetter(Modifiers::saturation),
                Codec.DOUBLE.fieldOf("nutrition").forGetter(Modifiers::nutrition),
                Codec.INT.optionalFieldOf("heal_amount", 0).forGetter(Modifiers::healAmount),
                Codec.STRING.optionalFieldOf("effect", "").forGetter(Modifiers::effectName),
                Codec.INT.optionalFieldOf("effect_duration", 0).forGetter(Modifiers::effectDuration)
        ).apply(instance, Modifiers::new));
    }

    public static class Serializer implements RecipeSerializer<SmokerModifierRecipe> {
        private static final Codec<Item> ITEM_CODEC = ResourceLocation.CODEC.xmap(BuiltInRegistries.ITEM::get, BuiltInRegistries.ITEM::getKey);

        public static final MapCodec<SmokerModifierRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ITEM_CODEC.fieldOf("material").forGetter(SmokerModifierRecipe::getSmokingMaterial),
                Modifiers.CODEC.fieldOf("modifiers").forGetter(recipe -> new Modifiers(recipe.getSaturation(), recipe.getNutrition(), recipe.getHealAmount(), recipe.getEffectName(), recipe.getEffectDuration())),
                Codec.INT.fieldOf("crafting_time").forGetter(SmokerModifierRecipe::getCraftingTime)
        ).apply(instance, (material, modifiers, craftingTime) -> new SmokerModifierRecipe(
                material, modifiers.saturation(), modifiers.nutrition(), craftingTime, modifiers.healAmount(), modifiers.effectName(), modifiers.effectDuration()
        )));

        public static final StreamCodec<RegistryFriendlyByteBuf, SmokerModifierRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::toNetwork, Serializer::fromNetwork
        );

        public static @NotNull SmokerModifierRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
            Item material = BuiltInRegistries.ITEM.get(buf.readResourceLocation());
            double saturation = buf.readDouble();
            double nutrition = buf.readDouble();
            int craftingTime = buf.readVarInt();
            int healAmount = buf.readVarInt();
            String effectName = buf.readUtf();
            int effectDuration = buf.readVarInt();

            return new SmokerModifierRecipe(material, saturation, nutrition, craftingTime, healAmount, effectName, effectDuration);
        }

        public static void toNetwork(RegistryFriendlyByteBuf buf, SmokerModifierRecipe recipe) {
            buf.writeResourceLocation(BuiltInRegistries.ITEM.getKey(recipe.smokingMaterial));
            buf.writeDouble(recipe.saturation);
            buf.writeDouble(recipe.nutrition);
            buf.writeVarInt(recipe.craftingTime);
            buf.writeVarInt(recipe.healAmount);
            buf.writeUtf(recipe.effectName);
            buf.writeVarInt(recipe.effectDuration);
        }

        @Override
        public @NotNull MapCodec<SmokerModifierRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, SmokerModifierRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
