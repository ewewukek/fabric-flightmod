package ewewukek.flymod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import ewewukek.flymod.FlyMod;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Redirect(
        method = "travel",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;setVelocity(DDD)V",
            ordinal = 0
        )
    )
    private void setVelocity(PlayerEntity player, double x, double y, double z) {
        if (FlyMod.overrideVerticalFriction && FlyMod.shouldExecute(player)) {
            FlyMod.overrideVerticalFriction = false;
            y = y / 0.6 * 0.91;
        }
        player.setVelocity(x, y, z);
    }
}
