package net.blazinblaze.blazetwitchmod.auth;

import com.github.philippheuer.credentialmanager.CredentialManager;
import com.github.philippheuer.credentialmanager.CredentialManagerBuilder;
import com.github.philippheuer.credentialmanager.authcontroller.DeviceFlowController;
import com.github.philippheuer.credentialmanager.domain.DeviceAuthorization;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.ITwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.auth.domain.TwitchScopes;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import com.github.twitch4j.common.util.ThreadUtils;
import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.config.BlazinBlazeYAML;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class BlazinBlazeAuth { ;

    public static List<String> grabOAuth() {
        Bot bot = new Bot();
        DeviceAuthorization req = bot.getController().startOAuth2DeviceAuthorizationGrantType(
                bot.getIdentityProvider(),
                Arrays.asList(TwitchScopes.CHAT_READ, TwitchScopes.HELIX_BITS_READ, TwitchScopes.HELIX_CHANNEL_REDEMPTIONS_READ, TwitchScopes.HELIX_CHANNEL_FOLLOWERS_READ, TwitchScopes.HELIX_CHANNEL_SUBSCRIPTIONS_READ, TwitchScopes.HELIX_CHANNEL_PREDICTIONS_READ, TwitchScopes.HELIX_CHANNEL_HYPE_TRAIN_READ),
                resp -> {
                    OAuth2Credential token = resp.getCredential();
                    if (token != null) {
                        BlazinBlazeYAML config = BlazinBlazeTwitchMod.config;
                        if(config != null) {
                            config.setClientInfo(BlazinBlazeYAML.channelToken, token.getAccessToken());
                            config.setClientInfo(BlazinBlazeYAML.refreshToken, token.getRefreshToken());
                            config.setClientInfo(BlazinBlazeYAML.refreshTokenExpiry, Instant.now().getEpochSecond() + 2592000L);
                            config.save();
                        }
                        BlazinBlazeTwitchMod.setCredential(token);
                        bot.start(token);
                    } else {
                        BlazinBlazeTwitchMod.LOGGER.warn("Could not obtain device flow token due to {}", resp.getError());
                    }
                }
        );
        //BlazinBlazeTwitchMod.LOGGER.info("The user should now visit: {}", req.getCompleteUri());
        return List.of(req.getCompleteUri(), req.getDeviceCode());
    }

    public static void grabClient(OAuth2Credential credential) {
        BlazinBlazeTwitchMod.setCredential(credential);
        Bot bot = new Bot();
        bot.start(credential);
    }

    public static class Bot {
        private final TwitchIdentityProvider identityProvider;
        private final ScheduledThreadPoolExecutor executor;
        private final DeviceFlowController controller;
        private final CredentialManager credentialManager;
        private ITwitchClient client;

        Bot() {
            this.identityProvider = new TwitchIdentityProvider(BlazinBlazeTwitchMod.CLIENT_ID, null, null);
            this.executor = ThreadUtils.getDefaultScheduledThreadPoolExecutor("t4j-bot", Runtime.getRuntime().availableProcessors());
            this.controller = new DeviceFlowController(executor, 0);
            this.credentialManager = CredentialManagerBuilder.builder().withAuthenticationController(controller).build();
            this.credentialManager.registerIdentityProvider(identityProvider);
        }

        void start(OAuth2Credential credential) {
            this.client = TwitchClientBuilder.builder()
                    .withClientId(BlazinBlazeTwitchMod.CLIENT_ID)
                    .withScheduledThreadPoolExecutor(executor)
                    .withCredentialManager(credentialManager)
                    .withEnableChat(true)
                    .withChatAccount(credential)
                    .withEnableHelix(true)
                    .withDefaultAuthToken(credential)
                    .build();
            if(this.client != null) {
                BlazinBlazeTwitchMod.setupTwitchClient(this.client, credential.getUserName());
            }
        }

        DeviceFlowController getController() {
            return this.controller;
        }

        TwitchIdentityProvider getIdentityProvider() {
            return this.identityProvider;
        }
    }
}
