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
    private void travel(LivingEntity entity, Vec3d input) {
        if (entity instanceof ClientPlayerEntity) {
            input = transformInput((ClientPlayerEntity)entity, input);
        }
        entity.travel(input);
    }

    private static Vec3d transformInput(ClientPlayerEntity player, Vec3d input) {
        final float deg2rad = (float)(Math.PI / 180);

        float cp = MathHelper.cos(deg2rad * player.pitch);
        float sp = MathHelper.sin(deg2rad * player.pitch);
        Vec3d v = player.getVelocity();

        boolean shouldApply = player.abilities.flying && player.input.pressingForward
            && input.lengthSquared() > 1e-7 && (
                player.input.jumping && player.pitch < -1e-3
                || player.input.sneaking && player.pitch > 1e-3
            );

        if (shouldApply) {
            if (input.lengthSquared() > 1) input = input.normalize();

            float flySpeed = player.abilities.getFlySpeed();
            float speed = flySpeed * (player.isSprinting() ? 2 : 1);
            double maxSpeed = speed * 0.91 / 0.09;

            double f = v.dotProduct(Vec3d.fromPolar(0, player.yaw));
            double l = Math.abs(v.y / sp);

            if (!Config.conservativeMode) {
                int iy = 0;
                if (player.input.jumping) iy++;
                if (player.input.sneaking) iy--;
                double vy = v.y - sp * speed - iy * 3 * flySpeed;
                player.setVelocity(v.x, vy, v.z);
                FlightMod.overrideVerticalFriction = true;
                l = Math.abs(vy / sp);
            }

            double t = l * cp;
            double z = (t - f) / speed;
            double maxZ = Math.abs(input.z);
            input = new Vec3d(input.x, input.y, MathHelper.clamp(z, -maxZ, maxZ));
        }

        return input;
    }
}
