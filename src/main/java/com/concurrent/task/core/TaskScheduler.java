package com.concurrent.task.core;

import com.concurrent.task.model.StepStrategy;
import com.concurrent.task.model.TaskContext;

import java.util.concurrent.ScheduledFuture;

/**
 * TODO 任务调度器部分待重构
 * @author : kenny
 * @since : 2024/2/24
 **/
public interface TaskScheduler {
    ScheduledFuture<?> scheduleTask(Runnable task, StepStrategy stepStrategy, TaskContext taskContext);
}
