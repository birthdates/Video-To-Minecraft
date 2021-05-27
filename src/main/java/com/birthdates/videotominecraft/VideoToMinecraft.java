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
    private final long FPS = 15; //FPS for canvas
    private final ScheduledThreadPoolExecutor executorService = new WrappedScheduledThreadPoolExecutor(Worker.WORKERS_PER_THREAD);

    public void onEnable() {
        instance = this;
        createDataFolderIfNotExists();
        registerCommands();
    }

    public void onDisable() {
        stopWorkers();
    }

    public void resizePool() {
        int workerCount = Worker.getWorkerCount();
        int neededThreads = workerCount / Worker.WORKERS_PER_THREAD;

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
        if (folder.exists() || folder.mkdir()) return;
        throw new IllegalStateException("Failed to create data folder.");
    }

    private void stopWorkers() {
        for (int i = Worker.getWorkers().size() - 1; i >= 0; --i) { //loop in reverse to prevent CME
            Worker worker = Worker.getWorkers().get(i);
            worker.finish();
        }
    }
}
