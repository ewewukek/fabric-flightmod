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

    public static MovementMode movementMode;
    public static InertiaCompensationMode inertiaCompensation;
    public static boolean airJumpFly;
    public static boolean sneakJumpDrop;

    public static void setDefaults() {
        movementMode = MovementMode.VANILLA;
        inertiaCompensation = InertiaCompensationMode.NEVER;
        airJumpFly = false;
        sneakJumpDrop = false;
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
                    case "movementMode":
                        movementMode = MovementMode.read(value);
                        break;
                    case "inertiaCompensationMode":
                        inertiaCompensation = InertiaCompensationMode.read(value);
                        break;
                    case "airJumpFly":
                        airJumpFly = readBoolean(value);
                        break;
                    case "sneakJumpDrop":
                        sneakJumpDrop = readBoolean(value);
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
            writer.write("movementMode = " + movementMode + "\n");
            writer.write("inertiaCompensationMode = " + inertiaCompensation + "\n");
            writer.write("airJumpFly = " + airJumpFly + "\n");
            writer.write("sneakJumpDrop = " + sneakJumpDrop + "\n");

        } catch (IOException e) {
            logger.warn("Could not save configuration file: ", e);
        }
    }

    public static boolean readBoolean(String value) throws IOException {
        if (value.equals("true")) {
            return true;
        } else if (value.equals("false")) {
            return false;
        }

        throw new IOException("invalid boolean value: " + value);
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
