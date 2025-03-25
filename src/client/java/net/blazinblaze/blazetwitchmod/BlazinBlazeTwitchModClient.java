package net.blazinblaze.blazetwitchmod;

import net.blazinblaze.blazetwitchmod.entity.BlazinBlazeTwitchModEntities;
import net.blazinblaze.blazetwitchmod.entity.model.BlazinBlazeModelLayers;
import net.blazinblaze.blazetwitchmod.entity.renderer.BeddyEntityRenderer;
import net.blazinblaze.blazetwitchmod.entity.renderer.LootCrateRenderer;
import net.blazinblaze.blazetwitchmod.entity.renderer.MegaCreeperRenderer;
import net.blazinblaze.blazetwitchmod.entity.renderer.NuclearTNTRenderer;
import net.blazinblaze.blazetwitchmod.networking.FollowNotifyS2CPayload;
import net.blazinblaze.blazetwitchmod.networking.MailScreenS2CPayload;
import net.blazinblaze.blazetwitchmod.networking.OverlayMessageS2CPayload;
import net.blazinblaze.blazetwitchmod.networking.PlaySoundS2CPayload;
import net.blazinblaze.blazetwitchmod.screen.custom.BlazinBlazeMailScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;

public class BlazinBlazeTwitchModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(BlazinBlazeTwitchModEntities.MEGA_CREEPER, MegaCreeperRenderer::new);

		EntityRendererRegistry.register(BlazinBlazeTwitchModEntities.NUCLEAR_TNT_ENTITY, NuclearTNTRenderer::new);

		BlazinBlazeModelLayers.registerModelLayers();
		EntityRendererRegistry.register(BlazinBlazeTwitchModEntities.LOOT_CRATE_ENTITY, LootCrateRenderer::new);
		EntityRendererRegistry.register(BlazinBlazeTwitchModEntities.BEDDY_ENTITY, BeddyEntityRenderer::new);

		ClientPlayNetworking.registerGlobalReceiver(FollowNotifyS2CPayload.ID, (payload, context) -> {
			context.client().getToastManager().add(
					SystemToast.create(context.client(), SystemToast.Type.PERIODIC_NOTIFICATION, Text.of("New Follower!"), Text.of(String.format(" %s just followed! Blazin!", payload.message())))
			);
		});

		ClientPlayNetworking.registerGlobalReceiver(MailScreenS2CPayload.ID, (payload, context) -> {
			context.client().setScreen(
					new BlazinBlazeMailScreen(Text.empty(), payload.message(), payload.type(), payload.author(), payload.farewell())
			);
		});

		ClientPlayNetworking.registerGlobalReceiver(PlaySoundS2CPayload.ID, (payload, context) -> {
			context.player().playSoundToPlayer(payload.sound(), SoundCategory.AMBIENT, payload.volume(), payload.pitch());
		});

		ClientPlayNetworking.registerGlobalReceiver(OverlayMessageS2CPayload.ID, (payload, context) -> {
			InGameHud hud = context.client().inGameHud;
			hud.setOverlayMessage(Text.literal(payload.message()), true);
		});
	}
}