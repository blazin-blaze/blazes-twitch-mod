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
    public static final String tokenExpires = "tokenExpires";
    public static final String clientConfigKey = "clientConfig";

    public static final String probabilityConfigKey = "probabilityConfig";

    // static final String defaultClientID = "[Place your ClientID here (Create this in the Twitch Developer portal through an app)]";
    //public static final String defaultClientSecret = "[Place your Client Secret here (Create this in the Twitch Developer portal through an app)]";
    public static final String defaultChannelToken = "[Place your Channel OAuth Token here (This provides proper channel permissions to the mod. Create this using following link and replace [CLIENTID] with your client id: https://id.twitch.tv/oauth2/authorize?response_type=token&client_id=[CLIENTID]&redirect_uri=http://localhost&scope=chat%3Aread%20bits%3Aread%20channel%3Aread%3Acharity%20channel%3Aread%3Ahype_train%20channel%3Aread%3Apolls%20channel%3Aread%3Apredictions%20channel%3Aread%3Aredemptions%20channel%3Aread%3Asubscriptions&state=c3ab8aa609ea11e793ae92361f002671&20moderator%3Aread%3Afollowers)]";
    public static final String defaultClientName = "[Place your Channel Name here (Your Twitch Username)]";

    private final File file;
    private Map<String, Map<String, Object>> config = new LinkedHashMap<String, Map<String, Object>>();
    private Map<String, Object> clientConfig = new LinkedHashMap<String, Object>();
    private Map<String, Object> probabilityConfig = new LinkedHashMap<String, Object>();

    private DumperOptions yamlOptions = new DumperOptions();
    private Yaml yaml;

    private void setDefaults() {
        clientConfig.put(channelToken, "");
        clientConfig.put(tokenExpires, 0L);

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
        if(key.equals(channelToken) || key.equals(tokenExpires)) {
            clientConfig.put(key, val);
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
