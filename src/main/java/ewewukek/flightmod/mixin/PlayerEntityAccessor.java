package ewewukek.flightmod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.player.PlayerEntity;

@Mixin(PlayerEntity.class)
public interface PlayerEntityAccessor {
    @Accessor("abilityResyncCountdown") int getAbilityResyncCountdown();
    @Accessor("abilityResyncCountdown") void setAbilityResyncCountdown(int value);
}
