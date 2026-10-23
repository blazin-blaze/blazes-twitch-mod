package net.blazinblaze.blazetwitchmod.screen.custom;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.util.MultiplierTick;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
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
    public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        int multipler = MultiplierTick.MultiplierTimer.getMultiplier();
        String text1 = String.format("§lCURRENT MULTIPLIER (x%s)", multipler);
        double remainingSeconds = 600 - Math.floor((double) MultiplierTick.MultiplierTimer.getTimer() / 20);
        int minutes = (int)Math.floor(remainingSeconds / 60);
        int seconds = (int)(remainingSeconds % 60);
        String text2 = String.format("%s:%s", minutes < 10 ? "0" + minutes : minutes, seconds < 10 ? "0" + seconds : seconds);

        Font textRenderer = Minecraft.getInstance().font;

        graphics.pose().pushMatrix();
        graphics.pose().scale(2, 2);
        graphics.text(textRenderer, Component.literal(text1), 10, textRenderer.lineHeight, -11671472, true);
        graphics.text(textRenderer, Component.literal(text2), 10, 10 + textRenderer.lineHeight, -7083952, true);
        graphics.pose().popMatrix();
    }
}
