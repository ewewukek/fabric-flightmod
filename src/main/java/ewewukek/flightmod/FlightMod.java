package ewewukek.flightmod;

import java.nio.file.Path;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class FlightMod implements ClientModInitializer {
    public static final Path CONFIG_ROOT = FabricLoader.getInstance().getConfigDir();

    public static boolean overrideVanillaFriction;

    @Override
    public void onInitializeClient() {
        Config.setServer(null);
    }
}
