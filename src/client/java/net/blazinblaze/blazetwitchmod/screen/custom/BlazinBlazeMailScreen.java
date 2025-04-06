package net.blazinblaze.blazetwitchmod.screen.custom;

import com.ibm.icu.text.BidiTransform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
//import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.ColorCode;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.*;
import java.util.List;

@Environment(EnvType.CLIENT)
public class BlazinBlazeMailScreen extends Screen {

    private final String messageVar;
    private final String typeVar;
    private final String authorVar;
    private final String farewellVar;

    public static final Identifier GUI_TEXTURE_DEFAULT = Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "textures/gui/mail_item/mail_background.png");
    public static final Identifier GUI_TEXTURE_BLAZIN = Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "textures/gui/mail_item/mail_background_blazin.png");
    public static final Identifier GUI_TEXTURE_EPIC = Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "textures/gui/mail_item/mail_background_epic.png");
    public static final Identifier GUI_TEXTURE_ERROR = Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "textures/gui/mail_item/mail_background_error.png");
    public static final Identifier GUI_TEXTURE_MINECRAFT = Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "textures/gui/mail_item/mail_background_minecraft.png");
    public static final Identifier GUI_TEXTURE_SINISTER = Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "textures/gui/mail_item/mail_background_sinister.png");
    public static final Identifier GUI_TEXTURE_STARRY = Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "textures/gui/mail_item/mail_background_starry.png");

    private static final Map<String, Identifier> MAIL_GUI_TYPES = Map.ofEntries(
            Map.entry("default", GUI_TEXTURE_DEFAULT),
            Map.entry("starry", GUI_TEXTURE_STARRY),
            Map.entry("blazin", GUI_TEXTURE_BLAZIN),
            Map.entry("epic", GUI_TEXTURE_EPIC),
            Map.entry("minecraft", GUI_TEXTURE_MINECRAFT),
            Map.entry("error", GUI_TEXTURE_ERROR),
            Map.entry("sinister", GUI_TEXTURE_SINISTER));

    private static final Map<String, Integer> MAIL_AUTHOR_COLORS = Map.ofEntries(
            Map.entry("default", Colors.YELLOW),
            Map.entry("starry", Colors.LIGHT_YELLOW),
            Map.entry("blazin", Colors.CYAN),
            Map.entry("epic", Colors.CYAN),
            Map.entry("minecraft", Colors.LIGHT_RED),
            Map.entry("error", Colors.WHITE),
            Map.entry("sinister", Colors.RED));

    public BlazinBlazeMailScreen(Text title, String message, String type, String author, String farewell) {
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

        ButtonWidget buttonWidget = ButtonWidget.builder(Text.of("X"), (btn) -> {
            // When the button is clicked, we can display a toast to the screen.

            this.close();

            //this.client.getToastManager().add(
                    //SystemToast.create(this.client, SystemToast.Type.NARRATOR_TOGGLE, Text.of("Hello World!"), Text.of("This is a toast."))
            //);
        }).dimensions(x + 245, y - 35, 20, 20).build();
        // x, y, width, height
        // It's recommended to use the fixed height of 20 to prevent rendering issues with the button
        // textures.

        // Register the button widget.
        this.addDrawableChild(buttonWidget);

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // Minecraft doesn't have a "label" widget, so we'll have to draw our own text.
        // We'll subtract the font height from the Y position to make the text appear above the button.
        // Subtracting an extra 10 pixels will give the text some padding.
        // textRenderer, text, x, y, color, hasShadow
        int x = (width - 240) / 2;
        int y = (height - 110) / 2;

        if(messageVar != null) {
            List<OrderedText> text = this.textRenderer.wrapLines(StringVisitable.plain(messageVar), 220);
            if(text != null) {
                for (int i = 0; i < text.size(); i++) {
                    if (i < 11) {
                        context.drawText(this.textRenderer, text.get(i), x, (y + i * 10) - this.textRenderer.fontHeight - 10, Colors.WHITE, true);
                    } else if(i == 11) {
                        if(text.size() > 12) {
                            context.drawText(this.textRenderer, OrderedText.concat(text.get(i), Text.of("[...]").asOrderedText()), x, (y + i * 10) - this.textRenderer.fontHeight - 10, Colors.WHITE, true);
                        }else {
                            context.drawText(this.textRenderer, text.get(i), x, (y + i * 10) - this.textRenderer.fontHeight - 10, Colors.WHITE, true);
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
                context.drawText(this.textRenderer, Text.literal(farewellVar + authorVar), (x + 25), (y + 135) - this.textRenderer.fontHeight - 10, MAIL_AUTHOR_COLORS.get(typeVar), true);
            }else {
                context.drawText(this.textRenderer, Text.literal(farewellVar + authorVar), x, (y + 135) - this.textRenderer.fontHeight - 10, MAIL_AUTHOR_COLORS.get(typeVar), true);
            }
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        if(typeVar != null) {
            //RenderSystem.setshader();
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            RenderSystem.setShaderTexture(0, RenderSystem.getDevice().createTexture(MAIL_GUI_TYPES.get(typeVar).getPath(), TextureFormat.DEPTH32, 256, 171, TextureFormat.DEPTH32.pixelSize()));

            int x = (width - 256) / 2;
            int y = (height - 171) / 2;

            context.drawTexture(RenderLayer::getGuiTextured, MAIL_GUI_TYPES.get(typeVar), x, y, 0, 0, 256, 171, 256, 171);
        }
    }
}
