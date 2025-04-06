package net.blazinblaze.blazetwitchmod.mixin;

import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ParrotEntity.class)
public interface ParrotEntityAccess {
    @Invoker void callSetVariant(ParrotEntity.Variant variant);
}
