package com.birthdates.videotominecraft.worker.impl;

import com.birthdates.videotominecraft.VideoToMinecraft;
import com.birthdates.videotominecraft.worker.Worker;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Frame worker that relays frames to a {@link Consumer<BufferedImage>} at a certain delay
 */
public class FrameWorker extends Worker {

    private final File folderFile;
    @Nullable
    private Consumer<BufferedImage> callback;
    @Nullable
    private Runnable onFinish;

    public FrameWorker(String folder) {
        this(null, folder);
    }

    public FrameWorker(@Nullable Object id, String folder) {
        this.id = id;
        folderFile = new File(folder);
    }

    public void start(Consumer<BufferedImage> callback, Runnable onFinish) {
        start(1, callback, onFinish);
    }

    public void start(int cacheSize, Consumer<BufferedImage> callback, Runnable onFinish) {
        this.callback = callback;
        this.onFinish = onFinish;

        long delay = 1000L / ((long) VideoToMinecraft.getInstance().getFPS() * cacheSize); //how long between each frame (ms)
        AtomicInteger position = new AtomicInteger();
        int maxFrames = folderFile.listFiles().length; //how many frames were extracted

        //receive frames at delay rate
        future = VideoToMinecraft.getInstance().getExecutorService().scheduleAtFixedRate(() -> {
            int index;
            if ((index = position.getAndIncrement()) >= maxFrames) {
                finish();
                return;
            }

            BufferedImage frame = readFrame(folderFile, index);
            if (frame == null) {
                return;
            }

            callback.accept(frame);
        }, delay, delay, TimeUnit.MILLISECONDS);
        super.start();
    }


    public boolean finish() {
        if (!super.finish()) return false;
        if (onFinish != null) onFinish.run();
        if (callback != null) callback.accept(null);
        return true;
    }

    private BufferedImage readFrame(File folder, int position) {
        File file = new File(folder, position + 1 + ".jpeg");
        if (!file.exists()) return null;

        try {
            return ImageIO.read(file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }
}
