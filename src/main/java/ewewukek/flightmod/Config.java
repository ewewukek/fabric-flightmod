package ewewukek.flightmod;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.network.ServerInfo;

public class Config {
    private static final Logger logger = LogManager.getLogger(FlightMod.class);

    public static String currentServer;
    public static Path configPath;

    public static boolean enableFlying;
    public static final boolean ENABLE_FLYING_DEFAULT = true;

    public static float flyingCost;
    public static final float FLYING_COST_DEFAULT = 0.025f;

    public static float flyingHorizontalCost;
    public static final float FLYING_HORIZONTAL_COST_DEFAULT = 0.1f;

    public static float flyingUpCost;
    public static final float FLYING_UP_COST_DEFAULT = 0.1f;

    public static boolean doFallDamage;
    public static final boolean DO_FALL_DAMAGE_DEFAULT = true;

    public static boolean flyInWater;
    public static final boolean FLY_IN_WATER_DEFAULT = false;

    public static boolean flyInLava;
    public static final boolean FLY_IN_LAVA_DEFAULT = false;

    public static int foodLevelWarning;
    public static final int FOOD_LEVEL_WARNING_DEFAULT = 6;

    public static MovementMode movementMode;
    public static final MovementMode MOVEMENT_MODE_DEFAULT_SERVER = MovementMode.FULL_SPEED;
    public static final MovementMode MOVEMENT_MODE_DEFAULT_CLIENT = MovementMode.VANILLA;

    public static InertiaCompensationMode inertiaCompensation;
    public static final InertiaCompensationMode INERTIA_COMPENSATION_DEFAULT_SERVER = InertiaCompensationMode.ALWAYS;
    public static final InertiaCompensationMode INERTIA_COMPENSATION_DEFAULT_CLIENT = InertiaCompensationMode.NEVER;

    public static boolean airJumpFly;
    public static final boolean AIR_JUMP_FLY_DEFAULT_SERVER = true;
    public static final boolean AIR_JUMP_FLY_DEFAULT_CLIENT = false;

    public static boolean sneakJumpDrop;
    public static final boolean SNEAK_JUMP_DROP_DEFAULT_SERVER = true;
    public static final boolean SNEAK_JUMP_DROP_DEFAULT_CLIENT = false;

    public static void setDefaults() {
        enableFlying = ENABLE_FLYING_DEFAULT;
        flyingCost = FLYING_COST_DEFAULT;
        flyingHorizontalCost = FLYING_HORIZONTAL_COST_DEFAULT;
        flyingUpCost = FLYING_UP_COST_DEFAULT;
        doFallDamage = DO_FALL_DAMAGE_DEFAULT;
        flyInWater = FLY_IN_WATER_DEFAULT;
        flyInLava = FLY_IN_LAVA_DEFAULT;
        foodLevelWarning = FOOD_LEVEL_WARNING_DEFAULT;

        if (currentServer == null) {
            movementMode = MOVEMENT_MODE_DEFAULT_SERVER;
            inertiaCompensation = INERTIA_COMPENSATION_DEFAULT_SERVER;
            airJumpFly = AIR_JUMP_FLY_DEFAULT_SERVER;
            sneakJumpDrop = SNEAK_JUMP_DROP_DEFAULT_SERVER;
        } else {
            movementMode = MOVEMENT_MODE_DEFAULT_CLIENT;
            inertiaCompensation = INERTIA_COMPENSATION_DEFAULT_CLIENT;
            airJumpFly = AIR_JUMP_FLY_DEFAULT_CLIENT;
            sneakJumpDrop = SNEAK_JUMP_DROP_DEFAULT_CLIENT;
        }
    }

    public static void setServer(ServerInfo server) {
        Path path;
        if (server == null || server.isLocal()) {
            currentServer = null;
            path = FlightMod.CONFIG_ROOT.resolve("flightmod.txt");
        } else {
            currentServer = server.address;
            path = FlightMod.CONFIG_ROOT.resolve("flightmod");
            if (!Files.exists(path)) {
                try {
                    Files.createDirectories(path);
                } catch (IOException e) {
                    logger.warn("Could not create directory: ", e);
                }
            }
            path = path.resolve(currentServer + ".txt");
        }
        if (!path.equals(configPath)) {
            configPath = path;
            load();
        }
    }

