package net.blazinblaze.blazetwitchmod;

import com.chocohead.mm.api.ClassTinkerers;
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
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

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
		HudElementRegistry.addFirst(Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "emote-hud"), BlazinBlazeEmoteHUD.getInstance());
		HudElementRegistry.addFirst(Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "multiplier-hud"), BlazinBlazeMultiplierHUD.getInstance());

		HandledScreens.register(BlazinBlazeTwitchMod.MAILBOX_SCREEN_HANDLER, BlazinBlazeMailboxScreen::new);
		HandledScreens.register(BlazinBlazeTwitchMod.RADIO_SCREEN_HANDLER, BlazinBlazeRadioScreen::new);

		ClientPlayNetworking.registerGlobalReceiver(FollowNotifyS2CPayload.ID, (payload, context) -> {
			context.client().getToastManager().add(
					SystemToast.create(context.client(), SystemToast.Type.PERIODIC_NOTIFICATION, Text.of("New Follower!"), Text.of(String.format("%s just followed! Blazin!", payload.message())))
			);

			context.client().getNarratorManager().narrate(Text.of(String.format("[TTS]New Follower! %s just followed! Blazin!", payload.message())));
			//context.client().getNarratorManager().narrate();
		});

		ClientPlayNetworking.registerGlobalReceiver(MailNotifyS2CPayload.ID, (payload, context) -> {
			context.client().getToastManager().add(
					new TutorialToast(context.client().textRenderer, ClassTinkerers.getEnum(TutorialToast.Type.class, "MAIL"), Text.of("You've got mail!"), Text.of(String.format("%s just sent you mail!", payload.message())), false, 8000)
			);
			//context.client().getNarratorManager().narrate();
		});

		ClientPlayNetworking.registerGlobalReceiver(VoiceMsgNotifyS2CPayload.ID, (payload, context) -> {
			context.client().getToastManager().add(
					new TutorialToast(context.client().textRenderer, ClassTinkerers.getEnum(TutorialToast.Type.class, "VOICE_MSG"), Text.of("New voice message!"), Text.of(String.format("%s just sent you a voice message!", payload.message())), false, 8000)
			);
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

		ClientPlayNetworking.registerGlobalReceiver(FullscreenS2CPayload.ID, (payload, context) -> {
			InGameHud hud = context.client().inGameHud;
			hud.setOverlayMessage(Text.literal(payload.message()), true);
			context.client().getWindow().toggleFullscreen();
			if(!context.client().getWindow().isFullscreen()) {
				context.client().getWindow().setWindowedSize(100, 100);
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(CloseS2CPayload.ID, (payload, context) -> {
			context.client().player.playSoundToPlayer(SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.NEUTRAL, 1.0F, 1.0F);
			context.client().setScreen(
					new BlazinBlazeCloseScreen(Text.empty(), payload.user())
			);
			CloseWindowTimer.INSTANCE.setTimerAndUser(100L);
		});

		ClientLifecycleEvents.CLIENT_STOPPING.register((client -> {
			BlazinBlazeEmoteHUD.getInstance().closeAllEmotes();
		}));
	}
}