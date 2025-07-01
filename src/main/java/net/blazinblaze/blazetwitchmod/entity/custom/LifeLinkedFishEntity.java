package net.blazinblaze.blazetwitchmod.entity.custom;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.SalmonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class LifeLinkedFishEntity extends SalmonEntity {

    private static final TrackedData<String> OWNER_UUID = DataTracker.registerData(LifeLinkedFishEntity.class, TrackedDataHandlerRegistry.STRING);

    public LifeLinkedFishEntity(EntityType<? extends SalmonEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        ServerPlayerEntity player = BlazinBlazeTwitchMod.serverRef.getPlayerManager().getPlayer(UUID.fromString(getOwnerUUID()));
        if(player != null && !player.isDisconnected()) {
            if(this.getWorld() instanceof ServerWorld world) {
                if(!player.isCreative()) {
                    player.kill(world);
                }
            }
        }
    }

    @Override
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        view.putString("ownerUUID", getOwnerUUID());
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(OWNER_UUID, "");
    }

    @Override
    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
        setOwnerUUID(view.getString("ownerUUID", ""));
    }

    public String getOwnerUUID() {
        return this.dataTracker.get(OWNER_UUID);
    }

    public void setOwnerUUID(String uuid) {
        this.dataTracker.set(OWNER_UUID, uuid);
    }
}
