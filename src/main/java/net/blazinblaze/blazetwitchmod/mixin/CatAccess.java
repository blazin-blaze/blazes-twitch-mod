package net.blazinblaze.blazetwitchmod.mixin;

import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.feline.CatVariant;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.core.Holder;
import net.minecraft.world.item.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Cat.class)
public interface CatAccess {
    @Invoker void callSetCollarColor(DyeColor color);
    @Invoker void callSetVariant(Holder<CatVariant> variant);
}
