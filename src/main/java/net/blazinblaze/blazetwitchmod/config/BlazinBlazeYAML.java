package net.blazinblaze.blazetwitchmod.config;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.reference.BlazinBlazeProbabilities;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BlazinBlazeYAML {

    public static final String channelToken = "channelToken";
    public static final String refreshToken = "refreshToken";
    public static final String refreshTokenExpiry = "refreshTokenExpiry";
    public static final String botYTName = "botYTName";
    public static final String clientConfigKey = "clientConfig";

    public static final String probabilityConfigKey = "probabilityConfig";

    private final File file;
    private Map<String, Map<String, Object>> config = new LinkedHashMap<String, Map<String, Object>>();
    private Map<String, Object> clientConfig = new LinkedHashMap<String, Object>();
    private Map<String, Object> probabilityConfig = new LinkedHashMap<String, Object>();

    private DumperOptions yamlOptions = new DumperOptions();
    private Yaml yaml;

    private void setDefaults() {
        clientConfig.put(channelToken, "");
        clientConfig.put(refreshToken, "");
        clientConfig.put(refreshTokenExpiry, 0L);
        clientConfig.put(botYTName, "");

        for (BlazinBlazeProbabilities probability : BlazinBlazeProbabilities.values()) {
            probabilityConfig.put(probability.getKey(), probability.getProbabilty());
        }

        config.put(clientConfigKey, clientConfig);
        config.put(probabilityConfigKey, probabilityConfig);

        save();
    }

    public void save() {
        config.put(clientConfigKey, clientConfig);
        config.put(probabilityConfigKey, probabilityConfig);
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            yaml.dump(config, writer);
        } catch(IOException | YAMLException e) {
            BlazinBlazeTwitchMod.LOGGER.error("Error while saving yaml config file.", e);
        }
    }

    public void load() {
        if(file.exists()) {
            try {
                InputStream stream = new FileInputStream(file.getAbsolutePath());
                Map<String, Map<String, Object>> data = yaml.load(stream);
                clientConfig = data.get(clientConfigKey);
                probabilityConfig = data.get(probabilityConfigKey);
            }catch(FileNotFoundException e) {
                BlazinBlazeTwitchMod.LOGGER.error("Config file not found.", e);
            }
        }else {
            createNew();
        }
    }

    public void setClientInfo(String key, Object val) {
        if(key.equals(channelToken) || key.equals(refreshToken) || key.equals(botYTName)) {
            if(val instanceof String) {
                clientConfig.put(key, val);
            }
        }else if (key.equals(refreshTokenExpiry)) {
            if(val instanceof Integer || val instanceof Long) {
                clientConfig.put(key, val);
            }
        }
    }

    public Object getClientInfo(String key) {
        Object temp = clientConfig.get(key);
        if(temp instanceof String || temp instanceof Long || temp instanceof Integer) {
            return temp;
        }else {
            return null;
        }
    }

    public Double getProbability(String key) {
        Object temp = probabilityConfig.get(key);
        if(temp instanceof Double) {
            return (double)temp;
        }else if(temp instanceof Integer){
            if((int)temp >= 1) {
                return (double)1;
            }
            return (double)0;
        }else {
            return null;
        }
    }

    public BlazinBlazeYAML(File file) {
        this.file = file;
        yamlOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK); // For block style YAML
        yamlOptions.setPrettyFlow(true);
        yamlOptions.setProcessComments(true);
        yaml = new Yaml(yamlOptions);
    }

    private void createNew() {
        setDefaults();
        save();
    }
}
