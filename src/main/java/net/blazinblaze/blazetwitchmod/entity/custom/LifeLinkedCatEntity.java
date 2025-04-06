package net.blazinblaze.blazetwitchmod.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class LifeLinkedCatEntity extends CatEntity {
    public LifeLinkedCatEntity(EntityType<? extends CatEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);

        if(this.getOwner() != null) {
            if(this.getOwner() instanceof PlayerEntity player) {
                if(this.getWorld() instanceof ServerWorld world) {
                    if(!player.isCreative()) {
                        player.kill(world);
                    }
                }
            }
        }
    }
}
