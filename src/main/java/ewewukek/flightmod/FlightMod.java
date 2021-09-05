package ewewukek.flightmod;

import java.nio.file.Path;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class FlightMod implements ClientModInitializer {
    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("flightmod.txt");

    public static boolean overrideVanillaFriction;

    @Override
    public void onInitializeClient() {
        Config.load();
    }
}
