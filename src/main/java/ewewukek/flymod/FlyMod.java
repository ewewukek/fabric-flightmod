package ewewukek.flymod;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;

public class FlyMod {
    public static boolean conservativeMode = true;
    public static boolean overrideVerticalFriction;

    public static boolean shouldExecute(LivingEntity entity) {
        if (!(entity instanceof ClientPlayerEntity)) return false;
        ClientPlayerEntity player = (ClientPlayerEntity)entity;

        return player.abilities.flying && player.input.pressingForward
            && (player.input.jumping || player.input.sneaking);
    }
}
