package net.blazinblaze.blazetwitchmod.util;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchModClient;
import net.blazinblaze.blazetwitchmod.config.GifDelaysFile;
import net.blazinblaze.blazetwitchmod.screen.custom.BlazinBlazeEmoteHUD;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.impl.client.rendering.hud.HudLayer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import org.apache.commons.lang3.mutable.MutableInt;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class EmoteTextures {

    private static final BlockingQueue<EmoteTask> taskQueue = new LinkedBlockingQueue<>();
    private static final ConcurrentMap<String, File> cachedEmoteImages = BlazinBlazeTwitchModClient.cachedEmoteImages;
    private static final BlazinBlazeEmoteHUD hud = BlazinBlazeEmoteHUD.getInstance();
    private static final AtomicInteger emoteIdCounter = new AtomicInteger(0);
    private static final Thread workerThread;

    static {
        workerThread = new Thread(() -> {
            while (true) {
                try {
                    EmoteTask task = taskQueue.take();
                    processTask(task);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    BlazinBlazeTwitchMod.LOGGER.error("Error processing emote task", e);
                }
            }
        }, "EmoteTextureWorker");

        workerThread.setDaemon(true);
        workerThread.start();
    }

    public static void enqueueEmote(String emoteId, String imageType) {
        taskQueue.offer(new EmoteTask(emoteId, imageType));
    }

    private static void processTask(EmoteTask task) throws IOException {

        String id = task.id;
        String imageType = task.imageType;

        File cachedFile = cachedEmoteImages.get(id);
        if (cachedFile == null || !cachedFile.exists()) {
            cachedFile = downloadEmote(id, imageType);
            cachedEmoteImages.put(id, cachedFile);
        }

        if(imageType.matches("gif")) {
            String outputDir = cachedFile.getParent() + String.format("\\%s-pngs", cachedFile.getName());

            File outputDirectory = new File(outputDir);
            if(!outputDirectory.exists()) {
                outputDirectory.mkdirs();
                File configFile = new File(FabricLoader.getInstance().getConfigDir().resolve("btm-gif-delays.json").toString());
                GifDelaysFile config = GifDelaysFile.loadConfigFile(configFile);
                List<ImageFrame> gifFrames = processGifFrames(cachedFile);
                ArrayList<Integer> delays = new ArrayList<>();
                for (int i = 0; i < gifFrames.size(); i++) {
                    var frameImage = gifFrames.get(i);
                    File outputFile = new File(outputDir + "\\frame_" + String.format("%04d", i) + ".png");
                    ImageIO.write(frameImage.getImage(), "PNG", outputFile);
                    delays.add(frameImage.getDelay());
                    BlazinBlazeTwitchMod.LOGGER.info("Saved {}", outputFile.getName());
                }
                config.gifDelays.put(id, delays);
                config.saveConfigFile(configFile);
            }
            if(outputDirectory.exists() && outputDirectory.isDirectory()) {
                File[] files = outputDirectory.listFiles();
                if(files != null) {
                    //List<NativeImage> images = new ArrayList<>();
                    //for(File file : files) {
                        //try (InputStream fileInputStream = new FileInputStream(file)) {
                            //NativeImage image = NativeImage.read(fileInputStream);
                            //images.add(image);
                        //}
                    //}
                    uploadAnimatedTextureToMinecraft(id, files);
                }
            }
        }else {
            try (InputStream fileInputStream = new FileInputStream(cachedFile)) {
                NativeImage image = NativeImage.read(fileInputStream);
                uploadTextureToMinecraft(id, image);
            }
        }
    }

    public static List<ImageFrame> processGifFrames(File gif) throws IOException {

        String DISPOSAL_PREVIOUS = "restoreToPrevious";

        String DISPOSAL_BACKGROUND = "restoreToBackgroundColor";

        var frames = new ArrayList<ImageFrame>(2);

        try (var is = ImageIO.createImageInputStream(gif)) {

            var it = ImageIO.getImageReadersBySuffix("gif");
            if (!it.hasNext())
                throw new IOException("No supported reader for this format");

            final var reader = it.next();
            reader.setInput(is);

            try {
                final var size = extractLogicalScreenSize(reader);
                int width = size != null ? size[0] : -1;
                int height = size != null ? size[1] : -1;

                BufferedImage master = null;
                Graphics2D baseImage = null;
                int numFrames = reader.getNumImages(true);

                for (int frameIndex = 0; frameIndex < numFrames; frameIndex++) {
                    BufferedImage img;

                    try {
                        img = reader.read(frameIndex);
                    } catch (IndexOutOfBoundsException e) {
                        break;
                    }

                    if (width == -1 || height == -1) {
                        width = img.getWidth();
                        height = img.getHeight();
                    }

                    ImageMetaData imageMetadata = extractImageMetaData(reader, frameIndex);
                    int x = imageMetadata.x;
                    int y = imageMetadata.y;

                    if (master == null) {
                        master = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                        baseImage = master.createGraphics();
                        baseImage.setBackground(new java.awt.Color(0, 0, 0, 0));
                    }

                    baseImage.drawImage(img, x, y, null);

                    //copy master image to image frame
                    var copy = new BufferedImage(master.getColorModel(), master.copyData(null), master.isAlphaPremultiplied(), null);
                    var imageFrame = new ImageFrame(copy, imageMetadata.delay, imageMetadata.disposal, width, height);
                    frames.add(imageFrame);

                    //Process disposal
                    if (DISPOSAL_PREVIOUS.equals(imageMetadata.disposal)) {
                        BufferedImage from = null;
                        //scan frames backwards (from current to first), search for last processed undisposed frame
                        for (int i = frameIndex - 1; i >= 0; i--) {
                            var frame = frames.get(i);
                            //scan previous undisposed frame
                            if (!DISPOSAL_PREVIOUS.equals(frame.getDisposal()) || frameIndex == 0) {
                                from = frame.getImage();
                                break;
                            }
                        }
                        if (from != null) {
                            master = new BufferedImage(from.getColorModel(), from.copyData(null), from.isAlphaPremultiplied(), null);
                            baseImage = master.createGraphics();
                            //clear everything with transparent pixels
                            baseImage.setBackground(new java.awt.Color(0, 0, 0, 0));
                        }
                    } else if (DISPOSAL_BACKGROUND.equals(imageMetadata.disposal)) {
                        baseImage.clearRect(x, y, img.getWidth(), img.getHeight());
                    }

                } //end for
                return frames;
            } finally {
                reader.dispose();
            }
        }
    }

    private static Integer[] extractLogicalScreenSize(ImageReader reader) {
        Integer[] size = null;
        try {
            var metadata = reader.getStreamMetadata();
            if (metadata == null)
                return null;
            var globalRoot = (IIOMetadataNode) metadata.getAsTree(metadata.getNativeMetadataFormatName());
            var globalScreenDescriptor = globalRoot.getElementsByTagName("LogicalScreenDescriptor");
            if (globalScreenDescriptor != null && globalScreenDescriptor.getLength() > 0) {
                IIOMetadataNode screenDescriptor = (IIOMetadataNode) globalScreenDescriptor.item(0);
                if (screenDescriptor != null) {
                    size = new Integer[2];
                    size[0] = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenWidth"));
                    size[1] = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenHeight"));
                }
            }
        } catch (IOException e) {
            BlazinBlazeTwitchMod.LOGGER.error("Issue extracting logical screen size: {}", String.valueOf(e));
        }
        return size;
    }

    private static ImageMetaData extractImageMetaData(ImageReader reader, int frameIndex) {
        try {
            var metadata = reader.getImageMetadata(frameIndex);
            var root = (IIOMetadataNode) metadata.getAsTree("javax_imageio_gif_image_1.0");
            var gce = (IIOMetadataNode) root.getElementsByTagName("GraphicControlExtension").item(0);
            int delay = Integer.parseInt(gce.getAttribute("delayTime"));
            String disposal = gce.getAttribute("disposalMethod");
            int x = 0, y = 0;
            var children = root.getChildNodes();
            for (int nodeIndex = 0; nodeIndex < children.getLength(); nodeIndex++) {
                Node nodeItem = children.item(nodeIndex);
                if ("ImageDescriptor".equalsIgnoreCase(nodeItem.getNodeName())) {
                    NamedNodeMap map = nodeItem.getAttributes();
                    x = Integer.parseInt(map.getNamedItem("imageLeftPosition").getNodeValue());
                    y = Integer.parseInt(map.getNamedItem("imageTopPosition").getNodeValue());
                }
            }
            return new ImageMetaData(frameIndex, delay, disposal, x, y);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void deleteFile(File file) {
        if(file.exists()) {
            if(file.isDirectory()) {
                File[] files = file.listFiles();
                if(files != null) {
                    for(File file1 : files) {
                        deleteFile(file1);
                    }
                }
            }
            file.delete();
        }
    }

    private static File downloadEmote(String id, String imageType) throws IOException {
        String urlString = String.format("https://cdn.betterttv.net/emote/%s/1x.%s", id, imageType);
        BlazinBlazeTwitchMod.LOGGER.info("Downloading emote from URL: {}", urlString);

        File dir = new File(Minecraft.getInstance().gameDirectory, "btmEmoteCache");
        if (!dir.exists()) dir.mkdirs();

        File file = new File(dir, String.format("%s.%s", id, imageType));
        Path targetPath = file.toPath();

        try (InputStream in = new URL(urlString).openStream()) {
            Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        return file;
    }

    private static void uploadTextureToMinecraft(String id, NativeImage image) {
        Minecraft.getInstance().execute(() -> {
            try {
                String uniqueId = id + "-" + emoteIdCounter.getAndIncrement();
                DynamicTexture texture = new DynamicTexture(() -> uniqueId, image);
                Identifier textureId = Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, uniqueId);

                Minecraft.getInstance().getTextureManager().register(textureId, texture);
                texture.upload();

                BlazeImage blazeImage = new BlazeImage(texture, id);

                hud.setCurrentEmote(uniqueId, blazeImage);
            } catch (Exception e) {
                BlazinBlazeTwitchMod.LOGGER.error("Failed to upload emote texture", e);
            }
        });
    }

    private static void uploadAnimatedTextureToMinecraft(String id, File[] images) {
        Minecraft.getInstance().execute(() -> {
            try {
                String uniqueId = id + "-" + emoteIdCounter.getAndIncrement();

                File firstFile = images[1];

                try (InputStream fileInputStream = new FileInputStream(firstFile)) {
                    NativeImage image = NativeImage.read(fileInputStream);

                    DynamicTexture texture = new DynamicTexture(() -> uniqueId, image);
                    Identifier textureId = Identifier.fromNamespaceAndPath(BlazinBlazeTwitchMod.MOD_ID, uniqueId);

                    Minecraft.getInstance().getTextureManager().register(textureId, texture);
                    texture.upload();

                    BlazeGif blazeGif = new BlazeGif(texture, id, images);

                    hud.setCurrentEmote(uniqueId, blazeGif);
                }
            } catch (Exception e) {
                BlazinBlazeTwitchMod.LOGGER.error("Failed to upload emote texture", e);
            }
        });
    }

    private static class EmoteTask {
        final String id;
        final String imageType;

        EmoteTask(String id, String imageType) {
            this.id = id;
            this.imageType = imageType;
        }
    }

    public static class ImageFrame {

        private final int delay;

        private final BufferedImage image;

        private final String disposal;

        private final int width, height;

        public ImageFrame(BufferedImage image, int delay, String disposal) {
            this.image = image;
            this.delay = delay;
            this.disposal = disposal;
            this.width = -1;
            this.height = -1;
        }

        public ImageFrame(BufferedImage image, int delay, String disposal, int width, int height) {
            this.image = image;
            this.delay = delay;
            this.disposal = disposal;
            this.width = width;
            this.height = height;
        }

        public ImageFrame(BufferedImage image) {
            this.image = image;
            this.delay = -1;
            this.disposal = null;
            this.width = -1;
            this.height = -1;
        }

        public BufferedImage getImage() {
            return image;
        }

        public int getDelay() {
            return delay;
        }

        public String getDisposal() {
            return disposal;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }

    private static class ImageMetaData {

        int index;

        int delay;

        String disposal;

        int x;

        int y;

        private ImageMetaData(int index, int delay, String disposal, int x, int y) {
            this.index = index;
            this.delay = delay;
            this.disposal = disposal;
            this.x = x;
            this.y = y;
        }
    }

}
