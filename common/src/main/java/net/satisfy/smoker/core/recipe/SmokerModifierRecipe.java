package net.satisfy.smoker.core.recipe;

import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.satisfy.smoker.core.registry.CommonRegistry;
import org.jetbrains.annotations.NotNull;

public class SmokerModifierRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final Ingredient fuel;
    private final double saturation;
    private final double nutrition;
    private final int craftingTime;
    private final int healAmount;
    private final String effectName;
    private final int effectDuration;

    public SmokerModifierRecipe(ResourceLocation id, Ingredient fuel, double saturation, double nutrition, int craftingTime, int healAmount, String effectName, int effectDuration) {
        this.id = id;
        this.fuel = fuel;
        this.saturation = saturation;
        this.nutrition = nutrition;
        this.craftingTime = craftingTime;
        this.healAmount = healAmount;
        this.effectName = effectName;
        this.effectDuration = effectDuration;
    }

    @Override
    public boolean matches(Container container, Level level) {
        ItemStack fuelStack = container.getItem(0);
        return !fuelStack.isEmpty() && fuel.test(fuelStack);
    }

    @Override
    public @NotNull ItemStack assemble(Container container, RegistryAccess registryAccess) {
        ItemStack input = container.getItem(1);
        if (input.isEmpty() || !input.isEdible()) return ItemStack.EMPTY;
        ItemStack result = input.copy();
        result.setCount(1);
        CompoundTag tag = result.getOrCreateTag();
        tag.putDouble("smoker_saturation", getSaturation());
        tag.putDouble("smoker_nutrition", getNutrition());
        tag.putInt("smoker_heal_amount", getHealAmount());

        if (hasEffect()) {
            tag.putString("smoker_effect", getEffectName());
            tag.putInt("smoker_effect_duration", getEffectDuration());
        }

        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return id;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return CommonRegistry.SMOKER_RECIPE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return CommonRegistry.SMOKER_RECIPE_TYPE.get();
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

    public static class Serializer implements RecipeSerializer<SmokerModifierRecipe> {
        @Override
        public @NotNull SmokerModifierRecipe fromJson(ResourceLocation id, JsonObject json) {
            String material = GsonHelper.getAsString(json, "material");
            Ingredient fuel = Ingredient.of(new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation(material))));
            JsonObject mod = GsonHelper.getAsJsonObject(json, "modifiers");
            double saturation = GsonHelper.getAsDouble(mod, "saturation");
            double nutrition = GsonHelper.getAsDouble(mod, "nutrition");
            int craftingTime = GsonHelper.getAsInt(json, "crafting_time");
            int healAmount = GsonHelper.getAsInt(mod, "heal_amount", 0);
            String effectName = mod.has("effect") ? GsonHelper.getAsString(mod, "effect") : "";
            int effectDuration = mod.has("effect_duration") ? GsonHelper.getAsInt(mod, "effect_duration") : 0;

            return new SmokerModifierRecipe(id, fuel, saturation, nutrition, craftingTime, healAmount, effectName, effectDuration);
        }

        @Override
        public @NotNull SmokerModifierRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            Ingredient fuel = Ingredient.fromNetwork(buf);
            double saturation = buf.readDouble();
            double nutrition = buf.readDouble();
            int craftingTime = buf.readVarInt();
            int healAmount = buf.readVarInt();
            String effectName = buf.readUtf();
            int effectDuration = buf.readVarInt();

            return new SmokerModifierRecipe(id, fuel, saturation, nutrition, craftingTime, healAmount, effectName, effectDuration);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, SmokerModifierRecipe recipe) {
            recipe.fuel.toNetwork(buf);
            buf.writeDouble(recipe.saturation);
            buf.writeDouble(recipe.nutrition);
            buf.writeVarInt(recipe.craftingTime);
            buf.writeVarInt(recipe.healAmount);
            buf.writeUtf(recipe.effectName);
            buf.writeVarInt(recipe.effectDuration);
        }
    }
}
