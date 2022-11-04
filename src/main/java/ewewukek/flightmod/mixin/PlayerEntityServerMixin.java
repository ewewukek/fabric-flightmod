package ewewukek.flightmod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ewewukek.flightmod.Config;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tag.FluidTags;

@Mixin(PlayerEntity.class)
public class PlayerEntityServerMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        PlayerAbilities abilities = player.getAbilities();

        if (player.world.isClient) return;

        boolean prevAllowFlying = abilities.allowFlying;
        boolean prevFlying = abilities.flying;

        abilities.allowFlying = player.getHungerManager().getFoodLevel() > 0;
        if (!Config.flyInWater && player.isSubmergedIn(FluidTags.WATER)) {
            abilities.allowFlying = false;
        }
        if (!Config.flyInLava && player.isSubmergedIn(FluidTags.LAVA)) {
            abilities.allowFlying = false;
        }

        if (!abilities.allowFlying) abilities.flying = false;

        if (abilities.flying) {
            player.addExhaustion(Config.flyingCost);
        }

        if (abilities.allowFlying != prevAllowFlying || abilities.flying != prevFlying) {
            player.sendAbilitiesUpdate();
        }
    }

    @Inject(method = "increaseTravelMotionStats", at = @At("TAIL"))
    public void increaseTravelMotionStats(double dx, double dy, double dz, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;

        if (player.world.isClient) return;

        if (player.getAbilities().flying) {
            float r = 0.01f * Math.round(100 * (float)Math.sqrt(dx * dx + dz * dz));
            player.addExhaustion(Config.flyingHorizontalCost * r);
            if (dy > 0) {
                player.addExhaustion(Config.flyingUpCost * 0.01f * Math.round(100 * dy));
            }
        }
    }

    @Redirect(method = "handleFallDamage", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerAbilities;allowFlying:Z"))
    public boolean allowFlying(PlayerAbilities abilities) {
        return Config.disableFallDamage || abilities.creativeMode;
    }
}
