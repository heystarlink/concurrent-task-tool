package com.concurrent.task.core.process;

import cn.hutool.core.util.IdUtil;
import com.concurrent.task.core.TaskExecutor;
import com.concurrent.task.core.stepchain.interceptor.Interceptor;
import com.concurrent.task.model.StepStrategy;
import com.concurrent.task.model.TaskContext;
import com.concurrent.task.model.enums.MatchOps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * 同步等待执行器
 * 步骤下所有的任务执行完毕后才会执行下一个步骤
 * @author : kenny
 * @since : 2024/2/20
 **/
public class AllOfStepWrapper extends AbstractConcurrentStepWrapper{
    private static final Logger logger = LogManager.getLogger(AllOfStepWrapper.class);

    private final AllOfStepProcessorWrapper allOfStepProcessorWrapper;

    public AllOfStepWrapper(StepProcessor stepProcessor,
                            List<Interceptor> stepInterceptors,
                            List<Interceptor> stepProcessorInterceptors) {
        this.matchOps = MatchOps.ALL;
        this.allOfStepProcessorWrapper = new AllOfStepProcessorWrapper(stepProcessor);
        this.stepProcessor = allOfStepProcessorWrapper;
        this.stepInterceptors = stepInterceptors;
        this.stepProcessorInterceptors = stepProcessorInterceptors;
    }


    @Override
    public Runnable generateScheduleTask(Map<String, Future<?>> futureMap, StepStrategy stepStrategy, TaskContext taskContext) {
        return () -> {
            if (super.checkScheduleStrategy(taskContext)) return;

            // 并发启动步骤任务
            int stepConcurrentCount = stepStrategy.getStepConcurrentCount();

            CompletableFuture<?>[] completableFutures = new CompletableFuture[stepConcurrentCount];
            for (int i = 0; i < stepConcurrentCount; i++){
                String uuid = IdUtil.fastUUID();
                completableFutures[i] = CompletableFuture.runAsync(generateStepTask(uuid, taskContext), TaskExecutor.TASK_EXECUTOR);
            }
            CompletableFuture.allOf(completableFutures).join();
        };
    }

    @Override
    protected void handleScheduleTaskCompletion() {
        stepTuple.getScheduledFuture().cancel(true);
    }
}
