package net.blazinblaze.blazetwitchmod.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BlazinBlazeConfig {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting() // Makes the json use new lines instead of being a "one-liner"
            .serializeNulls() // Makes fields with `null` value to be written as well.
            .disableHtmlEscaping() // We'll be able to use custom chars without them being saved differently
            .create();

    @SerializedName("// Blazin Twitch Mod CONFIG")
    public final String _comment_title = "";

    @SerializedName("// Make sure to surround each value with double quotation marks.")
    public final String _comment_notice = "";

    @SerializedName("// Your application's Client ID. Create this in the Twitch Developer portal.")
    public final String _comment_clientID = "example: giutghwermej7bzximzuOmuqr3bauc";
    @SerializedName("Client ID")
    public String clientID;

    @SerializedName("// Your application's Client Secret. Create this in the Twitch Developer portal.")
    public final String _comment_clientSecret = "example: roijsmhf4lhgi3m9tfwp8m3scpnlc3";
    @SerializedName("Client Secret")
    public String clientSecret;

    @SerializedName("// Your channel's Token. This provides proper channel permissions to the mod. Create this using the link:")
    public final String _comment_channelToken = "example: fdktk0mbimtbxoauk8Ojb2nkecypqr";
    @SerializedName("// https://id.twitch.tv/oauth2/authorize?response_type=token&client_id=[CLIENTID]&redirect_uri=http://localhost&scope=chat%3Aread%20bits%3Aread%20channel%3Aread%3Acharity%20channel%3Aread%3Ahype_train%20channel%3Aread%3Apolls%20channel%3Aread%3Apredictions%20channel%3Aread%3Aredemptions%20channel%3Aread%3Asubscriptions&state=c3ab8aa609ea11e793ae92361f002671&20moderator%3Aread%3Afollowers")
    public final String _comment_channelToken2 = "";
    @SerializedName("// Replace [CLIENTID] with your app's client id. Copy the access token found in the redirect URL between 'access_token=' and '&'")
    public final String _comment_channelToken3 = "example: localhost/#access_token=[YOUR_TOKEN]&";
    @SerializedName("Channel Token")
    public String channelToken;

    @SerializedName("// Your channel's username. NOT your display name; it is your full Twitch username.")
    public final String _comment_channelName = "example: blazinblaze_";
    @SerializedName("Channel Name")
    public String clientName;

    public static BlazinBlazeConfig loadConfigFile(File file) {
        BlazinBlazeConfig config = null;

        if (file.exists()) {
            // An existing config is present, we should use its values
            try (BufferedReader fileReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)
            )) {
                // Parses the config file and puts the values into config object
                config = gson.fromJson(fileReader, BlazinBlazeConfig.class);
            } catch (IOException e) {
                throw new RuntimeException("Problem occurred when trying to load config: ", e);
            }
        }
        // gson.fromJson() can return null if file is empty
        if (config == null) {
            config = new BlazinBlazeConfig();
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
