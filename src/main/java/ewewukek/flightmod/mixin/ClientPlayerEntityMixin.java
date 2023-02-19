package ewewukek.flightmod.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ewewukek.flightmod.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stat.StatHandler;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onConnect(MinecraftClient client, ClientWorld world, ClientPlayNetworkHandler networkHandler, StatHandler stats, ClientRecipeBook recipeBook, boolean lastSneaking, boolean lastSprinting, CallbackInfo ci) {
        Config.setServer(client.getCurrentServerEntry());
    }

    @Redirect(
        method = "canSprint",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/player/PlayerAbilities;allowFlying:Z",
            ordinal = 0
        )
    )
    private boolean canSprint(PlayerAbilities abilities) {
        return abilities.invulnerable;
    }

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
        if (Config.airJumpFly && !player.getAbilities().flying && !player.isOnGround()
        && !player.hasVehicle() && !player.isClimbing() && !player.isTouchingWater()) {
            ItemStack itemStack = player.getEquippedStack(EquipmentSlot.CHEST);
            if (itemStack.getItem() != Items.ELYTRA || !ElytraItem.isUsable(itemStack)) {
                return 1;
            }
        }
        if (Config.sneakJumpDrop && player.input.sneaking && player.getAbilities().flying) {
            return 1;
        }
        return player.abilityResyncCountdown;
    }
}
