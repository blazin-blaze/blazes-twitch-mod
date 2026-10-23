package net.blazinblaze.blazetwitchmod.screen.custom;

import com.ibm.icu.text.BidiTransform;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import com.mojang.blaze3d.opengl.GlProgram;
//import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.rendertype.RenderType;
import com.mojang.blaze3d.opengl.GlTexture;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;
import net.minecraft.core.Holder;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.CommonColors;
import net.minecraft.resources.Identifier;

import java.awt.*;
import java.util.*;
import java.util.List;

@Environment(EnvType.CLIENT)
public class BlazinBlazeMailScreen extends Screen {

    private final String messageVar;
    private final String typeVar;
    private final String authorVar;
    private final String farewellVar;

    public static final Identifier GUI_TEXTURE_DEFAULT = Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "textures/gui/mail_item/mail_background.png");
    public static final Identifier GUI_TEXTURE_BLAZIN = Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "textures/gui/mail_item/mail_background_blazin.png");
    public static final Identifier GUI_TEXTURE_EPIC = Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "textures/gui/mail_item/mail_background_epic.png");
    public static final Identifier GUI_TEXTURE_ERROR = Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "textures/gui/mail_item/mail_background_error.png");
    public static final Identifier GUI_TEXTURE_MINECRAFT = Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "textures/gui/mail_item/mail_background_minecraft.png");
    public static final Identifier GUI_TEXTURE_SINISTER = Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "textures/gui/mail_item/mail_background_sinister.png");
    public static final Identifier GUI_TEXTURE_STARRY = Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, "textures/gui/mail_item/mail_background_starry.png");

    private static final Map<String, Identifier> MAIL_GUI_TYPES = Map.ofEntries(
            Map.entry("default", GUI_TEXTURE_DEFAULT),
            Map.entry("starry", GUI_TEXTURE_STARRY),
            Map.entry("blazin", GUI_TEXTURE_BLAZIN),
            Map.entry("epic", GUI_TEXTURE_EPIC),
            Map.entry("minecraft", GUI_TEXTURE_MINECRAFT),
            Map.entry("error", GUI_TEXTURE_ERROR),
            Map.entry("sinister", GUI_TEXTURE_SINISTER));

    private static final Map<String, Integer> MAIL_AUTHOR_COLORS = Map.ofEntries(
            Map.entry("default", CommonColors.YELLOW),
            Map.entry("starry", CommonColors.SOFT_YELLOW),
            Map.entry("blazin", CommonColors.HIGH_CONTRAST_DIAMOND),
            Map.entry("epic", CommonColors.HIGH_CONTRAST_DIAMOND),
            Map.entry("minecraft", CommonColors.SOFT_RED),
            Map.entry("error", CommonColors.WHITE),
            Map.entry("sinister", CommonColors.RED));

    public BlazinBlazeMailScreen(Component title, String message, String type, String author, String farewell) {
        super(title);
        messageVar = message;
        typeVar = type;
        authorVar = author;
        farewellVar = farewell;
    }

    @Override
    protected void init() {

        int x = (width - 240) / 2;
        int y = (height - 110) / 2;

        Button buttonWidget = Button.builder(Component.nullToEmpty("X"), (btn) -> {
            // When the button is clicked, we can display a toast to the screen.

            this.onClose();

            //this.client.getToastManager().add(
                    //SystemToast.create(this.client, SystemToast.Type.NARRATOR_TOGGLE, Text.of("Hello World!"), Text.of("This is a toast."))
            //);
        }).bounds(x + 245, y - 35, 20, 20).build();
        // x, y, width, height
        // It's recommended to use the fixed height of 20 to prevent rendering issues with the button
        // textures.

        // Register the button widget.
        this.addRenderableWidget(buttonWidget);

    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        //context.drawItem(Items.ACACIA_LOG.getDefaultStack(), 200, 200);
        super.extractRenderState(graphics, mouseX, mouseY, a);

        // Minecraft doesn't have a "label" widget, so we'll have to draw our own text.
        // We'll subtract the font height from the Y position to make the text appear above the button.
        // Subtracting an extra 10 pixels will give the text some padding.
        // textRenderer, text, x, y, color, hasShadow

        int x = (width - 240) / 2;
        int y = (height - 110) / 2;

        if(messageVar != null) {
            List<FormattedCharSequence> text = this.font.split(FormattedText.of(messageVar), 220);
            if(text != null) {
                for (int i = 0; i < text.size(); i++) {
                    if (i < 11) {
                        graphics.text(this.font, text.get(i), x, (y + i * 10) - this.font.lineHeight - 10, CommonColors.WHITE, true);
                    } else if(i == 11) {
                        if(text.size() > 12) {
                            graphics.text(this.font, FormattedCharSequence.composite(text.get(i), Component.nullToEmpty("[...]").getVisualOrderText()), x, (y + i * 10) - this.font.lineHeight - 10, CommonColors.WHITE, true);
                        }else {
                            graphics.text(this.font, text.get(i), x, (y + i * 10) - this.font.lineHeight - 10, CommonColors.WHITE, true);
                        }
                    }
                    else {
                        break;
                    }
                }
            }
        }
        if(authorVar != null) {
            if(Objects.equals(typeVar, "epic") || Objects.equals(typeVar, "sinister")) {
                graphics.text(this.font, Component.literal(farewellVar + authorVar), (x + 25), (y + 135) - this.font.lineHeight - 10, MAIL_AUTHOR_COLORS.get(typeVar), true);
            }else {
                graphics.text(this.font, Component.literal(farewellVar + authorVar), x, (y + 135) - this.font.lineHeight - 10, MAIL_AUTHOR_COLORS.get(typeVar), true);
            }
        }
    }

    @Override
    public void extractMenuBackground(GuiGraphicsExtractor graphics) {
        super.extractMenuBackground(graphics);
        //RenderSystem.setshader();
        //RenderSystem.setShaderTexture(0,
        //RenderSystem.getDevice().createTextureView(
        //RenderSystem.getDevice().createTexture(MAIL_GUI_TYPES.get(typeVar).getPath(), GlTexture.USAGE_TEXTURE_BINDING, TextureFormat.DEPTH32, 256, 171, TextureFormat.DEPTH32.pixelSize(), this.client.options.getMipmapLevels().getValue())
        //));

        int x = (width - 256) / 2;
        int y = (height - 171) / 2;

        graphics.blit(RenderPipelines.GUI_TEXTURED, MAIL_GUI_TYPES.get(typeVar), x, y, 0, 0, 256, 171, 256, 171);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
