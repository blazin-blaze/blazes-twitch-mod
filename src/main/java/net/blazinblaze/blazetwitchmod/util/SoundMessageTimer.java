package net.blazinblaze.blazetwitchmod.util;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.networking.OverlayMessageS2CPayload;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.potion.Potion;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

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
                for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                    OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§4§lFAKE SOUND EFFECT!");
                    ServerPlayNetworking.send(serverPlayer, payload);
                    serverPlayer.playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_BIT.value(), SoundCategory.NEUTRAL, 1.0F, 1.0F);
                    serverPlayer.sendMessage(Text.literal(String.format("§5§l[BTM]§r §4%s played a sound effect!", currentTimer.user)), false);
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
