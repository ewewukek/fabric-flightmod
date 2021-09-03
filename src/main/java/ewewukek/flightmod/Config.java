package ewewukek.flightmod;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.Locale;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Config {
    private static final Logger logger = LogManager.getLogger(FlightMod.class);

    public static boolean compensateInertia = true;
    public static boolean vanillaVerticalVelocity = false;

    public static void load() {
        try (BufferedReader reader = Files.newBufferedReader(FlightMod.CONFIG_PATH)) {
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

                    boolean boolValue = false;
                    if (value.equals("true")) {
                        vanillaVerticalVelocity = true;
                    } else if (value.equals("false")) {
                        vanillaVerticalVelocity = false;
                    } else {
                        throw new IOException("invalid boolean value: " + value);
                    }

                    switch (key) {
                    case "compensateInertia":
                        compensateInertia = boolValue;
                        break;
                    case "vanillaVerticalVelocity":
                        vanillaVerticalVelocity = boolValue;
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
        try (BufferedWriter writer = Files.newBufferedWriter(FlightMod.CONFIG_PATH)) {
            writer.write("version = 1\n");
            writer.write("compensateInertia = " + compensateInertia + "\n");
            writer.write("vanillaVerticalVelocity = " + vanillaVerticalVelocity + "\n");

        } catch (IOException e) {
            logger.warn("Could not save configuration file: ", e);
        }
    }
}
