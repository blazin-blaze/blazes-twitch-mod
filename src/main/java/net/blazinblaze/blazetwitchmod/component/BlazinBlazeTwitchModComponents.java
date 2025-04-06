package net.blazinblaze.blazetwitchmod.component;

import com.mojang.serialization.Codec;
import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BlazinBlazeTwitchModComponents {
    protected static void initialize() {
        BlazinBlazeTwitchMod.LOGGER.info("Registering {} components", BlazinBlazeTwitchMod.MOD_ID);
        // Technically this method can stay empty, but some developers like to notify
        // the console, that certain parts of the mod have been successfully initialized
    }

    public static final ComponentType<String> MAIL_MESSAGE = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "mail_message"),
            ComponentType.<String>builder().codec(Codec.STRING).build()
    );

    public static final ComponentType<String> MAIL_AUTHOR = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "mail_author"),
            ComponentType.<String>builder().codec(Codec.STRING).build()
    );

    public static final ComponentType<String> MAIL_TYPE = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "mail_type"),
            ComponentType.<String>builder().codec(Codec.STRING).build()
    );

    public static final ComponentType<String> MAIL_FAREWELL = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "mail_farewell"),
            ComponentType.<String>builder().codec(Codec.STRING).build()
    );

    public static final ComponentType<String> VOICE_MESSAGE = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "voice_message"),
            ComponentType.<String>builder().codec(Codec.STRING).build()
    );

    public static final ComponentType<String> VOICE_AUTHOR = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "voice_author"),
            ComponentType.<String>builder().codec(Codec.STRING).build()
    );
}
