package net.blazinblaze.blazetwitchmod.util;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.data.BlazinBlazeAttachmentTypes;
import net.blazinblaze.blazetwitchmod.data.MultiplierAttachmentData;
import net.blazinblaze.blazetwitchmod.data.TimerAttachmentData;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.apache.commons.lang.mutable.MutableInt;

public class MultiplierTick implements ServerTickEvents.EndTick {

    public static final MultiplierTick INSTANCE = new MultiplierTick();

    @Override
    public void onEndTick(MinecraftServer server) {
        if(MultiplierTimer.getTimer() == 20*600) {
            MultiplierTimer.incrementMultipler();
            MultiplierTimer.resetTimer();
            for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                serverPlayer.sendMessage(Text.literal(String.format("§5§l[BTM]§r §a§lMultipler just INCREASED to x%s!", MultiplierTimer.getMultiplier())));
            }
        }else {
            MultiplierTimer.incrementTimer();
        }
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(INSTANCE);
    }

    public static class MultiplierTimer {

        private static final MutableInt multiplier = new MutableInt(1);
        private static final MutableInt timer = new MutableInt();

        public static int getTimer() {
            return timer.intValue();
        }

        public static void incrementTimer() {
            timer.increment();
            MinecraftServer server = BlazinBlazeTwitchMod.serverRef;
            if(server != null) {
                ServerWorld overworld = server.getOverworld();
                TimerAttachmentData data = overworld.getAttachedOrCreate(BlazinBlazeAttachmentTypes.TIMER_ATTACHMENT_TYPE, () -> TimerAttachmentData.DEFAULT);
                overworld.setAttached(BlazinBlazeAttachmentTypes.TIMER_ATTACHMENT_TYPE, data.addInt(1));
            }
        }

        public static void resetTimer() {
            timer.setValue(0);
            MinecraftServer server = BlazinBlazeTwitchMod.serverRef;
            if(server != null) {
                ServerWorld overworld = server.getOverworld();
                TimerAttachmentData data = overworld.getAttachedOrCreate(BlazinBlazeAttachmentTypes.TIMER_ATTACHMENT_TYPE, () -> TimerAttachmentData.DEFAULT);
                overworld.setAttached(BlazinBlazeAttachmentTypes.TIMER_ATTACHMENT_TYPE, data.clear());
            }
        }

        public static int getMultiplier() {
            return multiplier.intValue();
        }

        public static void incrementMultipler() {
            multiplier.increment();
            MinecraftServer server = BlazinBlazeTwitchMod.serverRef;
            if(server != null) {
                ServerWorld overworld = server.getOverworld();
                MultiplierAttachmentData data = overworld.getAttachedOrCreate(BlazinBlazeAttachmentTypes.MULTIPLIER_ATTACHMENT_TYPE, () -> MultiplierAttachmentData.DEFAULT);
                overworld.setAttached(BlazinBlazeAttachmentTypes.MULTIPLIER_ATTACHMENT_TYPE, data.addInt(1));
            }
        }

        public static void setTimer(int value) {
            timer.setValue(value);
        }

        public static void setMultiplier(int value) {
            timer.setValue(value);
        }

    }
}
