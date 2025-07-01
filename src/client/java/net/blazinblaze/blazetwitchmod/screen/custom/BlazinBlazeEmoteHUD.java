package net.blazinblaze.blazetwitchmod.screen.custom;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.util.BlazeGif;
import net.blazinblaze.blazetwitchmod.util.BlazeImage;
import net.blazinblaze.blazetwitchmod.util.CloseEmoteTimer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class BlazinBlazeEmoteHUD implements HudElement {

    private static BlazinBlazeEmoteHUD emoteHUDInstance;
    private ConcurrentMap<String, BlazeImage> currentEmotes = new ConcurrentHashMap<>();
    private ConcurrentMap<String, EmoteLocation> emoteLocations = new ConcurrentHashMap<>();
    private int maxWidth;
    private int maxHeight;

    private BlazinBlazeEmoteHUD() {
    }

    public static BlazinBlazeEmoteHUD getInstance() {
        if(emoteHUDInstance == null) {
            emoteHUDInstance = new BlazinBlazeEmoteHUD();
        }
        return emoteHUDInstance;
    }

    public List<BlazeImage> getGifs() {
        List<BlazeImage> gifs = new ArrayList<>();
        currentEmotes.forEach((key, value) -> {
            if(value instanceof BlazeGif) {
                gifs.add(value);
            }
        });
        return gifs;
    }

    public void setEmoteLocation(String id) {
        maxWidth = MinecraftClient.getInstance().getWindow().getWidth();
        maxHeight = MinecraftClient.getInstance().getWindow().getHeight();
        Random random = new Random();
        int x = (maxWidth - random.nextInt(maxWidth)) / 2;
        int y = (maxHeight - random.nextInt(maxHeight)) / 2;
        if(x < 0) {
            x = 0;
        }
        if(y < 0) {
            y = 0;
        }
        emoteLocations.put(id, new EmoteLocation(x, y));
    }

    public void setCurrentEmote(String id, BlazeImage image) {
        currentEmotes.put(id, image);
        currentEmotes.forEach((key, value) -> {
            BlazinBlazeTwitchMod.LOGGER.info(key);
        });
        CloseEmoteTimer.INSTANCE.setCloseTimer(100L, id, image);
        setEmoteLocation(id);
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player != null) {
            player.playSoundToPlayer(SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, SoundCategory.NEUTRAL, 1.0F, 1.0F);
        }
    }

    public void closeEmote(String id, BlazeImage image) {
        currentEmotes.remove(id, image);
        emoteLocations.remove(id);
        image.getTexture().close();
    }

    public void closeAllEmotes() {
        currentEmotes.forEach((key, value) -> {
            value.getTexture().close();
            currentEmotes.remove(key, value);
            emoteLocations.remove(key);
        });
    }

    @Override
    public void render(DrawContext context, RenderTickCounter tickCounter) {
        maxWidth = MinecraftClient.getInstance().getWindow().getWidth();
        maxHeight = MinecraftClient.getInstance().getWindow().getHeight();
        currentEmotes.forEach((key, value) -> {
            EmoteLocation location = emoteLocations.get(key);
            //if(location == null) {
                //setEmoteLocation(key);
                //location = emoteLocations.get(key);
           // }
            if(value instanceof BlazeGif gif) {
                try {
                    gif.tick();
                } catch (FileNotFoundException e) {
                    BlazinBlazeTwitchMod.LOGGER.error("Error while trying to get new GIF frame", e);
                }
            }
            int width = value.getTexture().getImage().getWidth();
            int height = value.getTexture().getImage().getHeight();
            int x = location.x;
            int y = location.y;
            context.drawTexture(RenderPipelines.GUI_TEXTURED, Identifier.of(BlazinBlazeTwitchMod.MOD_ID, key), x, y, 0, 0, width, height, width, height);
        });
    }

    record EmoteLocation(int x, int y) {}
}
