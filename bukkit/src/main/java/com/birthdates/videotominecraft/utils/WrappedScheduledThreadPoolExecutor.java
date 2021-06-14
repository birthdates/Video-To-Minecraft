package com.birthdates.videotominecraft.utils;

import com.birthdates.videotominecraft.VideoToMinecraft;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Level;

/**
 * This class is used to catch and alert errors in an executor
 */
public class WrappedScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {

    public WrappedScheduledThreadPoolExecutor(int corePoolSize) {
        super(corePoolSize);
    }

    @Override
    protected void afterExecute(Runnable runnable, Throwable throwable) {
        super.afterExecute(runnable, throwable);

        if (throwable != null && runnable instanceof Future<?>) {
            try {
                Future<?> future = (Future<?>) runnable;
                if (future.isDone())
                    future.get();
            } catch (Exception exception) {
                throwable = exception;
            }
        }

        if (throwable == null)
            return;
        VideoToMinecraft.getInstance().getLogger().log(Level.WARNING, "Failed to run tasks:");
        throwable.printStackTrace();
    }
}