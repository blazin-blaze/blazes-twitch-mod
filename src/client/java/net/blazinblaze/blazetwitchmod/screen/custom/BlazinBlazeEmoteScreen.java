package net.blazinblaze.blazetwitchmod.screen.custom;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.util.EmoteTextures;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.screens.Screen;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

public class BlazinBlazeEmoteScreen extends Screen {

    //private final NativeImageBackedTexture texture;
    //private final String emoteID;

    public BlazinBlazeEmoteScreen(Component title) throws IOException {
        super(title);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);

        int x = (width - 256) / 2;
        int y = (height - 171) / 2;

        BlazinBlazeTwitchMod.LOGGER.info("RENDER FIRED");
        //if(texture != null) {
        //context.drawTexture(RenderPipelines.GUI_TEXTURED, Identifier.of(BlazinBlazeTwitchMod.MOD_ID, emoteID), x, y, 0, 0, texture.getImage().getWidth(), texture.getImage().getHeight(), texture.getImage().getWidth(), texture.getImage().getHeight());
        //}
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
