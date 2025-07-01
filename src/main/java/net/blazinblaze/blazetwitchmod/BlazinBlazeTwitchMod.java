package net.blazinblaze.blazetwitchmod;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.helix.domain.UserList;
import com.netflix.hystrix.HystrixCommand;
import feign.FeignException;
import io.github.iprodigy.bttv.api.BetterTTV;
import io.github.iprodigy.bttv.api.BttvApiFactory;
import io.github.iprodigy.bttv.api.domain.User;
import io.github.iprodigy.bttv.common.Emote;
import io.github.iprodigy.bttv.common.Provider;
import net.blazinblaze.blazetwitchmod.block.BlazinBlazeTwitchModBlocks;
import net.blazinblaze.blazetwitchmod.config.BlazinBlazeConfig;
import net.blazinblaze.blazetwitchmod.effect.RandomTeleportEffect;
import net.blazinblaze.blazetwitchmod.entity.BlazinBlazeTwitchModEntities;
import net.blazinblaze.blazetwitchmod.entity.custom.MegaCreeperEntity;
import net.blazinblaze.blazetwitchmod.item.BlazinBlazeTwitchModItems;
import net.blazinblaze.blazetwitchmod.networking.*;
import net.blazinblaze.blazetwitchmod.sound.BlazinBlazeTwitchModSounds;
import net.blazinblaze.blazetwitchmod.util.SoundMessageTimer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionTypes;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public class BlazinBlazeTwitchMod implements ModInitializer {
	public static final String MOD_ID = "blaze-twitch-mod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final RegistryEntry<StatusEffect> RANDOM_TELEPORT =
			Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MOD_ID, "random_teleport"), new RandomTeleportEffect());

	private TwitchClient client;

	public static MinecraftServer serverRef;
	public static BetterTTV bAPI;
	public static Collection<io.github.iprodigy.bttv.common.Emote> globalEmotes;
	public static Map<User, List<Emote>> channelEmotes = new HashMap<>();
	public static Map<User, List<Emote>> sharedEmotes = new HashMap<>();

	@Override
	public void onInitialize() {

		BlazinBlazeTwitchModEntities.registerModEntites();
		BlazinBlazeTwitchModEntities.registerAttributes();

		FabricDefaultAttributeRegistry.register(BlazinBlazeTwitchModEntities.MEGA_CREEPER, MegaCreeperEntity.createCreeperAttributes());

		BlazinBlazeTwitchModItems.initialize();
		BlazinBlazeTwitchModBlocks.initialize();
		BlazinBlazeTwitchModSounds.initialize();

		File configFile = new File(FabricLoader.getInstance().getConfigDir().resolve("btm-config.json").toString());
		BlazinBlazeConfig config = BlazinBlazeConfig.loadConfigFile(configFile);

		if((config.clientName != null && !config.clientName.isBlank()) &&
				(config.clientID != null && !config.clientID.isBlank()) &&
				(config.clientSecret != null && !config.clientSecret.isBlank()) &&
				(config.channelToken != null && !config.channelToken.isBlank())) {
			// Build credential when possible (old OAuth)
			String token = config.channelToken;
			OAuth2Credential credential = StringUtils.isNotBlank(token) ? new OAuth2Credential("twitch", token) : null;

			// Build TwitchClient
			client = TwitchClientBuilder.builder()
					.withClientId(config.clientID)
					.withClientSecret(config.clientSecret)
					.withEnableChat(true)
					.withChatAccount(credential)
					.withEnableHelix(true)
					.withDefaultAuthToken(credential)
					.withChatCommandsViaHelix(false)
					.build();

			client.getChat().joinChannel(config.clientName);
			client.getClientHelper().enableFollowEventListener(config.clientName);
			client.getClientHelper().enableStreamEventListener(config.clientName);

			// Register event listeners
			client.getEventManager().getEventHandler(SimpleEventHandler.class).registerListener(new BlazinBlazeTwitchModEvent());

			bAPI = BttvApiFactory.build();
			globalEmotes = BlazinBlazeTwitchMod.bAPI.getGlobalEmotes();
			HystrixCommand<UserList> hystrixUsers = client.getHelix().getUsers(null, null, List.of(config.clientName));
			List<com.github.twitch4j.helix.domain.User> usersList = hystrixUsers.execute().getUsers();
			for(com.github.twitch4j.helix.domain.User user : usersList) {
				try {
					User userttv = bAPI.getUser(Provider.TWITCH, user.getId());
					channelEmotes.put(userttv, userttv.channelEmotes());
					sharedEmotes.put(userttv, userttv.sharedEmotes());
				}catch(UnsupportedOperationException e) {
					LOGGER.warn("Could not find BetterTTV user for the specified ID.");
				}
			}
		}else {
			LOGGER.error("ClientID, ClientName, and/or ClientSecret in config file is blank/null.\n" +
					"Please check the config file and fill in this information for the mod to work correctly.");
		}

		PayloadTypeRegistry.playS2C().register(MailScreenS2CPayload.ID, MailScreenS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(FollowNotifyS2CPayload.ID, FollowNotifyS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(PlaySoundS2CPayload.ID, PlaySoundS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(OverlayMessageS2CPayload.ID, OverlayMessageS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(VoiceMessageS2CPayload.ID, VoiceMessageS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(EmoteScreenS2CPayload.ID, EmoteScreenS2CPayload.CODEC);

		SoundMessageTimer.register();

		ServerLifecycleEvents.SERVER_STARTED.register((server -> {
			serverRef = server;
		}));

		ServerLifecycleEvents.SERVER_STOPPED.register((server -> {
			serverRef = null;
		}));

		//ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
			//player.sendMessage(Text.literal("EVENT TRIGGERED"));
			//DynamicRegistryManager registryManager = destination.getRegistryManager();
			//if(Objects.equals(origin.getDimensionEntry(), registryManager.getOrThrow(RegistryKeys.DIMENSION_TYPE).getEntry(DimensionTypes.THE_NETHER.getValue()).get())) {
				//BlazinBlazeLootPlayerData playerState = BlazinBlazeStateSaverAndLoader.getPlayerState(player);

				//player.sendMessage(Text.literal(String.valueOf(playerState.netherLootCrates)));

				//for(int i = 0; i < playerState.netherLootCrates; i++) {
					//BlazinBlazeTwitchModEntities.LOOT_CRATE_ENTITY.spawn(destination, new BlockPos((int) player.getX(), (int) (player.getY() + 5), (int) player.getZ()), SpawnReason.MOB_SUMMONED);
				//}

				//playerState.netherLootCrates = 0;

				//PacketByteBuf data = PacketByteBufs.create();
				//data.writeInt(playerState.netherLootCrates);
			//}
		//});
	}
}