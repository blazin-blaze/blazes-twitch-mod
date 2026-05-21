package net.blazinblaze.blazetwitchmod.mm;

import com.chocohead.mm.api.ClassTinkerers;
import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.util.Identifier;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class EarlyRisers implements Runnable {
    @Override
    public void run() {
        MappingResolver remapper = FabricLoader.getInstance().getMappingResolver();
        String layer = remapper.mapClassName("intermediary", "net.minecraft.class_372$class_373");
        ClassTinkerers.enumBuilder(layer, "Lnet/minecraft/util/Identifier;").addEnum("MAIL", () -> new Object[] {Identifier.of("blaze-twitch-mod", "toast/mail_toast_icon")}).build();
        ClassTinkerers.enumBuilder(layer, "Lnet/minecraft/util/Identifier;").addEnum("VOICE_MSG", () -> new Object[] {Identifier.of("blaze-twitch-mod", "toast/voice_message_toast_icon")}).build();
    }
}
