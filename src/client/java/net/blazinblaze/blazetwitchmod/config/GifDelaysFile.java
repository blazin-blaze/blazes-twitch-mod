package net.blazinblaze.blazetwitchmod.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import com.mojang.blaze3d.platform.NativeImage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GifDelaysFile {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting() // Makes the json use new lines instead of being a "one-liner"
            .serializeNulls() // Makes fields with `null` value to be written as well.
            .disableHtmlEscaping() // We'll be able to use custom chars without them being saved differently
            .create();

    public ConcurrentMap<String, ArrayList<Integer>> gifDelays = new ConcurrentHashMap<>();

    public static GifDelaysFile loadConfigFile(File file) {
        GifDelaysFile config = null;

        if (file.exists()) {
            // An existing config is present, we should use its values
            try (BufferedReader fileReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)
            )) {
                // Parses the config file and puts the values into config object
                config = gson.fromJson(fileReader, GifDelaysFile.class);
            } catch (IOException e) {
                throw new RuntimeException("Problem occurred when trying to load config: ", e);
            }
        }
        // gson.fromJson() can return null if file is empty
        if (config == null) {
            config = new GifDelaysFile();
        }

        // Saves the file in order to write new fields if they were added
        config.saveConfigFile(file);
        return config;
    }


    public void saveConfigFile(File file) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            gson.toJson(this, writer);
        } catch (IOException e) {
            BlazinBlazeTwitchMod.LOGGER.error("Failed to save config file", e);
        }
    }
}
