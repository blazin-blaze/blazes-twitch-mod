package net.blazinblaze.blazetwitchmod.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WolfEntity.class)
public interface WolfEntityAccess {
    @Invoker void callSetCollarColor(DyeColor color);
}
