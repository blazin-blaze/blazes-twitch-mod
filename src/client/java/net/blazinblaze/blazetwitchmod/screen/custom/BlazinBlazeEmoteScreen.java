package net.blazinblaze.blazetwitchmod.screen.custom;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.util.EmoteTextures;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

public class BlazinBlazeEmoteScreen extends Screen {

    //private final NativeImageBackedTexture texture;
    //private final String emoteID;

    public BlazinBlazeEmoteScreen(Text title) throws IOException {
        super(title);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);

        int x = (width - 256) / 2;
        int y = (height - 171) / 2;

        BlazinBlazeTwitchMod.LOGGER.info("RENDER FIRED");
        //if(texture != null) {
            //context.drawTexture(RenderPipelines.GUI_TEXTURED, Identifier.of(BlazinBlazeTwitchMod.MOD_ID, emoteID), x, y, 0, 0, texture.getImage().getWidth(), texture.getImage().getHeight(), texture.getImage().getWidth(), texture.getImage().getHeight());
        //}
    }

    @Override
    public boolean shouldPause() {
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
