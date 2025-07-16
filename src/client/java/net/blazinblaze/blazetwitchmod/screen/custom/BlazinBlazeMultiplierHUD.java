package net.blazinblaze.blazetwitchmod.screen.custom;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.util.MultiplierTick;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Util;
import org.apache.commons.lang.mutable.MutableInt;

import java.util.concurrent.atomic.AtomicInteger;

public class BlazinBlazeMultiplierHUD implements HudElement {

    private static BlazinBlazeMultiplierHUD hudInstance;

    private BlazinBlazeMultiplierHUD() {
    }

    public static BlazinBlazeMultiplierHUD getInstance() {
        if(hudInstance == null) {
            hudInstance = new BlazinBlazeMultiplierHUD();
        }
        return hudInstance;
    }

    @Override
    public void render(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        int multipler = MultiplierTick.MultiplierTimer.getMultiplier();
        String text1 = String.format("§lCURRENT MULTIPLIER (x%s)", multipler);
        double remainingSeconds = 600 - Math.floor((double) MultiplierTick.MultiplierTimer.getTimer() / 20);
        int minutes = (int)Math.floor(remainingSeconds / 60);
        int seconds = (int)(remainingSeconds % 60);
        String text2 = String.format("%s:%s", minutes < 10 ? "0" + minutes : minutes, seconds < 10 ? "0" + seconds : seconds);

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        drawContext.getMatrices().pushMatrix();
        drawContext.getMatrices().scale(2, 2);
        drawContext.drawText(textRenderer, Text.literal(text1), 10, textRenderer.fontHeight, -11671472, true);
        drawContext.drawText(textRenderer, Text.literal(text2), 10, 10 + textRenderer.fontHeight, -7083952, true);
        drawContext.getMatrices().popMatrix();
    }
}
