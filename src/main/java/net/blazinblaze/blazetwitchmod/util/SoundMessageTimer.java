package net.blazinblaze.blazetwitchmod.util;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.networking.OverlayMessageS2CPayload;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;

public class SoundMessageTimer implements ServerTickEvents.EndTick {
    public static final SoundMessageTimer INSTANCE = new SoundMessageTimer();

    private static final ArrayList<Timer> ongoingTimers = new ArrayList<Timer>();

    public void setTimerAndUser(long ticksUntilSomething, String user) {
        ongoingTimers.add(new Timer(ticksUntilSomething, user));
    }

    @Override
    public void onEndTick(MinecraftServer server) {
        for (var iter = ongoingTimers.listIterator(); iter.hasNext(); ) {
            Timer currentTimer = iter.next();
            if ((currentTimer.ticksUntilSomething - 1) <= 0L) {
                for (ServerPlayer serverPlayer : PlayerLookup.all(server)) {
                    OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§4§lFAKE SOUND EFFECT!");
                    ServerPlayNetworking.send(serverPlayer, payload);
                    serverPlayer.playSound(SoundEvents.NOTE_BLOCK_BIT.value(), 1.0F, 1.0F);
                    serverPlayer.sendSystemMessage(Component.literal(String.format("§5§l[BTM]§r §4%s played a sound effect!", currentTimer.user)), false);
                }
                iter.remove();
            }else {
                iter.set(new Timer(currentTimer.ticksUntilSomething - 1, currentTimer.user));
            }
        }
    }
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(INSTANCE);
    }

    record Timer(Long ticksUntilSomething, String user) {}
}
