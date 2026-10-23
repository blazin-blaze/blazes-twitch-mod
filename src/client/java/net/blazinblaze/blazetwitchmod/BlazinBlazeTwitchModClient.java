package net.blazinblaze.blazetwitchmod;

import net.blazinblaze.blazetwitchmod.entity.BlazinBlazeTwitchModEntities;
import net.blazinblaze.blazetwitchmod.entity.model.BlazinBlazeModelLayers;
import net.blazinblaze.blazetwitchmod.entity.renderer.*;
import net.blazinblaze.blazetwitchmod.networking.*;
import net.blazinblaze.blazetwitchmod.screen.custom.*;
import net.blazinblaze.blazetwitchmod.util.CloseEmoteTimer;
import net.blazinblaze.blazetwitchmod.util.CloseWindowTimer;
import net.blazinblaze.blazetwitchmod.util.EmoteTextures;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.io.File;
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
		EntityRendererRegistry.register(BlazinBlazeTwitchModEntities.LIFE_LINKED_FISH_ENTITY, LifeLinkedFishRenderer::new);

		EntityRendererRegistry.register(BlazinBlazeTwitchModEntities.CHATTER_ENTITY, ChatterEntityRenderer::new);

		CloseEmoteTimer.register();
		CloseWindowTimer.register();
		HudElementRegistry.addFirst(Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "emote-hud"), BlazinBlazeEmoteHUD.getInstance());
		HudElementRegistry.addFirst(Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "multiplier-hud"), BlazinBlazeMultiplierHUD.getInstance());

		MenuScreens.register(BlazinBlazeTwitchMod.MAILBOX_SCREEN_HANDLER, BlazinBlazeMailboxScreen::new);
		MenuScreens.register(BlazinBlazeTwitchMod.RADIO_SCREEN_HANDLER, BlazinBlazeRadioScreen::new);

		ClientPlayNetworking.registerGlobalReceiver(FollowNotifyS2CPayload.ID, (payload, context) -> {
			context.client().getToastManager().addToast(
					SystemToast.multiline(context.client(), SystemToast.SystemToastId.PERIODIC_NOTIFICATION, Component.nullToEmpty("New Follower!"), Component.nullToEmpty(String.format("%s just followed! Blazin!", payload.message())))
			);

			context.client().getNarrator().saySystemChatQueued(Component.nullToEmpty(String.format("[TTS]New Follower! %s just followed! Blazin!", payload.message())));
		});

		ClientPlayNetworking.registerGlobalReceiver(MailNotifyS2CPayload.ID, (payload, context) -> {
			context.client().getToastManager().addToast(
					new TutorialToast(context.client().font, TutorialToast.Icons.BLAZE_TWITCH_MOD_MAIL, Component.nullToEmpty("You've got mail!"), Component.nullToEmpty(String.format("%s just sent you mail!", payload.message())), false, 8000)
			);
		});

		ClientPlayNetworking.registerGlobalReceiver(VoiceMsgNotifyS2CPayload.ID, (payload, context) -> {
			context.client().getToastManager().addToast(
					new TutorialToast(context.client().font, TutorialToast.Icons.BLAZE_TWITCH_MOD_VOICE_MSG, Component.nullToEmpty("New voice message!"), Component.nullToEmpty(String.format("%s just sent you a voice message!", payload.message())), false, 8000)
			);
		});

		ClientPlayNetworking.registerGlobalReceiver(MailScreenS2CPayload.ID, (payload, context) -> {
			context.client().setScreen(
					new BlazinBlazeMailScreen(Component.empty(), payload.message(), payload.mailType(), payload.author(), payload.farewell())
			);
		});

		ClientPlayNetworking.registerGlobalReceiver(EmoteScreenS2CPayload.ID, (payload, context) -> {
			EmoteTextures.enqueueEmote(payload.emoteID(), payload.imageType());
        });

		ClientPlayNetworking.registerGlobalReceiver(PlaySoundS2CPayload.ID, (payload, context) -> {
			context.player().playSound(payload.sound(), payload.volume(), payload.pitch());
		});

		ClientPlayNetworking.registerGlobalReceiver(OverlayMessageS2CPayload.ID, (payload, context) -> {
			Gui hud = context.client().gui;
			hud.setOverlayMessage(Component.literal(payload.message()), true);
		});

		ClientPlayNetworking.registerGlobalReceiver(VoiceMessageS2CPayload.ID, (payload, context) -> {
			context.client().getNarrator().saySystemChatQueued(Component.nullToEmpty(String.format("[TTS]%s says: %s", payload.author(), payload.message())));
		});

		ClientPlayNetworking.registerGlobalReceiver(FullscreenS2CPayload.ID, (payload, context) -> {
			Gui hud = context.client().gui;
			hud.setOverlayMessage(Component.literal(payload.message()), true);
			context.client().getWindow().toggleFullScreen();
			if(!context.client().getWindow().isFullscreen()) {
				context.client().getWindow().setWindowed(100, 100);
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(CloseS2CPayload.ID, (payload, context) -> {
			context.client().player.playSound(SoundEvents.GLASS_BREAK, 1.0F, 1.0F);
			context.client().setScreen(
					new BlazinBlazeCloseScreen(Component.empty(), payload.user())
			);
			CloseWindowTimer.INSTANCE.setTimerAndUser(100L);
		});

		ClientLifecycleEvents.CLIENT_STOPPING.register((client -> {
			BlazinBlazeEmoteHUD.getInstance().closeAllEmotes();
		}));
	}
}