package com.concurrent.task.metric;

import com.concurrent.task.core.TaskExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author : kenny
 * @since : 2024/2/23
 **/
public class TaskExecutorMetric {
    private static final Logger logger = LogManager.getLogger(TaskExecutorMetric.class);
    public static ThreadPoolTaskScheduler METRIC_SCHEDULER;
    static {
        METRIC_SCHEDULER= new ThreadPoolTaskScheduler();
        METRIC_SCHEDULER.setPoolSize(1);
        METRIC_SCHEDULER.initialize();
        METRIC_SCHEDULER.scheduleAtFixedRate(() -> {
            try {
                ThreadPoolExecutor threadPoolExecutor = TaskExecutor.TASK_EXECUTOR.getThreadPoolExecutor();
                logger.debug(String.format("task.thread.pool.core.size: %s", threadPoolExecutor.getCorePoolSize()));
                logger.debug(String.format("task.thread.pool.largest.size: %s", threadPoolExecutor.getLargestPoolSize()));
                logger.debug(String.format("task.thread.pool.max.size: %s", threadPoolExecutor.getMaximumPoolSize()));
                logger.debug(String.format("task.thread.pool.active.size: %s", threadPoolExecutor.getActiveCount()));
                logger.debug(String.format("task.thread.pool.thread.count: %s", threadPoolExecutor.getPoolSize()));
                logger.debug(String.format("task.thread.pool.queue.size: %s", threadPoolExecutor.getQueue().size()));
            } catch (Exception e) {
                logger.error("TaskExecutorMetric error", e);
            }
        }, 3000);
    }
}
