package com.chinatower.gis.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.*;

@Slf4j
public class ClueThreadPool {

    private static final int DEFAULT_MAX_CONCURRENT = Runtime.getRuntime().availableProcessors() * 2;

    private static final String THREAD_POOL_NAME = "ClueThreadPool-%d";

    private static final ThreadFactory FACTORY = new BasicThreadFactory.Builder().namingPattern(THREAD_POOL_NAME)
            .daemon(true).build();

    private static final int DEFAULT_SIZE = 300;

    private static final long DEFAULT_KEEP_ALIVE = 60L;

    private static ExecutorService executor;

    private static BlockingQueue<Runnable> executeQueue = new ArrayBlockingQueue<>(DEFAULT_SIZE);

    static {
        try {
            executor = new ThreadPoolExecutor(DEFAULT_MAX_CONCURRENT, DEFAULT_MAX_CONCURRENT + 2, DEFAULT_KEEP_ALIVE,
                    TimeUnit.SECONDS, executeQueue, FACTORY, new ThreadPoolExecutor.CallerRunsPolicy());

            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    log.info("ClueThreadPool shutting down.");
                    executor.shutdown();

                    try {
                        if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                            log.error("ClueThreadPool shutdown immediately due to wait timeout.");
                            executor.shutdownNow();
                        }
                    } catch (InterruptedException e) {
                        log.error("ClueThreadPool shutdown interrupted.");
                        Thread.currentThread().interrupt();
                    }

                    log.info("ClueThreadPool shutdown complete.");
                }
            }));
        } catch (Exception e) {
            log.error("ClueThreadPool init error.", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    private ClueThreadPool() {
    }

    public static boolean execute(Runnable task) {

        try {
            executor.execute(task);
        } catch (RejectedExecutionException e) {
            log.error("Task executing was rejected.", e);
            return false;
        }

        return true;
    }

    public static <T> Future<T> submitTask(Callable<T> task) {

        try {
            return executor.submit(task);
        } catch (RejectedExecutionException e) {
            log.error("Task executing was rejected.", e);
            throw new UnsupportedOperationException("Unable to submit the task, rejected.", e);
        }
    }
}
