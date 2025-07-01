package net.blazinblaze.blazetwitchmod;

import com.mojang.text2speech.Narrator;
import com.mojang.text2speech.NarratorWindows;
import net.blazinblaze.blazetwitchmod.entity.BlazinBlazeTwitchModEntities;
import net.blazinblaze.blazetwitchmod.entity.custom.LifeLinkedCatEntity;
import net.blazinblaze.blazetwitchmod.entity.custom.LifeLinkedParrotEntity;
import net.blazinblaze.blazetwitchmod.entity.model.BlazinBlazeModelLayers;
import net.blazinblaze.blazetwitchmod.entity.renderer.*;
import net.blazinblaze.blazetwitchmod.mixin.client.NarrateTTSMixin;
import net.blazinblaze.blazetwitchmod.networking.*;
import net.blazinblaze.blazetwitchmod.screen.custom.BlazinBlazeEmoteHUD;
import net.blazinblaze.blazetwitchmod.screen.custom.BlazinBlazeEmoteScreen;
import net.blazinblaze.blazetwitchmod.screen.custom.BlazinBlazeMailScreen;
import net.blazinblaze.blazetwitchmod.util.CloseEmoteTimer;
import net.blazinblaze.blazetwitchmod.util.EmoteTextures;
import net.blazinblaze.blazetwitchmod.util.GIFTick;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.narration.Narration;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.NarratorMode;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BlazinBlazeTwitchModClient implements ClientModInitializer {

	public static ConcurrentMap<String, File> cachedEmoteImages = new ConcurrentHashMap<>();

	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(BlazinBlazeTwitchModEntities.MEGA_CREEPER, MegaCreeperRenderer::new);

		EntityRendererRegistry.register(BlazinBlazeTwitchModEntities.NUCLEAR_TNT_ENTITY, NuclearTNTRenderer::new);

		BlazinBlazeModelLayers.registerModelLayers();
		EntityRendererRegistry.register(BlazinBlazeTwitchModEntities.LOOT_CRATE_ENTITY, LootCrateRenderer::new);
		EntityRendererRegistry.register(BlazinBlazeTwitchModEntities.BEDDY_ENTITY, BeddyEntityRenderer::new);

		EntityRendererRegistry.register(BlazinBlazeTwitchModEntities.LIFE_LINKED_CAT_ENTITY, LifeLinkedCatRenderer::new);
		EntityRendererRegistry.register(BlazinBlazeTwitchModEntities.LIFE_LINKED_WOLF_ENTITY, LifeLinkedWolfRenderer::new);
		EntityRendererRegistry.register(BlazinBlazeTwitchModEntities.LIFE_LINKED_PARROT_ENTITY, LifeLinkedParrotRenderer::new);

		//GIFTick.register();
		CloseEmoteTimer.register();
		HudElementRegistry.addFirst(Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "emote-hud"), BlazinBlazeEmoteHUD.getInstance());

		ClientPlayNetworking.registerGlobalReceiver(FollowNotifyS2CPayload.ID, (payload, context) -> {
			context.client().getToastManager().add(
					SystemToast.create(context.client(), SystemToast.Type.PERIODIC_NOTIFICATION, Text.of("New Follower!"), Text.of(String.format(" %s just followed! Blazin!", payload.message())))
			);

			context.client().getNarratorManager().narrate(Text.of(String.format("[TTS]New Follower! %s just followed! Blazin!", payload.message())));
			//context.client().getNarratorManager().narrate();
		});

		ClientPlayNetworking.registerGlobalReceiver(MailScreenS2CPayload.ID, (payload, context) -> {
			context.client().setScreen(
					new BlazinBlazeMailScreen(Text.empty(), payload.message(), payload.type(), payload.author(), payload.farewell())
			);
		});

		ClientPlayNetworking.registerGlobalReceiver(EmoteScreenS2CPayload.ID, (payload, context) -> {
			EmoteTextures.enqueueEmote(payload.emoteID(), payload.imageType());
        });

		ClientPlayNetworking.registerGlobalReceiver(PlaySoundS2CPayload.ID, (payload, context) -> {
			context.player().playSoundToPlayer(payload.sound(), SoundCategory.AMBIENT, payload.volume(), payload.pitch());
		});

		ClientPlayNetworking.registerGlobalReceiver(OverlayMessageS2CPayload.ID, (payload, context) -> {
			InGameHud hud = context.client().inGameHud;
			hud.setOverlayMessage(Text.literal(payload.message()), true);
		});

		ClientPlayNetworking.registerGlobalReceiver(VoiceMessageS2CPayload.ID, (payload, context) -> {
			context.client().getNarratorManager().narrate(Text.of(String.format("[TTS]%s says: %s", payload.author(), payload.message())));
			//context.client().getNarratorManager().narrate(String.format("%s says: %s", payload.author(), payload.message()));
		});


		ClientLifecycleEvents.CLIENT_STOPPING.register((client -> {
			BlazinBlazeEmoteHUD.getInstance().closeAllEmotes();
		}));
	}
}