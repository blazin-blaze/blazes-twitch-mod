package net.blazinblaze.blazetwitchmod.util;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.networking.OverlayMessageS2CPayload;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.ArrayList;

public class TeleportTimer implements ServerTickEvents.EndTick {
    public static final TeleportTimer INSTANCE = new TeleportTimer();

    private static final ArrayList<Location> locations = new ArrayList<Location>();

    public void setLocation(ServerLevel world, int x, int z) {
        locations.add(new Location(world, x, z));
    }

    @Override
    public void onEndTick(MinecraftServer server) {
        for (var iter = locations.listIterator(); iter.hasNext(); ) {
            Location location = iter.next();
            ServerLevel world = location.world;
            if(world.isLoaded(new BlockPos(location.x, world.getHeight(Heightmap.Types.WORLD_SURFACE, location.x, location.z), location.z))) {
                for (ServerPlayer serverPlayer : PlayerLookup.all(server)) {
                    var instance = new MobEffectInstance(BlazinBlazeTwitchMod.RANDOM_TELEPORT, 8*20, 0, false, true, false);
                    serverPlayer.addEffect(instance);
                }
                iter.remove();
            }
        }
    }
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(INSTANCE);
    }

    record Location(ServerLevel world, int x, int z) {}
}