package net.satisfy.smoker.core.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class SmokingFoodRecipe extends AbstractCookingRecipe {
    private final ResourceLocation woodType;
    private final List<BonusEffect> effects;
    private final float nutritionMultiplier;
    private final float saturationMultiplier;

    public SmokingFoodRecipe(ResourceLocation id, String group, CookingBookCategory category, Ingredient ingredient, float experience, int cookingTime, ResourceLocation woodType, List<BonusEffect> effects, float nutritionMultiplier, float saturationMultiplier) {
        super(RecipeType.SMOKING, id, group, category, ingredient, ItemStack.EMPTY, experience, cookingTime);
        this.woodType = woodType;
        this.effects = effects;
        this.nutritionMultiplier = nutritionMultiplier;
        this.saturationMultiplier = saturationMultiplier;
    }

    @Override
    public @NotNull ItemStack getToastSymbol() {
        return new ItemStack(Blocks.SMOKER);
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull ItemStack assemble(Container container, RegistryAccess registryAccess) {
        ItemStack input = container.getItem(0);
        if (!input.getItem().isEdible()) return ItemStack.EMPTY;
        ItemStack fuel = container.getItem(1);
        if (!fuel.getItem().builtInRegistryHolder().key().location().equals(this.woodType)) return ItemStack.EMPTY;
        ItemStack result = input.copy();
        CompoundTag tag = result.getOrCreateTag();
        CompoundTag bonus = new CompoundTag();
        bonus.putString("wood_type", this.woodType.toString());
        Vector2f multipliers = new Vector2f(this.nutritionMultiplier, this.saturationMultiplier);
        bonus.putFloat("nutrition", multipliers.x());
        bonus.putFloat("saturation", multipliers.y());
        ListTag effectsList = new ListTag();
        for (BonusEffect effect : this.effects) {
            CompoundTag effectTag = new CompoundTag();
            effectTag.putString("effect", effect.effect.toString());
            effectTag.putInt("duration", effect.duration);
            effectTag.putInt("amplifier", effect.amplifier);
            effectsList.add(effectTag);
        }
        bonus.put("effects", effectsList);
        tag.put("SmokingBonus", bonus);
        result.setTag(tag);
        return result;
    }

    public static class BonusEffect {
        public final ResourceLocation effect;
        public final int duration;
        public final int amplifier;

        public BonusEffect(ResourceLocation effect, int duration, int amplifier) {
            this.effect = effect;
            this.duration = duration;
            this.amplifier = amplifier;
        }
    }

    public static class Serializer implements RecipeSerializer<SmokingFoodRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public @NotNull SmokingFoodRecipe fromJson(ResourceLocation id, JsonObject json) {
            String group = json.has("group") ? json.get("group").getAsString() : "";
            CookingBookCategory category = CookingBookCategory.MISC;
            Ingredient ingredient = Ingredient.fromJson(json.get("ingredient").getAsJsonObject());
            float experience = json.has("experience") ? json.get("experience").getAsFloat() : 0.0F;
            int cookingTime = json.has("cookingTime") ? json.get("cookingTime").getAsInt() : 100;
            JsonObject bonusJson = json.get("bonus").getAsJsonObject();
            ResourceLocation woodType = new ResourceLocation(bonusJson.get("wood_type").getAsString());
            float nutrition = bonusJson.get("nutrition").getAsFloat();
            float saturation = bonusJson.get("saturation").getAsFloat();
            JsonArray effectsArray = bonusJson.get("effects").getAsJsonArray();
            List<BonusEffect> effects = new ArrayList<>();
            for (int i = 0; i < effectsArray.size(); i++) {
                JsonObject effectObj = effectsArray.get(i).getAsJsonObject();
                ResourceLocation effectId = new ResourceLocation(effectObj.get("effect").getAsString());
                int duration = effectObj.get("duration").getAsInt();
                int amplifier = effectObj.get("amplifier").getAsInt();
                effects.add(new BonusEffect(effectId, duration, amplifier));
            }
            return new SmokingFoodRecipe(id, group, category, ingredient, experience, cookingTime, woodType, effects, nutrition, saturation);
        }

        @Override
        public @NotNull SmokingFoodRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            String group = buf.readUtf();
            CookingBookCategory category = buf.readEnum(CookingBookCategory.class);
            Ingredient ingredient = Ingredient.fromNetwork(buf);
            float experience = buf.readFloat();
            int cookingTime = buf.readVarInt();
            ResourceLocation woodType = buf.readResourceLocation();
            float nutrition = buf.readFloat();
            float saturation = buf.readFloat();
            int effectsSize = buf.readVarInt();
            List<BonusEffect> effects = new ArrayList<>();
            for (int i = 0; i < effectsSize; i++) {
                ResourceLocation effectId = buf.readResourceLocation();
                int duration = buf.readVarInt();
                int amplifier = buf.readVarInt();
                effects.add(new BonusEffect(effectId, duration, amplifier));
            }
            return new SmokingFoodRecipe(id, group, category, ingredient, experience, cookingTime, woodType, effects, nutrition, saturation);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, SmokingFoodRecipe recipe) {
            buf.writeUtf(recipe.getGroup());
            buf.writeEnum(recipe.category());
            recipe.ingredient.toNetwork(buf);
            buf.writeFloat(recipe.experience);
            buf.writeVarInt(recipe.cookingTime);
            buf.writeResourceLocation(recipe.woodType);
            buf.writeFloat(recipe.nutritionMultiplier);
            buf.writeFloat(recipe.saturationMultiplier);
            buf.writeVarInt(recipe.effects.size());
            for (BonusEffect effect : recipe.effects) {
                buf.writeResourceLocation(effect.effect);
                buf.writeVarInt(effect.duration);
                buf.writeVarInt(effect.amplifier);
            }
        }
    }
}
