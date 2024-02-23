package com.concurrent.task.core.process;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

/**
 * 执行结果元组
 * @author : kenny
 * @since : 2024/2/20
 **/
@Data
public class StepTuple {
    /** 定时任务 */
    private ScheduledFuture<?> scheduledFuture;
    /** 第一个完成任务的线程编号 */
    private String firstCompletedStepId;
    /** 线程任务返回值列表 **/
    private Map<String, Future<?>> concurrentStepFutureMap;
}
