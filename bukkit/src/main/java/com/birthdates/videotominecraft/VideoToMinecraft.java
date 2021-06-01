package com.birthdates.videotominecraft;

import com.birthdates.videotominecraft.command.watch.StopWatchingCommand;
import com.birthdates.videotominecraft.command.watch.WatchCommand;
import com.birthdates.videotominecraft.command.watch.WatchMovieCommand;
import com.birthdates.videotominecraft.configuration.Configuration;
import com.birthdates.videotominecraft.utils.WrappedScheduledThreadPoolExecutor;
import com.birthdates.videotominecraft.worker.Worker;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@Getter
public class VideoToMinecraft extends JavaPlugin {

    @Getter
    private static VideoToMinecraft instance;
    private Configuration configuration;

    public void onEnable() {
        checkForFFMPEG();
        instance = this;
        configuration = new Configuration();
        createDataFolderIfNotExists();
        registerCommands();
    }

    public void onDisable() {
        Worker.stopWorkers();
    }

    public void postToMainThread(Runnable task) {
        Bukkit.getScheduler().runTask(this, task);
    }

    private void registerCommands() {
        getCommand("watch").setExecutor(new WatchCommand());
        getCommand("stopwatching").setExecutor(new StopWatchingCommand());
        getCommand("watchmovie").setExecutor(new WatchMovieCommand());
    }

    /**
     * Faster access for the config FPS value
     *
     * @return The FPS value in the configuration file
     */
    public long getFPS() {
        return configuration.getFps();
    }

    private void createDataFolderIfNotExists() {
        File folder = getDataFolder();
        if (folder.exists() || folder.mkdir()) return;
        throw new IllegalStateException("Failed to create data folder.");
    }

    /**
     * Checks if the ffmpeg cli can be found in the path by executing it (it will throw an error if not found)
     */
    private void checkForFFMPEG() {
        try {
            Process process = Runtime.getRuntime().exec("ffmpeg");
            process.destroy(); //destroy if somehow, it's open
        } catch (IOException ignored) {
            throw new IllegalStateException("FFMPEG not in path!");
        }
    }
}
