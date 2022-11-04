package ewewukek.flightmod;

import java.nio.file.Path;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class FlightMod implements ModInitializer {
    public static final String MODID = "flightmod";
    public static final Path CONFIG_ROOT = FabricLoader.getInstance().getConfigDir();

    public static boolean overrideVanillaFriction;

    @Override
    public void onInitialize() {
        Config.setServer(null);
        Config.load();

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier(MODID, "reload");
            }

            @Override
            public void reload(ResourceManager var1) {
                Config.load();
            }
        });
    }
}
