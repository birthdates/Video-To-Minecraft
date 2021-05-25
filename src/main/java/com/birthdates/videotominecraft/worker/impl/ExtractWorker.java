package com.birthdates.videotominecraft.worker.impl;

import com.birthdates.videotominecraft.VideoToMinecraft;
import com.birthdates.videotominecraft.maps.Maps;
import com.birthdates.videotominecraft.movie.Movie;
import com.birthdates.videotominecraft.worker.Worker;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
        future = VideoToMinecraft.getInstance().getExecutorService().submit(() -> {
            work(rotate);
            VideoToMinecraft.getInstance().postToMainThread(callback);
        });
    }

    public void work(boolean rotate) {
        //save all frames
        File outputDirFile = new File(outputDir);
        if (outputDirFile.exists()) {
            File[] files = outputDirFile.listFiles();
            if (files != null && files.length > 0) return; //already extracted this video
        }
        outputDirFile.mkdirs();

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
        super.finish();
    }

    private String scaleString(int res) {
        return res + ":" + res;
    }
}
