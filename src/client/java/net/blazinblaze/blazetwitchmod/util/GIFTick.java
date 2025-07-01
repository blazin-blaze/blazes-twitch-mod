package net.blazinblaze.blazetwitchmod.util;

import net.blazinblaze.blazetwitchmod.screen.custom.BlazinBlazeEmoteHUD;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

import java.io.FileNotFoundException;
import java.util.List;

public class GIFTick implements ClientTickEvents.EndTick {

    public static final GIFTick INSTANCE = new GIFTick();
    private static BlazinBlazeEmoteHUD hud = BlazinBlazeEmoteHUD.getInstance();

    @Override
    public void onEndTick(MinecraftClient client) {
        List<BlazeImage> images = hud.getGifs();
        for(BlazeImage image : images) {
            if(image instanceof BlazeGif gif) {
                try {
                    gif.tick();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(INSTANCE);
    }
}
