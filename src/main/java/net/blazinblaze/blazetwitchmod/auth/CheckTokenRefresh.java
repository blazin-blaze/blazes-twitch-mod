package net.blazinblaze.blazetwitchmod.auth;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

import java.util.Optional;

public class CheckTokenRefresh implements Runnable {

    private final OAuth2Credential credential;

    public CheckTokenRefresh(OAuth2Credential credential) {
        this.credential = credential;
    }

    private void updateCred(TwitchIdentityProvider provider) {
        OAuth2Credential newCred = BlazinBlazeTwitchMod.refreshToken(provider, credential.getAccessToken());
        boolean hasUpdated = BlazinBlazeTwitchMod.updateCredential(newCred);
        MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

        if(server != null) {
            server.getPlayerManager().broadcast(Text.of("§5§l[BTM]§r §eThe Twitch Access Token is close to expiring. Attempting to update token to prevent future errors."), false);
        }

        if(!hasUpdated) {
            if(server != null) {
                server.getPlayerManager().broadcast(Text.empty(), (player) -> {
                    if(!server.isSingleplayer()) {
                        if(!server.getPlayerManager().isOperator(player.getGameProfile()) && !server.isHost(player.getGameProfile())) {
                            return Text.of("§5§l[BTM]§r §cToken expiry check failed. The server owner/host must re-authenticate to avoid future errors.");
                        }
                    }
                    return Text.of("§5§l[BTM]§r §cToken expiry check failed. You must re-authenticate to avoid future errors.");
                }, false);
            }
            BlazinBlazeTwitchMod.closeTwitchClient();
            BlazinBlazeTwitchMod.createNewToken();
        }
    }

    @Override
    public void run() {
        TwitchIdentityProvider identityProvider = new TwitchIdentityProvider(BlazinBlazeTwitchMod.CLIENT_ID, null, null);
        Optional<OAuth2Credential> additionalCredentialInformation = identityProvider.getAdditionalCredentialInformation(credential);
        MinecraftServer server = BlazinBlazeTwitchMod.serverRef;

        server.getPlayerManager().broadcast(Text.of("§5§l[BTM]§r §eToken refresh check."), false);
        if(additionalCredentialInformation.isPresent()) {
            OAuth2Credential cred = additionalCredentialInformation.get();
            int expiresIn = cred.getExpiresIn();
            if(expiresIn < 3600) {
                updateCred(identityProvider);
            }
        }else {
            updateCred(identityProvider);
        }
    }
}
