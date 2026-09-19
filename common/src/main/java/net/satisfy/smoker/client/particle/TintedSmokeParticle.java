package net.satisfy.smoker.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SmokeParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TintedSmokeParticle extends SmokeParticle {
    protected TintedSmokeParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd,
                                   float size, SpriteSet sprites, float r, float g, float b) {
        super(level, x, y, z, xd, yd, zd, size, sprites);
        this.setColor(r, g, b);
    }

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
