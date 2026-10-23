package net.blazinblaze.blazetwitchmod;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import Type;
import java.util.HashMap;
import java.util.UUID;

public class BlazinBlazeStateSaverAndLoader extends PersistentState {

    //public Integer totalNetherLootCrates = 0;

    public HashMap<UUID, BlazinBlazeLootPlayerData> players = new HashMap<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        //data.putInt("totalNetherLootCrates", totalNetherLootCrates);

        NbtCompound playersNbt = new NbtCompound();
        players.forEach((uuid, playerData) -> {
            NbtCompound playerNbt = new NbtCompound();

            playerNbt.putInt("netherLootCrates", playerData.netherLootCrates);

            playersNbt.put(uuid.toString(), playerNbt);
        });
        nbt.put("players", playersNbt);

        return nbt;
    }

    public static BlazinBlazeStateSaverAndLoader createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        BlazinBlazeStateSaverAndLoader state = new BlazinBlazeStateSaverAndLoader();
        //state.totalDirtBlocksBroken = tag.getInt("totalDirtBlocksBroken");

        NbtCompound playersNbt = tag.getCompound("players");
        playersNbt.getKeys().forEach(key -> {
            BlazinBlazeLootPlayerData playerData = new BlazinBlazeLootPlayerData();

            playerData.netherLootCrates = playersNbt.getCompound(key).getInt("dirtBlocksBroken");

            UUID uuid = UUID.fromString(key);
            state.players.put(uuid, playerData);
        });

        return state;
    }

    public static BlazinBlazeStateSaverAndLoader createNew() {
        BlazinBlazeStateSaverAndLoader state = new BlazinBlazeStateSaverAndLoader();
        //state.totalDirtBlocksBroken = 0;
        state.players = new HashMap<>();
        return state;
    }

    private static final Type<BlazinBlazeStateSaverAndLoader> type = new Type<>(
            BlazinBlazeStateSaverAndLoader::createNew,
            BlazinBlazeStateSaverAndLoader::createFromNbt,
            null
    );

    public static BlazinBlazeStateSaverAndLoader getServerState(MinecraftServer server) {
        PersistentStateManager persistentStateManager = server.getWorld(World.NETHER).getPersistentStateManager();

        BlazinBlazeStateSaverAndLoader state = persistentStateManager.getOrCreate(type, BlazinBlazeTwitchMod.MOD_ID);

        state.markDirty();

        return state;
    }

    public static BlazinBlazeLootPlayerData getPlayerState(LivingEntity player) {
        BlazinBlazeStateSaverAndLoader serverState = getServerState(player.getWorld().getServer());

        BlazinBlazeLootPlayerData playerState = serverState.players.computeIfAbsent(player.getUuid(), uuid -> new BlazinBlazeLootPlayerData());

        return playerState;
    }
}
