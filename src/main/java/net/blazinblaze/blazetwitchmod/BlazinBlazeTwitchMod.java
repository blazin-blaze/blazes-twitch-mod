package net.blazinblaze.blazetwitchmod;

import com.github.philippheuer.credentialmanager.CredentialManager;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.credentialmanager.identityprovider.OAuth2IdentityProvider;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.ITwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import com.github.twitch4j.helix.domain.UserList;
import com.netflix.hystrix.HystrixCommand;
import io.github.iprodigy.bttv.api.BetterTTV;
import io.github.iprodigy.bttv.api.BttvApiFactory;
import io.github.iprodigy.bttv.api.domain.User;
import io.github.iprodigy.bttv.common.Emote;
import io.github.iprodigy.bttv.common.Provider;
import joptsimple.internal.Strings;
import net.blazinblaze.blazetwitchmod.auth.BlazinBlazeAuth;
import net.blazinblaze.blazetwitchmod.auth.CheckTokenRefresh;
import net.blazinblaze.blazetwitchmod.block.BlazinBlazeTwitchModBlocks;
import net.blazinblaze.blazetwitchmod.config.BlazinBlazeYAML;
import net.blazinblaze.blazetwitchmod.data.BlazinBlazeAttachmentTypes;
import net.blazinblaze.blazetwitchmod.data.LootCrateAttachmentData;
import net.blazinblaze.blazetwitchmod.data.MultiplierAttachmentData;
import net.blazinblaze.blazetwitchmod.data.TimerAttachmentData;
import net.blazinblaze.blazetwitchmod.effect.ImmunityShieldEffect;
import net.blazinblaze.blazetwitchmod.effect.RandomTeleportEffect;
import net.blazinblaze.blazetwitchmod.entity.BlazinBlazeTwitchModEntities;
import net.blazinblaze.blazetwitchmod.entity.custom.MegaCreeperEntity;
import net.blazinblaze.blazetwitchmod.item.BlazinBlazeTwitchModItems;
import net.blazinblaze.blazetwitchmod.networking.*;
import net.blazinblaze.blazetwitchmod.sound.BlazinBlazeTwitchModSounds;
import net.blazinblaze.blazetwitchmod.util.MultiplierTick;
import net.blazinblaze.blazetwitchmod.util.SoundMessageTimer;
import net.blazinblaze.blazetwitchmod.util.TeleportTimer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.TextAction;
import java.io.File;
import java.net.URI;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class BlazinBlazeTwitchMod implements ModInitializer {
	public static final String MOD_ID = "blaze-twitch-mod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final RegistryEntry<StatusEffect> RANDOM_TELEPORT =
			Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MOD_ID, "random_teleport"), new RandomTeleportEffect());

	public static final RegistryEntry<StatusEffect> IMMUNITY_SHIELD_EFFECT =
			Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MOD_ID, "immunity_shield_effect"), new ImmunityShieldEffect());

	private static ITwitchClient client;
	public static String uri;
	private static String code;
	private static OAuth2Credential clientCredential;

	public static MinecraftServer serverRef;
	public static BetterTTV bAPI;
	public static Collection<io.github.iprodigy.bttv.common.Emote> globalEmotes;
	public static Map<String, List<Emote>> channelEmotes = new HashMap<>();
	public static Map<String, List<Emote>> sharedEmotes = new HashMap<>();;
	public static BlazinBlazeYAML config;

	public static final String CLIENT_ID = "hukytbnqolhx8d6t4ofj7mx0o8ezeu";

	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private static ScheduledFuture<?> checkTokenRefreshTask;

	@Override
	public void onInitialize() {

		BlazinBlazeTwitchModEntities.registerModEntites();
		BlazinBlazeTwitchModEntities.registerAttributes();

		FabricDefaultAttributeRegistry.register(BlazinBlazeTwitchModEntities.MEGA_CREEPER, MegaCreeperEntity.createCreeperAttributes());

		BlazinBlazeTwitchModItems.initialize();
		BlazinBlazeTwitchModBlocks.initialize();
		BlazinBlazeTwitchModSounds.initialize();
		BlazinBlazeAttachmentTypes.init();

		MultiplierTick.register();
		SoundMessageTimer.register();
		TeleportTimer.register();

		PayloadTypeRegistry.playS2C().register(MailScreenS2CPayload.ID, MailScreenS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(FollowNotifyS2CPayload.ID, FollowNotifyS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(PlaySoundS2CPayload.ID, PlaySoundS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(OverlayMessageS2CPayload.ID, OverlayMessageS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(VoiceMessageS2CPayload.ID, VoiceMessageS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(EmoteScreenS2CPayload.ID, EmoteScreenS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(FullscreenS2CPayload.ID, FullscreenS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(CloseS2CPayload.ID, CloseS2CPayload.CODEC);

		//File configFile = new File(FabricLoader.getInstance().getConfigDir().resolve("btm-config.json").toString());
		//BlazinBlazeConfig config = BlazinBlazeConfig.loadConfigFile(configFile);

		File configFileYaml = new File(FabricLoader.getInstance().getConfigDir().resolve("btm-config.yaml").toString());
		config = new BlazinBlazeYAML(configFileYaml);
		config.load();

		//String clientName = config.getClientInfo(BlazinBlazeYAML.clientName);
		//String channelToken = config.getClientInfo(BlazinBlazeYAML.channelToken);

		/*if((clientName != null && !clientName.equals(BlazinBlazeYAML.defaultClientName) && !clientName.isEmpty()) &&
				(channelToken != null && !channelToken.equals(BlazinBlazeYAML.defaultChannelToken) && !channelToken.isEmpty())) {
			// Build credential when possible (old OAuth)
			String token = channelToken;
			OAuth2Credential credential = StringUtils.isNotBlank(token) ? new OAuth2Credential("twitch", token) : null;

			// Build TwitchClient
			client = TwitchClientBuilder.builder()
					.withClientId(clientID)
					.withClientSecret(clientSecret)
					.withEnableChat(true)
					.withChatAccount(credential)
					.withEnableHelix(true)
					.withDefaultAuthToken(credential)
					.withChatCommandsViaHelix(false)
					.build();

			client.getChat().joinChannel(clientName);
			client.getClientHelper().enableFollowEventListener(clientName);
			client.getClientHelper().enableStreamEventListener(clientName);

			// Register event listeners
			client.getEventManager().getEventHandler(SimpleEventHandler.class).registerListener(new BlazinBlazeTwitchModEvent());

			bAPI = BttvApiFactory.build();
			globalEmotes = BlazinBlazeTwitchMod.bAPI.getGlobalEmotes();
			HystrixCommand<UserList> hystrixUsers = client.getHelix().getUsers(null, null, List.of(clientName));
			List<com.github.twitch4j.helix.domain.User> usersList = hystrixUsers.execute().getUsers();
			for(com.github.twitch4j.helix.domain.User user : usersList) {
				try {
					User userttv = bAPI.getUser(Provider.TWITCH, user.getId());
					channelEmotes.put(user.getId(), userttv.channelEmotes());
					sharedEmotes.put(user.getId(), userttv.sharedEmotes());
				}catch(UnsupportedOperationException e) {
					LOGGER.warn("Could not find BetterTTV user for the specified ID.");
				}
			}
			if(client != null) {
				hasLoadedClient = true;
			}
		}else {
			LOGGER.error("ClientID, ClientName, and/or ClientSecret in config file is blank/null.\n" +
					"Please check the config file and fill in this information for the mod to work correctly.");
		}*/


		ServerLifecycleEvents.SERVER_STARTED.register((server -> {
			serverRef = server;

			OAuth2Credential validCredential = isTokenValid();

			if(validCredential != null) {
				BlazinBlazeAuth.grabClient(validCredential);
			}else {
				server.getPlayerManager().broadcast(Text.of("§5§l[BTM]§r Server OAuth token has expired / is invalid. Authenticating."), false);
				createNewToken();
			}

			ServerWorld overworld = server.getOverworld();
			MultiplierTick.MultiplierTimer.setMultiplier(overworld.getAttachedOrCreate(BlazinBlazeAttachmentTypes.MULTIPLIER_ATTACHMENT_TYPE, () -> MultiplierAttachmentData.DEFAULT).integer());
			MultiplierTick.MultiplierTimer.setTimer(overworld.getAttachedOrCreate(BlazinBlazeAttachmentTypes.TIMER_ATTACHMENT_TYPE, () -> TimerAttachmentData.DEFAULT).integer());
		}));

		ServerLifecycleEvents.SERVER_STOPPED.register((server -> {
			serverRef = null;
			if(checkTokenRefreshTask != null) {
				checkTokenRefreshTask.cancel(true);
			}
			if(client != null) {
				client.close();
				client = null;
			}
		}));

		ServerPlayerEvents.JOIN.register((player -> {
			if(serverRef != null) {
				if(!isClientLoaded()) {
					if(serverRef.isSingleplayer()) {
						sendFailMessages(player);
					}else {
						if(serverRef.getPlayerManager().isOperator(player.getGameProfile()) || serverRef.isHost(player.getGameProfile())) {
							sendFailMessages(player);
						}else {
							player.sendMessage(Text.of("§5§l[BTM]§r §cA server operator/host has not yet properly configured the connection to their Twitch channel."));
						}
					}
					LOGGER.warn("Connection to Twitch channel has not yet been established.");
				}else {
					sendSuccessMessage();
				}
			}
		}));

		ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
			DynamicRegistryManager registryManager = destination.getRegistryManager();
			if(Objects.equals(origin.getDimensionEntry(), registryManager.getOrThrow(RegistryKeys.DIMENSION_TYPE).getEntry(DimensionTypes.THE_NETHER.getValue()).get())) {
				LootCrateAttachmentData data = player.getAttachedOrCreate(BlazinBlazeAttachmentTypes.LOOT_CRATE_ATTACHMENT_TYPE, () -> LootCrateAttachmentData.DEFAULT);

				int num = data.integer();

				if(num > 0) {
					player.sendMessage(Text.literal(String.valueOf(num)));

					for(int i = 0; i < num; i++) {
						BlazinBlazeTwitchModEntities.LOOT_CRATE_ENTITY.spawn(destination, new BlockPos((int) player.getX(), (int) (player.getY() + 5), (int) player.getZ()), SpawnReason.MOB_SUMMONED);
					}

					player.setAttached(BlazinBlazeAttachmentTypes.LOOT_CRATE_ATTACHMENT_TYPE, data.clear());
				}
			}
		});
	}

	public static void closeTwitchClient() {
		if(client != null) {
			client.close();
			client = null;
		}
	}

	public static void setCredential(OAuth2Credential credential) {
		clientCredential = credential;
	}

	public static boolean updateCredential(OAuth2Credential newCredential) {
		if(clientCredential != null) {
			clientCredential.updateCredential(newCredential);
			if(checkTokenRefreshTask != null) {
				checkTokenRefreshTask.cancel(true);
			}
			checkTokenRefreshTask = scheduler.scheduleAtFixedRate(new CheckTokenRefresh(clientCredential), 0, 1, TimeUnit.HOURS);
			return true;
		}
		return false;
	}

	public static OAuth2Credential refreshToken(TwitchIdentityProvider provider, String token) {
		var refreshToken = config.getClientInfo(BlazinBlazeYAML.refreshToken);
		var refreshTokenExpiry = config.getClientInfo(BlazinBlazeYAML.refreshTokenExpiry);

		if(refreshToken instanceof String reToken) {
			if(refreshTokenExpiry instanceof Long || refreshTokenExpiry instanceof Integer) {
				if(Instant.now().getEpochSecond() < (refreshTokenExpiry instanceof Long ? (long)refreshTokenExpiry : (int)refreshTokenExpiry)) {
					OAuth2Credential credWithRefresh = (!reToken.isEmpty()) ? new OAuth2Credential("twitch", token, reToken, null, null, null, null, null) : null;
					if(credWithRefresh != null) {
						Optional<OAuth2Credential> tempCredential = provider.refreshCredential(credWithRefresh);
						OAuth2Credential newCredential = tempCredential.orElse(null);
						if(newCredential != null) {
							config.setClientInfo(BlazinBlazeYAML.channelToken, newCredential.getAccessToken());
							config.setClientInfo(BlazinBlazeYAML.refreshToken, newCredential.getRefreshToken());
							config.setClientInfo(BlazinBlazeYAML.refreshTokenExpiry, Instant.now().getEpochSecond() + 2592000L);
							config.save();
							return newCredential;
						}
					}
				}
			}
		}

		return null;
	}

	private static OAuth2Credential isTokenValid() {
		if(config != null) {
			var channelToken = config.getClientInfo(BlazinBlazeYAML.channelToken);

			if(channelToken instanceof String token) {
				TwitchIdentityProvider provider = new TwitchIdentityProvider(BlazinBlazeTwitchMod.CLIENT_ID, null, null);
				OAuth2Credential credential = (!token.isEmpty()) ? new OAuth2Credential("twitch", token) : null;
				if(credential != null) {
					Optional<Boolean> isValid = provider.isCredentialValid(credential);
					if(isValid.isPresent() && isValid.get()) {
						return credential;
					}else {
                        return refreshToken(provider, token);
					}
				}
			}
		}

		return null;
	}

	private static boolean isClientLoaded() {
		if(BlazinBlazeTwitchMod.client != null) {
			if(BlazinBlazeTwitchMod.code != null) {
				TwitchIdentityProvider provider = new TwitchIdentityProvider(BlazinBlazeTwitchMod.CLIENT_ID, null, null);
				return provider.getDeviceAccessToken(code).getCredential() != null;
			}else {
				OAuth2Credential validCredential = isTokenValid();
				return validCredential != null;
			}
		}

		return false;
	}

	private static void sendFailMessages(ServerPlayerEntity player) {
		player.sendMessage(Text.of("§5§l[BTM]§r §cA connection to your Twitch channel has not yet been established."));
		if(uri != null) {
			player.sendMessage(Text.literal("§5§l[BTM]§r §cIf you have not authorized the mod yet, please do so by clicking ").append("§c§n§lhere.").setStyle(Style.EMPTY.withClickEvent(new ClickEvent.OpenUrl(URI.create(uri)))));
		}else {
			player.sendMessage(Text.literal("§5§l[BTM]§r §cAuthentication link not available. Try restarting the server or your Minecraft instance."));
		}
	}

	private static void sendSuccessMessage() {
		serverRef.getPlayerManager().broadcast(Text.of("§5§l[BTM]§r §aConnection to Twitch channel established. Have fun!"), false);
	}

	public static void createNewToken() {
		List<String> info = BlazinBlazeAuth.grabOAuth();
		uri = info.getFirst();
		code = info.get(1);

		if(uri != null) {
			Util.getOperatingSystem().open(URI.create(uri));
            if(serverRef != null) {
				serverRef.getPlayerManager().broadcast(Text.empty(), (player) -> {
					if(!serverRef.isSingleplayer()) {
						if(!serverRef.getPlayerManager().isOperator(player.getGameProfile()) && !serverRef.isHost(player.getGameProfile())) {
							return Text.literal("§5§l[BTM]§r §cA server operator or host must authorize the mod to enable Twitch integrations.");
						}
					}
					return Text.literal("§5§l[BTM]§r §cPlease authorize the mod through the opened url or by clicking ").append("§c§n§lhere.").setStyle(Style.EMPTY.withClickEvent(new ClickEvent.OpenUrl(URI.create(uri))));
				}, false);
			}
			LOGGER.warn("IMPORTANT: Please navigate to the opened URL to authorize your Twitch channel. If a link was not opened, please manually authorize here: {}", uri);
		}
	}

	public static void setupTwitchClient(ITwitchClient client, String clientName) {
		BlazinBlazeTwitchMod.client = client;
		BlazinBlazeTwitchMod.client.getChat().joinChannel(clientName);
		BlazinBlazeTwitchMod.client.getClientHelper().enableFollowEventListener(clientName);
		BlazinBlazeTwitchMod.client.getClientHelper().enableStreamEventListener(clientName);

		client.getEventManager().getEventHandler(SimpleEventHandler.class).registerListener(new BlazinBlazeTwitchModEvent());

		bAPI = BttvApiFactory.build();
		globalEmotes = BlazinBlazeTwitchMod.bAPI.getGlobalEmotes();
		HystrixCommand<UserList> hystrixUsers = client.getHelix().getUsers(null, null, List.of(clientName));
		List<com.github.twitch4j.helix.domain.User> usersList = hystrixUsers.execute().getUsers();
		for(com.github.twitch4j.helix.domain.User user : usersList) {
			try {
				User userttv = bAPI.getUser(Provider.TWITCH, user.getId());
				channelEmotes.put(user.getId(), userttv.channelEmotes());
				sharedEmotes.put(user.getId(), userttv.sharedEmotes());
			}catch(UnsupportedOperationException e) {
				LOGGER.warn("Could not find BetterTTV user for the specified ID.");
				if(serverRef != null) {
					serverRef.getPlayerManager().broadcast(Text.empty(), (player) -> {
						if(!serverRef.isSingleplayer()) {
							if(!serverRef.getPlayerManager().isOperator(player.getGameProfile()) && !serverRef.isHost(player.getGameProfile())) {
								return Text.of("§5§l[BTM]§r §cA server operator or host has not yet properly configured BetterTTV emote integration.");
							}
						}
						return Text.of("§5§l[BTM]§r §cBetterTTV emote integration not established. Have you set up a BTTV account?");
					}, false);
				}
			}
		}
		if(serverRef != null) {
			sendSuccessMessage();
		}
        if (checkTokenRefreshTask != null) {
            checkTokenRefreshTask.cancel(true);
        }
        checkTokenRefreshTask = scheduler.scheduleAtFixedRate(new CheckTokenRefresh(clientCredential), 0, 1, TimeUnit.HOURS);
    }
}