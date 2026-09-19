package net.satisfy.smoker.core.util;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.satisfy.smoker.core.registry.CommonRegistry;

import java.util.Set;

public final class SmokerSmokeColors {
    private SmokerSmokeColors() {
    }

    public static final int NORMAL = 0;
    public static final int DARK = 1;
    public static final int WARM = 2;

    public static final float DARK_R = 0.08F;
    public static final float DARK_G = 0.08F;
    public static final float DARK_B = 0.08F;

    public static final float WARM_R = 0.75F;
    public static final float WARM_G = 0.5F;
    public static final float WARM_B = 0.32F;

    private static final Set<String> DARK_WOODS = Set.of(
            "minecraft:crimson_planks",
            "minecraft:warped_planks",
            "bloomingnature:ebony_planks"
    );

    private static final Set<String> WARM_WOODS = Set.of(
            "minecraft:acacia_planks",
            "minecraft:dark_oak_planks",
            "bloomingnature:baobab_planks"
    );

    public static int getCategory(Item material) {
        if (material == null) return NORMAL;
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(material);
        if (id == null) return NORMAL;
        String key = id.toString();
        if (DARK_WOODS.contains(key)) return DARK;
        if (WARM_WOODS.contains(key)) return WARM;
        return NORMAL;
    }

    public static SimpleParticleType getSmokeParticle(int smokeKind) {
        return switch (smokeKind) {
            case DARK -> CommonRegistry.DARK_SMOKE.get();
            case WARM -> CommonRegistry.WARM_SMOKE.get();
            default -> null;
        };
    }

    public static SimpleParticleType getLargeSmokeParticle(int smokeKind) {
        return switch (smokeKind) {
            case DARK -> CommonRegistry.DARK_SMOKE_LARGE.get();
            case WARM -> CommonRegistry.WARM_SMOKE_LARGE.get();
            default -> null;
        };
    }
}
