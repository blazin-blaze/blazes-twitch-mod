package net.blazinblaze.blazetwitchmod.entity.custom;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.animal.fish.Salmon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class LifeLinkedFishEntity extends Salmon {

    private static final EntityDataAccessor<String> OWNER_UUID = SynchedEntityData.defineId(LifeLinkedFishEntity.class, EntityDataSerializers.STRING);

    public LifeLinkedFishEntity(EntityType<? extends Salmon> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void die(DamageSource damageSource) {
        ServerPlayer player = BlazinBlazeTwitchMod.serverRef.getPlayerList().getPlayer(UUID.fromString(getOwnerUUID()));
        if(player != null && !player.hasDisconnected()) {
            if(this.level() instanceof ServerLevel world) {
                if(!player.isCreative()) {
                    player.kill(world);
                }
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput view) {
        super.addAdditionalSaveData(view);
        view.putString("ownerUUID", getOwnerUUID());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(OWNER_UUID, "");
    }

    @Override
    protected void readAdditionalSaveData(ValueInput view) {
        super.readAdditionalSaveData(view);
        setOwnerUUID(view.getStringOr("ownerUUID", ""));
    }

    public String getOwnerUUID() {
        return this.entityData.get(OWNER_UUID);
    }

    public void setOwnerUUID(String uuid) {
        this.entityData.set(OWNER_UUID, uuid);
    }
}
