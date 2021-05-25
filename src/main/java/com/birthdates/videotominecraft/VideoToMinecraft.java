package com.birthdates.videotominecraft;

import com.birthdates.videotominecraft.command.watch.StopWatchingCommand;
import com.birthdates.videotominecraft.command.watch.WatchCommand;
import com.birthdates.videotominecraft.command.watch.WatchMovieCommand;
import com.birthdates.videotominecraft.executor.WrappedScheduledThreadPoolExecutor;
import com.birthdates.videotominecraft.worker.Worker;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@Getter
public class VideoToMinecraft extends JavaPlugin {

    @Getter
    private static VideoToMinecraft instance;
    private final int FPS = 20; //FPS for canvas
    private final int WORKERS_PER_THREAD = 1;
    private final ScheduledThreadPoolExecutor executorService = new WrappedScheduledThreadPoolExecutor(WORKERS_PER_THREAD);

    public void onEnable() {
        instance = this;
        createDataFolderIfNotExists();
        registerCommands();
    }

    public void onDisable() {
        for (int i = Worker.getWorkers().size() - 1; i >= 0; i--) { //loop in reverse to prevent CME
            Worker worker = Worker.getWorkers().get(i);
            worker.finish();
        }
    }

    public void resizePool() {
        int workerCount = Worker.getWorkers().size();
        int neededThreads = workerCount / WORKERS_PER_THREAD;

        if (neededThreads == 0 || executorService.getCorePoolSize() == neededThreads) return;
        executorService.setCorePoolSize(neededThreads);
    }

    public void postToMainThread(Runnable task) {
        Bukkit.getScheduler().runTask(this, task);
    }

    private void registerCommands() {
        getCommand("watch").setExecutor(new WatchCommand());
        getCommand("stopwatching").setExecutor(new StopWatchingCommand());
        getCommand("watchmovie").setExecutor(new WatchMovieCommand());
    }

    private void createDataFolderIfNotExists() {
        File folder = getDataFolder();
        if(folder.exists() || folder.mkdir()) return;
        throw new IllegalStateException("Failed to create data folder.");
    }
}
