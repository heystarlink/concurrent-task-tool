package com.concurrent.task.util;

import cn.hutool.json.JSONUtil;
import com.concurrent.task.core.stepchain.interceptor.Interceptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author : kenny
 * @since : 2024/2/22
 **/
public class TimeLogger {
    private static final Logger logger = LogManager.getLogger(TimeLogger.class);

    private static long startTime; // 记录开始时间

    public static void startTimer() {
        startTime = System.nanoTime(); // 记录开始时间
    }

    public static void logExecutionTime(Interceptor.Invocation invocation) {
        long elapsedTime = System.nanoTime() - startTime; // 计算执行时间
        logger.info(String.format("任务执行时间，threadName: %s, stepName: %s, taskContext: %s, elapsedTime: %d ns",
                Thread.currentThread().getName(), invocation.getStepProcessor().getStepName(), JSONUtil.toJsonStr(invocation.getTaskContext()), elapsedTime));
    }
}
