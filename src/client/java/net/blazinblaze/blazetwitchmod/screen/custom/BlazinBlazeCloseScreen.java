package net.blazinblaze.blazetwitchmod.screen.custom;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;

public class BlazinBlazeCloseScreen extends Screen {

    private final String user;

    public BlazinBlazeCloseScreen(Component title, String user) {
        super(title);
        this.user = user;
    }

    @Override
    protected void init() {

        int x = (width / 2) - (60 / 2);
        int y = (height + 100) / 2;

        Button buttonWidget = Button.builder(Component.nullToEmpty("Ok."), (btn) -> {

            this.minecraft.getWindow().close();

        }).bounds(x, y - 50, 60, 20).build();

        this.addRenderableWidget(buttonWidget);

    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);

        int y = (height - 80) / 2;
        int y2 = (height - 40) / 2;
        graphics.text(this.font, Component.literal("§4§lWINDOW CLOSED!"), graphics.guiWidth()/2 - font.width("§4§lWINDOW CLOSED!")/2, y, CommonColors.WHITE, true);
        graphics.text(this.font, Component.literal(String.format("%s just closed your game's window!", user)), graphics.guiWidth()/2 - font.width(String.format("%s just closed your game's window!", user)) / 2, y2, CommonColors.WHITE, true);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }
}
