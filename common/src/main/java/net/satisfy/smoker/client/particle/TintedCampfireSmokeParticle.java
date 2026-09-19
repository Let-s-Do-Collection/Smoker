package net.satisfy.smoker.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

@Environment(EnvType.CLIENT)
public class TintedCampfireSmokeParticle extends TextureSheetParticle {
    protected TintedCampfireSmokeParticle(ClientLevel level, double x, double y, double z,
                                           double xd, double yd, double zd, SpriteSet sprites,
                                           float r, float g, float b) {
        super(level, x, y, z, xd, yd, zd);
        this.scale(3.0F);
        this.setSize(0.25F, 0.25F);
        this.lifetime = this.random.nextInt(50) + 280;
        this.gravity = 3.0E-6F;
        this.xd = xd;
        this.yd = yd + this.random.nextFloat() / 500.0F;
        this.zd = zd;
        this.setColor(r, g, b);
        this.setAlpha(0.95F);
        this.pickSprite(sprites);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        int oldAge = this.age;
        this.age = oldAge + 1;
        if (oldAge >= this.lifetime || this.alpha <= 0.0F) {
            this.remove();
            return;
        }

        this.xd += (this.random.nextFloat() / 5000.0F) * (this.random.nextBoolean() ? 1 : -1);
        this.zd += (this.random.nextFloat() / 5000.0F) * (this.random.nextBoolean() ? 1 : -1);
        this.yd -= this.gravity;
        this.move(this.xd, this.yd, this.zd);
        if (this.age >= this.lifetime - 60 && this.alpha > 0.01F) {
            this.alpha -= 0.015F;
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static ParticleProvider<SimpleParticleType> provider(SpriteSet sprites, float r, float g, float b) {
        return new ParticleProvider<>() {
            @Override
            public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                            double x, double y, double z, double xd, double yd, double zd) {
                return new TintedCampfireSmokeParticle(level, x, y, z, xd, yd, zd, sprites, r, g, b);
            }
        };
    }
}
