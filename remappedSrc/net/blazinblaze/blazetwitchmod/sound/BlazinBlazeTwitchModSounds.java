package net.blazinblaze.blazetwitchmod.sound;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class BlazinBlazeTwitchModSounds {
    private BlazinBlazeTwitchModSounds() {
        // private empty constructor to avoid accidental instantiation
    }

    // ITEM_METAL_WHISTLE is the name of the custom sound event
    // and is called in the mod to use the custom sound
    public static final SoundEvent WEE_SOUND_EFFECT = registerSound("wee-sound-effect");
    public static final SoundEvent MAIL_ITEM_USE = registerSound("mail-item-use");
    public static final SoundEvent TADA_SOUND_EFFECT = registerSound("tada-sound-effect");
    public static final SoundEvent THUD_SOUND_EFFECT = registerSound("thud-sound-effect");

    // actual registration of all the custom SoundEvents
    private static SoundEvent registerSound(String id) {
        Identifier identifier = Identifier.of(BlazinBlazeTwitchMod.MOD_ID, id);
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
    }

    // This static method starts class initialization, which then initializes
    // the static class variables (e.g. ITEM_METAL_WHISTLE).
    public static void initialize() {
        BlazinBlazeTwitchMod.LOGGER.info("Registering " + BlazinBlazeTwitchMod.MOD_ID + " Sounds");
        // Technically this method can stay empty, but some developers like to notify
        // the console, that certain parts of the mod have been successfully initialized
    }
}
