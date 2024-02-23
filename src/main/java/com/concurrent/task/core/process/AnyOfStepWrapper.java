package com.concurrent.task.core.process;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.concurrent.task.core.TaskExecutor;
import com.concurrent.task.core.stepchain.interceptor.Interceptor;
import com.concurrent.task.core.stepchain.interceptor.InvocationChain;
import com.concurrent.task.exception.TaskException;
import com.concurrent.task.exception.TaskInterruptedException;
import com.concurrent.task.model.StepStrategy;
import com.concurrent.task.model.TaskContext;
import com.concurrent.task.model.enums.MatchOps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Any模式并发执行器（互斥）
 * 执行任意一个步骤成功即可继续执行下一步骤
 *
 * @author : kenny
 * @since : 2024/2/20
 **/
public class AnyOfStepWrapper extends AbstractConcurrentStepWrapper {
    private static final Logger logger = LogManager.getLogger(AnyOfStepWrapper.class);

    public AnyOfStepWrapper(StepProcessor stepProcessor, List<Interceptor> stepInterceptors, List<Interceptor> stepProcessorInterceptors) {
        this.stepProcessor = stepProcessor;
        this.matchOps = MatchOps.ANY;
        this.stepInterceptors = stepInterceptors;
        this.stepProcessorInterceptors = stepProcessorInterceptors;
    }

    @Override
    public Runnable generateScheduleTask(Map<String, Future<?>> futureMap, StepStrategy stepStrategy, TaskContext taskContext) {
        return () -> {
            if (super.checkScheduleStrategy(taskContext)) return;

            // 并发启动步骤任务
            int stepConcurrentCount = stepStrategy.getStepConcurrentCount();
            for (int i = 0; i < stepConcurrentCount; i++){
                String uuid = IdUtil.fastUUID();
                Runnable runnable = generateStepTask(uuid, taskContext);
                Future<?> future = TaskExecutor.TASK_EXECUTOR.submit(runnable);
                futureMap.put(uuid, future);
            }
        };
    }

    @Override
    protected void handleScheduleTaskCompletion() {
        Map<String, Future<?>> concurrentStepFutureMap = stepTuple.getConcurrentStepFutureMap();
        // 如果没有第一个完成的步骤任务，则视为失败
        if (StringUtils.isEmpty(stepTuple.getFirstCompletedStepId())) {
            // 中断定时任务
            stepTuple.getScheduledFuture().cancel(true);
            // 中断步骤任务
            synchronized (concurrentStepFutureMap) {
                Set<Map.Entry<String, Future<?>>> entries = concurrentStepFutureMap.entrySet();
                for (Map.Entry<String, Future<?>> entry : entries) {
                    entry.getValue().cancel(true);
                }
            }
            // 记录错误日志
            logger.error(AnyOfStepWrapper.class.getName(), JSONUtil.toJsonStr(stepTuple));
            throw new TaskException("步骤任务执行超时!");
        } else {
            // 获取第一个完成的任务的 Future
            Future<?> firstFuture = getFutureWait(concurrentStepFutureMap, stepTuple.getFirstCompletedStepId(), 10);
            // 停止当前步骤的调度任务
            stepTuple.getScheduledFuture().cancel(false);

            // 扫尾工作
            synchronized (concurrentStepFutureMap) {
                Set<Map.Entry<String, Future<?>>> entries = concurrentStepFutureMap.entrySet();
                for (Map.Entry<String, Future<?>> entry : entries) {
                    if (!entry.getKey().equals(stepTuple.getFirstCompletedStepId())) {
                        entry.getValue().cancel(true);
                    }
                }
                // 等待线程执行完成
                try {
                    firstFuture.get();
                } catch (InterruptedException | ExecutionException e) {
                    logger.error(AnyOfStepWrapper.class.getName(), "执行线程意外终止", e);
                    Thread.currentThread().interrupt();
                    throw new TaskException(e);
                }
            }
        }
    }

    /**
     * 获取等待的Future
     * @param futureMap 存储步骤任务的Future的Map
     * @param firstFutureUUID 第一个完成的步骤任务的UUID
     * @param timeOut 超时时间
     * @return 第一个完成的步骤任务的Future
     */
    private Future<?> getFutureWait(Map<String, Future<?>> futureMap, String firstFutureUUID, Integer timeOut) {
        Future<?> future = futureMap.get(firstFutureUUID);
        if (future != null) {
            return future;
        }
        int count = timeOut * 10;
        for (int i = 0; i < count; i++) {
            future = futureMap.get(firstFutureUUID);
            if (future != null) {
                return future;
            }
        }
        throw new TaskException("Step获取Future失败" + firstFutureUUID);
    }
}