    public static void load() {
        setDefaults();
        try (BufferedReader reader = Files.newBufferedReader(configPath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                int commentStart = line.indexOf('#');
                if (commentStart != -1) line = line.substring(0, commentStart);

                line.trim();
                if (line.length() == 0) continue;

                try (Scanner s = new Scanner(line)) {
                    s.useLocale(Locale.US);
                    s.useDelimiter("\\s*=\\s*");

                    if (!s.hasNext()) throw new IOException("expected field name");
                    String key = s.next().trim();

                    if (key.equals("version")) continue;

                    if (!s.hasNext()) throw new IOException("expected value");
                    String value = s.next().trim();

                    switch (key) {
                    case "enableFlying":
                        enableFlying = Boolean.parseBoolean(value);
                        break;
                    case "doFallDamage":
                        doFallDamage = Boolean.parseBoolean(value);
                        break;
                    case "flyInWater":
                        flyInWater = Boolean.parseBoolean(value);
                        break;
                    case "flyInLava":
                        flyInLava = Boolean.parseBoolean(value);
                        break;
                    case "flyingCost":
                        flyingCost = Float.parseFloat(value);
                        break;
                    case "flyingHorizontalCost":
                        flyingHorizontalCost = Float.parseFloat(value);
                        break;
                    case "flyingUpCost":
                        flyingUpCost = Float.parseFloat(value);
                        break;
                    case "foodLevelWarning":
                        foodLevelWarning = Integer.parseInt(value);
                        break;
                    case "movementMode":
                        movementMode = MovementMode.read(value);
                        break;
                    case "inertiaCompensationMode":
                        inertiaCompensation = InertiaCompensationMode.read(value);
                        break;
                    case "airJumpFly":
                        airJumpFly = Boolean.parseBoolean(value);
                        break;
                    case "sneakJumpDrop":
                        sneakJumpDrop = Boolean.parseBoolean(value);
                        break;
                    default:
                        throw new IOException("unrecognized field: " + key);
                    }
                }
            }
        } catch (NoSuchFileException e) {
            save();

        } catch (IOException e) {
            logger.warn("Could not read configuration file: ", e);
        }
    }

    public static void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(configPath)) {
            writer.write("version = 1\n");
            if (currentServer == null) {
                writer.write("enableFlying = " + enableFlying + "\n");
                writer.write("doFallDamage = " + doFallDamage + "\n");
                writer.write("flyInWater = " + flyInWater + "\n");
                writer.write("flyInLava = " + flyInLava + "\n");
                writer.write("flyingCost = " + flyingCost + "\n");
                writer.write("flyingHorizontalCost = " + flyingHorizontalCost + "\n");
                writer.write("flyingUpCost = " + flyingUpCost + "\n");
                writer.write("foodLevelWarning = " + foodLevelWarning + "\n");
            }
            writer.write("movementMode = " + movementMode + "\n");
            writer.write("inertiaCompensationMode = " + inertiaCompensation + "\n");
            writer.write("airJumpFly = " + airJumpFly + "\n");
            writer.write("sneakJumpDrop = " + sneakJumpDrop + "\n");

        } catch (IOException e) {
            logger.warn("Could not save configuration file: ", e);
        }
    }

    public enum MovementMode {
        VANILLA("vanilla"),
        VANILLA_VERTICAL("vanilla_vertical"),
        FULL_SPEED("full_speed");

        private final String value;

        MovementMode(String value) {
            this.value = value;
        }

        public static MovementMode read(String value) throws IOException {
            if (value.equals(VANILLA.value)) return VANILLA;
            if (value.equals(VANILLA_VERTICAL.value)) return VANILLA_VERTICAL;
            if (value.equals(FULL_SPEED.value)) return FULL_SPEED;
            throw new IOException("invalid movement mode value: " + value);
        }

        public MovementMode next() {
            switch (this) {
            case VANILLA:
                return VANILLA_VERTICAL;
            case VANILLA_VERTICAL:
                return FULL_SPEED;
            case FULL_SPEED:
                return VANILLA;
            }
            throw new RuntimeException("invalid mode");
        }

        public boolean enabled() {
            return this != VANILLA;
        }

        public boolean fullSpeed() {
            return this == FULL_SPEED;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public enum InertiaCompensationMode {
        NEVER("never"),
        MOVING_FORWARD("moving_forward"),
        ALWAYS("always");

        private final String value;

        InertiaCompensationMode(String value) {
            this.value = value;
        }

        public static InertiaCompensationMode read(String value) throws IOException {
            if (value.equals(NEVER.value)) return NEVER;
            if (value.equals(MOVING_FORWARD.value)) return MOVING_FORWARD;
            if (value.equals(ALWAYS.value)) return ALWAYS;
            throw new IOException("invalid inertia compensation mode value: " + value);
        }

        public InertiaCompensationMode next() {
            switch (this) {
            case NEVER:
                return MOVING_FORWARD;
            case MOVING_FORWARD:
                return ALWAYS;
            case ALWAYS:
                return NEVER;
            }
            throw new RuntimeException("invalid mode");
        }

        public boolean enabled() {
            return this != NEVER;
        }

        public boolean always() {
            return this == ALWAYS;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
