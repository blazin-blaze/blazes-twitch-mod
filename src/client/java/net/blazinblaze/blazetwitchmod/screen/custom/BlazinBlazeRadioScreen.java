package net.blazinblaze.blazetwitchmod.screen.custom;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.screen.MailboxScreenHandler;
import net.blazinblaze.blazetwitchmod.screen.RadioScreenHandler;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BlazinBlazeRadioScreen extends HandledScreen<RadioScreenHandler> {

    private static final Identifier TEXTURE = Identifier.of(BlazinBlazeTwitchMod.MOD_ID, "textures/gui/container/radio_screen.png");

    public BlazinBlazeRadioScreen(RadioScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {//-16448250
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, -3289650, false);
        context.drawText(this.textRenderer, this.playerInventoryTitle, this.playerInventoryTitleX, this.playerInventoryTitleY, -3289650, false);
    }
}
