package net.blazinblaze.blazetwitchmod.util;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.networking.OverlayMessageS2CPayload;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

import java.util.ArrayList;

public class TeleportTimer implements ServerTickEvents.EndTick {
    public static final TeleportTimer INSTANCE = new TeleportTimer();

    private static final ArrayList<Location> locations = new ArrayList<Location>();

    public void setLocation(ServerWorld world, int x, int z) {
        locations.add(new Location(world, x, z));
    }

    @Override
    public void onEndTick(MinecraftServer server) {
        for (var iter = locations.listIterator(); iter.hasNext(); ) {
            Location location = iter.next();
            ServerWorld world = location.world;
            if(world.isPosLoaded(new BlockPos(location.x, world.getTopY(Heightmap.Type.WORLD_SURFACE, location.x, location.z), location.z))) {
                for (ServerPlayerEntity serverPlayer : PlayerLookup.all(server)) {
                    var instance = new StatusEffectInstance(BlazinBlazeTwitchMod.RANDOM_TELEPORT, 8*20, 0, false, true, false);
                    serverPlayer.addStatusEffect(instance);
                }
                iter.remove();
            }
        }
    }
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(INSTANCE);
    }

    record Location(ServerWorld world, int x, int z) {}
}