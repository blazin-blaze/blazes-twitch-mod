package net.blazinblaze.blazetwitchmod.entity.custom;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class LifeLinkedWolfEntity extends Wolf {
    public LifeLinkedWolfEntity(EntityType<? extends Wolf> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);

        if(this.getOwner() != null) {
            if(this.getOwner() instanceof Player player) {
                if(this.level() instanceof ServerLevel world) {
                    if(!player.isCreative()) {
                        player.kill(world);
                    }
                }
            }
        }
    }
}
