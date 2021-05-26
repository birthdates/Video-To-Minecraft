package com.birthdates.videotominecraft.worker;

import com.birthdates.videotominecraft.VideoToMinecraft;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Base worker class
 */
public abstract class Worker {

    public static final int WORKERS_PER_THREAD = 2;
    @Getter
    private static final List<Worker> workers = new ArrayList<>();
    @Getter
    @Nullable
    protected Object id;
    @Nullable
    protected Future<?> future;
    private boolean finished;

    protected void start() {
        workers.add(this);
        VideoToMinecraft.getInstance().resizePool();
    }

    public boolean finish() {
        if (finished) return false;
        finished = true;

        if (future != null)
            future.cancel(false);
        workers.remove(this);
        VideoToMinecraft.getInstance().resizePool();
        return true;
    }

    public static int getWorkerCount() {
        int output = 0;
        for (Worker worker : workers) {
            output += worker.getScore()*WORKERS_PER_THREAD;
        }
        return output;
    }

    public abstract int getScore();
}
