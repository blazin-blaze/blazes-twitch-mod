package net.blazinblaze.blazetwitchmod.component;

import com.mojang.serialization.Codec;
import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public class BlazinBlazeTwitchModComponents {
    protected static void initialize() {
        BlazinBlazeTwitchMod.LOGGER.info("Registering {} components", BlazinBlazeTwitchMod.MOD_ID);
    }

    public static final DataComponentType<String> MAIL_MESSAGE = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "mail_message"),
            DataComponentType.<String>builder().persistent(Codec.STRING).build()
    );

    public static final DataComponentType<String> MAIL_AUTHOR = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "mail_author"),
            DataComponentType.<String>builder().persistent(Codec.STRING).build()
    );

    public static final DataComponentType<String> MAIL_TYPE = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "mail_type"),
            DataComponentType.<String>builder().persistent(Codec.STRING).build()
    );

    public static final DataComponentType<String> MAIL_FAREWELL = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "mail_farewell"),
            DataComponentType.<String>builder().persistent(Codec.STRING).build()
    );

    public static final DataComponentType<String> VOICE_MESSAGE = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "voice_message"),
            DataComponentType.<String>builder().persistent(Codec.STRING).build()
    );

    public static final DataComponentType<String> VOICE_AUTHOR = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "voice_author"),
            DataComponentType.<String>builder().persistent(Codec.STRING).build()
    );

    public static final DataComponentType<String> FAKE_EYE_USER = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "fake_eye_user"),
            DataComponentType.<String>builder().persistent(Codec.STRING).build()
    );
}
