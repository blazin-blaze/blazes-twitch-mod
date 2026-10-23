package net.blazinblaze.blazetwitchmod.util;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.config.GifDelaysFile;
import net.fabricmc.loader.api.FabricLoader;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.apache.commons.lang3.mutable.MutableInt;

import javax.imageio.stream.ImageInputStream;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntUnaryOperator;

public class BlazeGif extends BlazeImage {

    private final File[] images;
    private final MutableInt frame = new MutableInt();
    private final MutableInt delayProgress = new MutableInt();

    public BlazeGif(DynamicTexture texture, String id, File[] images) {
        super(texture, id);
        this.images = images;
    }

    public void tick() throws FileNotFoundException {
        File configFile = new File(FabricLoader.getInstance().getConfigDir().resolve("btm-gif-delays.json").toString());
        GifDelaysFile config = GifDelaysFile.loadConfigFile(configFile);
        ArrayList<Integer> delays = config.gifDelays.get(this.getID());
        if(delays != null) {
            int index = frame.getValue() % images.length;
            if(index < delays.size()) {
                int currentDelay = delays.get(index);
                if(currentDelay == delayProgress.intValue()) {
                    File file = images[(frame.getAndIncrement() % images.length)];

                    try (InputStream fileInputStream = new FileInputStream(file)){
                        DynamicTexture texture = this.getTexture();
                        delayProgress.setValue(0);
                        NativeImage image = NativeImage.read(fileInputStream);
                        texture.setPixels(image);
                        texture.upload();
                    } catch (IOException e) {
                        BlazinBlazeTwitchMod.LOGGER.error("Error while trying to switch to new GIF frame", e);
                    }

                    //image.resizeSubRectTo(0, 0, image.getWidth(), image.getHeight(), texture.getImage());
                }else if(currentDelay > delayProgress.intValue()) {
                    delayProgress.increment();
                }
            }
        }
    }

}
