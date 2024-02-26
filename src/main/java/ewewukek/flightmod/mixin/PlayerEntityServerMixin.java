package ewewukek.flightmod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ewewukek.flightmod.Config;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public class PlayerEntityServerMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        PlayerAbilities abilities = player.getAbilities();
        World world = player.getWorld();

        if (world.isClient || abilities.invulnerable) return;

        if (!Config.enableFlying) {
            if (abilities.allowFlying || abilities.flying) {
                abilities.allowFlying = abilities.flying = false;
                player.sendAbilitiesUpdate();
            }
            return;
        }

        boolean prevAllowFlying = abilities.allowFlying;
        boolean prevFlying = abilities.flying;

        abilities.allowFlying = !player.hasVehicle() && player.getHungerManager().getFoodLevel() > 0;

        if (!Config.flyInWater && player.getFluidHeight(FluidTags.WATER) > 0.0
        || !Config.flyInLava && player.getFluidHeight(FluidTags.LAVA) > 0.0
        || !Config.flyInSlowBlocks && isTouchingSlowingBlock(player)) {

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

    private boolean isTouchingSlowingBlock(PlayerEntity player) {
        if (player.isRegionUnloaded()) {
            return false;
        }

        Box box = player.getBoundingBox().contract(0.001);
        BlockPos.Mutable blockPos = new BlockPos.Mutable();
        World world = player.getWorld();

        for (int x = MathHelper.floor(box.minX); x < MathHelper.ceil(box.maxX); ++x) {
            for (int y = MathHelper.floor(box.minY); y < MathHelper.ceil(box.maxY); ++y) {
                for (int z = MathHelper.floor(box.minZ); z < MathHelper.ceil(box.maxZ); ++z) {
                    blockPos.set(x, y, z);
                    Block block = world.getBlockState(blockPos).getBlock();
                    if (block == Blocks.COBWEB || block == Blocks.SWEET_BERRY_BUSH || block == Blocks.POWDER_SNOW) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Redirect(
        method = "handleFallDamage",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/player/PlayerAbilities;allowFlying:Z"
        )
    )
    private boolean handleFallDamagePatch(PlayerAbilities abilities) {
        if (abilities.invulnerable) { // vanilla behavior
            return abilities.allowFlying;
        }
        return !Config.doFallDamage;
    }
}
