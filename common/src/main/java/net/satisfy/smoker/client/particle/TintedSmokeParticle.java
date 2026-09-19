package net.satisfy.smoker.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SmokeParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A real, textured smoke particle (reuses vanilla's own animated smoke sprites via the SpriteSet
 * handed to it at registration) that is additionally tinted a fixed color - used for the Smoking
 * Station's DARK/WARM wood-category smoke. SmokeParticle's constructor is protected, but that's
 * still callable via super(...) from this subclass even though we're in a different package.
 */
@Environment(EnvType.CLIENT)
public class TintedSmokeParticle extends SmokeParticle {
    protected TintedSmokeParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd,
                                   float size, SpriteSet sprites, float r, float g, float b) {
        super(level, x, y, z, xd, yd, zd, size, sprites);
        this.setColor(r, g, b);
    }

    /**
     * Builds the ParticleProvider to register for a colored smoke SimpleParticleType, given the
     * fixed RGB tint for that category. The actual SpriteSet is only known once the platform's
     * particle-registration callback fires (after the texture atlas is stitched), so this is
     * applied to that SpriteSet at registration time on each platform - see
     * net.satisfy.smoker.client.SmokerClientParticles.
     */
    public static ParticleProvider<SimpleParticleType> provider(SpriteSet sprites, float r, float g, float b) {
        return new ParticleProvider<>() {
            @Override
            public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                             double x, double y, double z, double xd, double yd, double zd) {
                return new TintedSmokeParticle(level, x, y, z, xd, yd, zd, 1.0F, sprites, r, g, b);
            }
        };
    }
}
