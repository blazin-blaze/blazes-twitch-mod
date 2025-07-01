package net.blazinblaze.blazetwitchmod.util;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;

import java.util.List;

public class BlazeImage {

    private final NativeImageBackedTexture texture;
    private final String id;

    public BlazeImage(NativeImageBackedTexture texture, String id) {
        this.texture = texture;
        this.id = id;
    }

    public NativeImageBackedTexture getTexture() {
        return this.texture;
    }

    public String getID() {
        return this.id;
    }

}
