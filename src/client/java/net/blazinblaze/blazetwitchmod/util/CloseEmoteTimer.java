package net.blazinblaze.blazetwitchmod.util;

import net.blazinblaze.blazetwitchmod.networking.OverlayMessageS2CPayload;
import net.blazinblaze.blazetwitchmod.screen.custom.BlazinBlazeEmoteHUD;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

public class CloseEmoteTimer implements ClientTickEvents.EndTick {
    public static final CloseEmoteTimer INSTANCE = new CloseEmoteTimer();

    private static final ArrayList<CloseEmoteTimer.Timer> ongoingTimers = new ArrayList<CloseEmoteTimer.Timer>();
    private static final BlazinBlazeEmoteHUD hudInstance = BlazinBlazeEmoteHUD.getInstance();

    public void setCloseTimer(long ticksUntilSomething, String id, BlazeImage texture) {
        ongoingTimers.add(new CloseEmoteTimer.Timer(ticksUntilSomething, id, texture));
    }

    @Override
    public void onEndTick(Minecraft client) {
        for (var iter = ongoingTimers.listIterator(); iter.hasNext(); ) {
            CloseEmoteTimer.Timer currentTimer = iter.next();
            if ((currentTimer.ticksUntilSomething - 1) <= 0L) {
                hudInstance.closeEmote(currentTimer.id, currentTimer.texture);
                iter.remove();
            }else {
                iter.set(new CloseEmoteTimer.Timer(currentTimer.ticksUntilSomething - 1, currentTimer.id, currentTimer.texture));
            }
        }
    }

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(INSTANCE);
    }

    record Timer(Long ticksUntilSomething, String id, BlazeImage texture) {}
}
