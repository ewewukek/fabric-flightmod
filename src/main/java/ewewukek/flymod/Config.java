package ewewukek.flymod;

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
    private static final Logger logger = LogManager.getLogger(FlyMod.class);

    public static boolean conservativeMode = true;

    public static void load() {
        try (BufferedReader reader = Files.newBufferedReader(FlyMod.CONFIG_PATH)) {
            String line = reader.readLine();
            if (line == null) throw new IOException("unexpected end of file");

            line.trim();
            try (Scanner s = new Scanner(line)) {
                s.useLocale(Locale.US);
                s.useDelimiter("\\s*=\\s*");
                if (!s.hasNext()) throw new IOException("expected field name");

                String key = s.next().trim();
                if (!key.equals("conservativeMode")) throw new IOException("unrecognized field name: " + key);

                if (!s.hasNext()) throw new IOException("expected value");
                String value = s.next().trim();

                switch(value) {
                case "true":
                    conservativeMode = true;
                    break;
                case "false":
                    conservativeMode = false;
                    break;
                default:
                    throw new IOException("invalid boolean value: " + value);
                }
            }
            if (reader.readLine() != null) {
                logger.warn("ignoring configuration file contents below first line");
            }
        } catch (NoSuchFileException e) {
            save();

        } catch (IOException e) {
            logger.warn("Could not read configuration file: ", e);
        }
    }

    private static void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(FlyMod.CONFIG_PATH)) {
            writer.write("conservativeMode = " + conservativeMode + "\n");

        } catch (IOException e) {
            logger.warn("Could not save configuration file: ", e);
        }
    }
}
