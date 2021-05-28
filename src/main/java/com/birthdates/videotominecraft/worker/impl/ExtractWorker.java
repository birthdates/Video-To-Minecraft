package com.birthdates.videotominecraft.worker.impl;

import com.birthdates.videotominecraft.VideoToMinecraft;
import com.birthdates.videotominecraft.compression.Compression;
import com.birthdates.videotominecraft.maps.Maps;
import com.birthdates.videotominecraft.movie.Movie;
import com.birthdates.videotominecraft.worker.Worker;
import org.bukkit.map.MapPalette;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Extraction worker that extracts frames from a video using ffmpeg
 */
public class ExtractWorker extends Worker {

    //q:v 1 is the video quality (1-31) 1 being the highest
    private static final String ffmpegExtractCommand = "ffmpeg -i \"{0}\" -vf \"fps={1},{2}\" -q:v 1 \"{3}/%d.jpeg\"";

    private final File file;
    private final String outputDir;

    public ExtractWorker(File file, String outputDir) {
        this.file = file;
        this.outputDir = outputDir;
    }

    public void start(boolean rotate, Runnable callback) {
        super.start();
        future = VideoToMinecraft.getInstance().getExecutorService().submit(() -> work(rotate, () -> VideoToMinecraft.getInstance().postToMainThread(callback)));
    }

    public void work(boolean rotate, Runnable callback) {
        //save all frames
        File outputDirFile = new File(outputDir);
        if (outputDirFile.exists()) {
            File[] files = outputDirFile.listFiles();
            if (files != null && files.length > 0) {
                callback.run();
                return; //already extracted this video
            }
        } else if (!outputDirFile.mkdirs()) {
            throw new IllegalStateException("Failed to create folder \"" + outputDir + "\"");
        }

        int mapRes = Maps.getResolution();
        int movieRes = mapRes * Movie.GRID_SIZE;

        //format command
        String command = ffmpegExtractCommand
                .replace("{0}", file.getAbsolutePath()) //input
                .replace("{1}", String.valueOf(VideoToMinecraft.getInstance().getFPS()))
                .replace("{2}", rotate ?
                        "rotate=PI+(PI/2),scale=" + scaleString(movieRes)
                        : "scale=" + scaleString(mapRes) + ",setsar=1:1") //do calculations in ffmpeg instead of live
                .replace("{3}", outputDir);
        //run command & wait
        Process process;
        try {
            process = Runtime.getRuntime().exec(command);
            if (!process.waitFor(120, TimeUnit.SECONDS)) //this can affect the length of the video as it will stop the process
                process.destroy();
        } catch (IOException | InterruptedException exception) {
            exception.printStackTrace();
        }

        transformToMinecraftColors(callback);
    }

    /**
     * Transform the ffmpeg images into Minecraft colors to save a lot of performance (each transformation takes ~10-15 ms)
     *
     * @param callback Target callback
     */
    private void transformToMinecraftColors(Runnable callback) {
        FrameWorker frameWorker = new FrameWorker(outputDir);
        AtomicInteger number = new AtomicInteger(1);
        try {
            //start reading all the ffmpeg generated frames at 1000 FPS (1ms delay)
            frameWorker.start(1000L, false, (bytes) -> {
                if (bytes == null)
                    return;

                BufferedImage image = null;
                try {
                    image = ImageIO.read(new ByteArrayInputStream(bytes));
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
                if (image == null)
                    return;

                //imageToBytes not only converts the image to bytes, it also changes the colors to the nearest minecraft color
                byte[] imageBytes = MapPalette.imageToBytes(image);
                Path path = new File(outputDir + number.getAndIncrement() + ".jpeg").toPath();
                try {
                    Files.write(path, Compression.compress(imageBytes));
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }, () -> {
                callback.run();
                super.finish();
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private String scaleString(int res) {
        return res + ":" + res;
    }

    public double getScore() {
        return 2D;
    }
}
