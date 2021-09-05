package ewewukek.flightmod;

import java.nio.file.Path;

import net.fabricmc.loader.api.FabricLoader;

public class FlightMod {
    public static final Path CONFIG_ROOT = FabricLoader.getInstance().getConfigDir();

    public static boolean overrideVanillaFriction;
}
