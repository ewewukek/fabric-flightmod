package ewewukek.flightmod.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import ewewukek.flightmod.Config;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Redirect(
        method = "tickMovement",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;abilityResyncCountdown:I",
            opcode = Opcodes.GETFIELD,
            ordinal = 0
        )
    )
    private int abilityResyncCountdown(ClientPlayerEntity player) {
        if ((Config.airJumpFly || player.abilities.creativeMode) && !player.abilities.flying
        && !player.hasVehicle() && !player.isClimbing() && !player.isOnGround() && !player.isTouchingWater()) {
            ItemStack itemStack = player.getEquippedStack(EquipmentSlot.CHEST);
            if (itemStack.getItem() != Items.ELYTRA || !ElytraItem.isUsable(itemStack)) {
                return 1;
            }
        }
        return ((PlayerEntityAccessor)player).getAbilityResyncCountdown();
    }
}
