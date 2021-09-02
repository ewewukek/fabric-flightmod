package ewewukek.flightmod;

import java.nio.file.Path;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;

public class FlightMod implements ClientModInitializer {
    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("flightmod.txt");

    public static boolean overrideVerticalFriction;

    @Override
    public void onInitializeClient() {
        Config.load();
    }

    public static boolean shouldExecute(LivingEntity entity) {
        if (!(entity instanceof ClientPlayerEntity)) return false;
        ClientPlayerEntity player = (ClientPlayerEntity)entity;

        return player.abilities.flying && player.input.pressingForward
            && (player.input.jumping || player.input.sneaking);
    }
}
