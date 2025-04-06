package net.blazinblaze.blazetwitchmod.mixin;

import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CatEntity.class)
public interface CatEntityAccess {
    @Invoker void callSetCollarColor(DyeColor color);
    @Invoker void callSetVariant(RegistryEntry<CatVariant> variant);
}
