package com.birthdates.videotominecraft.worker.impl;

import com.birthdates.videotominecraft.VideoToMinecraft;
import com.birthdates.videotominecraft.utils.Compression;
import com.birthdates.videotominecraft.worker.Worker;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Frame worker that relays frames at a certain delay
 */
public class FrameWorker extends Worker {

    private final File folderFile;
    @Nullable
    private Consumer<byte[]> callback;
    @Nullable
    private Runnable onFinish;

    public FrameWorker(String folder) {
        this(null, folder);
    }

    public FrameWorker(@Nullable Object id, String folder) {
        this.id = id;
        folderFile = new File(folder);
    }

    public void start(Consumer<byte[]> callback, Runnable onFinish) {
        start(VideoToMinecraft.getInstance().getFPS(), true, callback, onFinish);
    }

    public void start(long fps, boolean decompress, Consumer<byte[]> callback, Runnable onFinish) {
        this.callback = callback;
        this.onFinish = onFinish;

        long delay = 1000L / fps; //how long between each frame (ms)
        AtomicInteger position = new AtomicInteger();
        int maxFrames = folderFile.listFiles().length; //how many frames were extracted

        //receive frames at delay rate
        future = VideoToMinecraft.getInstance().getExecutorService().scheduleAtFixedRate(() -> {
            int index = position.getAndIncrement();
            if (index >= maxFrames) {
                finish();
                return;
            }

            byte[] frame = readFrame(folderFile, index);
            if (frame == null) {
                return;
            }
            if (decompress) {
                frame = Compression.decompress(frame);
                if (frame == null)
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

    private byte[] readFrame(File folder, int position) {
        File file = new File(folder, position + 1 + ".jpeg");
        if (!file.exists()) return null;

        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public double getScore() {
        return 0.5D;
    }
}
