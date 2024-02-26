package ewewukek.flightmod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ewewukek.flightmod.Config;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "increaseTravelMotionStats", at = @At("TAIL"))
    private void increaseTravelMotionStats(double dx, double dy, double dz, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        PlayerAbilities abilities = player.getAbilities();
        World world = player.getWorld();

        if (world.isClient || abilities.invulnerable) return;

        if (abilities.flying) {
            float r = 0.01f * Math.round(100 * (float)Math.sqrt(dx * dx + dz * dz));
            player.addExhaustion(Config.flyingHorizontalCost * r);
            if (dy > 0) {
                player.addExhaustion(Config.flyingUpCost * 0.01f * Math.round(100 * dy));
            }
        }
    }
}
