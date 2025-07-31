package net.blazinblaze.blazetwitchmod.screen.custom;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

public class BlazinBlazeCloseScreen extends Screen {

    private final String user;

    public BlazinBlazeCloseScreen(Text title, String user) {
        super(title);
        this.user = user;
    }

    @Override
    protected void init() {

        int x = (width / 2) - (60 / 2);
        int y = (height + 100) / 2;

        ButtonWidget buttonWidget = ButtonWidget.builder(Text.of("Ok."), (btn) -> {

            this.client.getWindow().close();

        }).dimensions(x, y - 50, 60, 20).build();

        this.addDrawableChild(buttonWidget);

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);

        int y = (height - 80) / 2;
        int y2 = (height - 40) / 2;
        context.drawText(this.textRenderer, Text.literal("§4§lWINDOW CLOSED!"), context.getScaledWindowWidth()/2 - textRenderer.getWidth("§4§lWINDOW CLOSED!")/2, y, Colors.WHITE, true);
        context.drawText(this.textRenderer, Text.literal(String.format("%s just closed your game's window!", user)), context.getScaledWindowWidth()/2 - textRenderer.getWidth(String.format("%s just closed your game's window!", user)) / 2, y2, Colors.WHITE, true);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean shouldPause() {
        return true;
    }
}
