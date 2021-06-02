package com.birthdates.videotominecraft.worker;

import com.birthdates.videotominecraft.utils.WrappedScheduledThreadPoolExecutor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Base worker class
 */
public abstract class Worker {

    private static final int WORKERS_PER_THREAD = 2;
    protected static final ScheduledThreadPoolExecutor executorService = new WrappedScheduledThreadPoolExecutor(Worker.WORKERS_PER_THREAD);
    @Getter
    private static final List<Worker> workers = new ArrayList<>();
    @Getter
    @Nullable
    protected Object id;
    @Nullable
    protected Future<?> future;
    private boolean finished;

    private static double getWorkersScore() {
        double output = 0;
        synchronized (workers) {
            for (Worker worker : workers) {
                output += worker.getScore();
            }
        }
        return output;
    }

    public static void stopWorkers() {
        synchronized (workers) {
            for (int i = workers.size() - 1; i >= 0; --i) { //loop in reverse to prevent CME
                Worker worker = workers.get(i);
                worker.finish();
            }
        }
        executorService.shutdown();
    }

    private static void resizePool() {
        double workersScore = getWorkersScore();
        int neededThreads = (int) (workersScore / Worker.WORKERS_PER_THREAD);

        if (neededThreads == 0 || executorService.getCorePoolSize() == neededThreads) return;
        executorService.setCorePoolSize(neededThreads);
    }

    protected void start() {
        synchronized (workers) {
            workers.add(this);
        }
        resizePool();
    }

    public boolean finish() {
        if (finished) return false;
        finished = true;

        if (future != null)
            future.cancel(false);
        synchronized (workers) {
            workers.remove(this);
        }
        resizePool();
        return true;
    }

    /**
     * Used for thread count
     *
     * @return The score for intensity (1 = 1 threads, 0.5 means a worker will only count has half a thread, thus needing two to complete 1 thread)
     */
    public abstract double getScore();
}
