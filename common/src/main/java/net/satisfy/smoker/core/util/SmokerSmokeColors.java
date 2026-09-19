package net.satisfy.smoker.core.util;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.satisfy.smoker.core.registry.CommonRegistry;

import java.util.Set;

/**
 * Maps the wood item currently burning as smoking material to a smoke color category, per the
 * cosmetic "wood-colored smoke" feature: only a handful of themed categories, not one color per
 * exact wood. Matches by item id string rather than a hard class dependency on BloomingNature (an
 * optional companion mod), the same way the smoker_modifier recipes reference it.
 */
public final class SmokerSmokeColors {
    private SmokerSmokeColors() {
    }

    public static final int NORMAL = 0;
    public static final int DARK = 1;
    public static final int WARM = 2;

    /** Near-black tint for the DARK category's particle provider (client-side registration). */
    public static final float DARK_R = 0.08F;
    public static final float DARK_G = 0.08F;
    public static final float DARK_B = 0.08F;

    /**
     * Muted amber tint for the WARM category's particle provider (client-side registration) - kept
     * closer to a smoky brownish-orange than a saturated/neon orange, so it still reads as smoke
     * rather than a solid-colored dot.
     */
    public static final float WARM_R = 0.75F;
    public static final float WARM_G = 0.5F;
    public static final float WARM_B = 0.32F;

    /** Nether wood and other near-black themed planks. */
    private static final Set<String> DARK_WOODS = Set.of(
            "minecraft:crimson_planks",
            "minecraft:warped_planks",
            "bloomingnature:ebony_planks"
    );

    /** Savanna/amber-glow themed planks. */
    private static final Set<String> WARM_WOODS = Set.of(
            "minecraft:acacia_planks",
            "minecraft:dark_oak_planks",
            "bloomingnature:baobab_planks"
    );

    /**
     * The smoke-kind category (NORMAL/DARK/WARM) for the given smoking-material item, e.g. as read
     * from the Smoking Station's SMOKINGMATERIAL_SLOT before it is consumed.
     */
    public static int getCategory(Item material) {
        if (material == null) return NORMAL;
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(material);
        if (id == null) return NORMAL;
        String key = id.toString();
        if (DARK_WOODS.contains(key)) return DARK;
        if (WARM_WOODS.contains(key)) return WARM;
        return NORMAL;
    }

    /**
     * The registered particle type to spawn for a given smoke-kind category - the small, quick
     * puff mirroring vanilla's ParticleTypes.SMOKE - or null for NORMAL (callers should keep
     * spawning the existing vanilla ParticleTypes.SMOKE in that case).
     */
    public static SimpleParticleType getSmokeParticle(int smokeKind) {
        return switch (smokeKind) {
            case DARK -> CommonRegistry.DARK_SMOKE.get();
            case WARM -> CommonRegistry.WARM_SMOKE.get();
            default -> null;
        };
    }

    /**
     * The registered particle type to spawn for a given smoke-kind category - the large, slow,
     * long-lived column mirroring vanilla's ParticleTypes.CAMPFIRE_SIGNAL_SMOKE - or null for
     * NORMAL (callers should keep spawning the existing vanilla CAMPFIRE_SIGNAL_SMOKE in that
     * case).
     */
    public static SimpleParticleType getLargeSmokeParticle(int smokeKind) {
        return switch (smokeKind) {
            case DARK -> CommonRegistry.DARK_SMOKE_LARGE.get();
            case WARM -> CommonRegistry.WARM_SMOKE_LARGE.get();
            default -> null;
        };
    }
}
