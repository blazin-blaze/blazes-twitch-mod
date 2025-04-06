package net.blazinblaze.blazetwitchmod;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import net.blazinblaze.blazetwitchmod.block.BlazinBlazeTwitchModBlocks;
import net.blazinblaze.blazetwitchmod.effect.RandomTeleportEffect;
import net.blazinblaze.blazetwitchmod.entity.BlazinBlazeTwitchModEntities;
import net.blazinblaze.blazetwitchmod.entity.custom.MegaCreeperEntity;
import net.blazinblaze.blazetwitchmod.item.BlazinBlazeTwitchModItems;
import net.blazinblaze.blazetwitchmod.networking.*;
import net.blazinblaze.blazetwitchmod.sound.BlazinBlazeTwitchModSounds;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
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

import java.util.Objects;

public class BlazinBlazeTwitchMod implements ModInitializer {
	public static final String MOD_ID = "blaze-twitch-mod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final RegistryEntry<StatusEffect> RANDOM_TELEPORT =
			Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MOD_ID, "random_teleport"), new RandomTeleportEffect());

	private TwitchClient client;

	public static MinecraftServer serverRef;

	@Override
	public void onInitialize() {
		BlazinBlazeTwitchModEntities.registerModEntites();
		BlazinBlazeTwitchModEntities.registerAttributes();

		FabricDefaultAttributeRegistry.register(BlazinBlazeTwitchModEntities.MEGA_CREEPER, MegaCreeperEntity.createCreeperAttributes());

		BlazinBlazeTwitchModItems.initialize();
		BlazinBlazeTwitchModBlocks.initialize();
		BlazinBlazeTwitchModSounds.initialize();

		// Build credential when possible (old OAuth)
		String token = "n1vfxildaa0dht01nytkitkoves27h";
		OAuth2Credential credential = StringUtils.isNotBlank(token) ? new OAuth2Credential("twitch", token) : null;

		// Build TwitchClient
		client = TwitchClientBuilder.builder()
				.withClientId("nldmdgulpwmasgoozi1i2t5vt9vn0r")
				.withClientSecret("wh0kdntms4cgi4rgxtpquhgnoesj01")
				.withEnableChat(true)
				.withChatAccount(credential)
				.withEnableHelix(true)
				.withDefaultAuthToken(credential)
				.withChatCommandsViaHelix(false)
				.build();

		client.getChat().joinChannel("blazinblaze_");
		client.getClientHelper().enableFollowEventListener("blazinblaze_");
		client.getClientHelper().enableStreamEventListener("blazinblaze_");

		// Register event listeners
		client.getEventManager().getEventHandler(SimpleEventHandler.class).registerListener(new BlazinBlazeTwitchModEvent());

		PayloadTypeRegistry.playS2C().register(MailScreenS2CPayload.ID, MailScreenS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(FollowNotifyS2CPayload.ID, FollowNotifyS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(PlaySoundS2CPayload.ID, PlaySoundS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(OverlayMessageS2CPayload.ID, OverlayMessageS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(VoiceMessageS2CPayload.ID, VoiceMessageS2CPayload.CODEC);

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