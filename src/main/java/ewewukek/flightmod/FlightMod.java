package ewewukek.flightmod;

import java.nio.file.Path;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class FlightMod implements ModInitializer {
    public static final String MODID = "flightmod";
    public static final Path CONFIG_ROOT = FabricLoader.getInstance().getConfigDir();

    public static boolean overrideVanillaFriction;

    @Override
    public void onInitialize() {
        Config.setServer(null);
        Config.load();
    }
}
