package net.blazinblaze.blazetwitchmod.util;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.DynamicTexture;

import java.util.List;

public class BlazeImage {

    private final DynamicTexture texture;
    private final String id;

    public BlazeImage(DynamicTexture texture, String id) {
        this.texture = texture;
        this.id = id;
    }

    public DynamicTexture getTexture() {
        return this.texture;
    }

    public String getID() {
        return this.id;
    }

}
