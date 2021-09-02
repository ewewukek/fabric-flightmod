package ewewukek.flightmod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import ewewukek.flightmod.Config;
import ewewukek.flightmod.FlightMod;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Redirect(
        method = "tickMovement",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;travel(Lnet/minecraft/util/math/Vec3d;)V",
            ordinal = 0
        )
    )
    void travel(LivingEntity entity, Vec3d input) {
        final float deg2rad = (float)(Math.PI / 180);

        if (FlightMod.shouldExecute(entity) && input.lengthSquared() > 1e-7) {
            ClientPlayerEntity player = (ClientPlayerEntity)entity;

            float flyingSpeed = player.abilities.getFlySpeed() * (player.isSprinting() ? 2 : 1);

            float cp = MathHelper.cos(deg2rad * player.pitch);
            float sp = MathHelper.sin(deg2rad * player.pitch);

            if (!Config.conservativeMode) {
                int iy = 0;
                if (player.input.jumping) iy++;
                if (player.input.sneaking) iy--;

                double f = input.z * flyingSpeed;
                double yv = iy * 3 * player.abilities.getFlySpeed();
                double vl = Math.sqrt(f * f + yv * yv);
                double y = yv / vl;
                double inputZ;

                if ((iy > 0 && -sp > y) || (iy < 0 && -sp < y)) {
                    player.setVelocity(player.getVelocity().add(0, -sp * flyingSpeed - yv, 0));
                    FlightMod.overrideVerticalFriction = true;
                    inputZ = cp;
                } else {
                    inputZ = f / vl;
                }
                input = new Vec3d(input.x, input.y, inputZ);

            } else if (Math.abs(sp) > 1e-3) {
                Vec3d v = player.getVelocity();
                if (Math.abs(v.y) > 0.003) {
                    double f = v.dotProduct(Vec3d.fromPolar(0, player.yaw)) + input.z * flyingSpeed;
                    double l = Math.abs(v.y / sp);
                    double t = l * cp;
                    double z = (t - f) / flyingSpeed;
                    input = new Vec3d(input.x, input.y, MathHelper.clamp(z, -1, 1));
                }
            }
        }
        entity.travel(input);
    }
}
