package com.concurrent.task.core.process;

import com.concurrent.task.core.stepchain.interceptor.InvocationChain;
import com.concurrent.task.exception.TaskInterruptedException;
import com.concurrent.task.model.StepStrategy;
import com.concurrent.task.model.TaskContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

/**
 * 步骤并发执行器公共实现类
 * @author : kenny
 * @since : 2024/2/23
 **/
public abstract class AbstractConcurrentStepWrapper extends StepWrapper{
    private static final Logger logger = LogManager.getLogger(AbstractConcurrentStepWrapper.class);

    /**
     * 步骤并发执行器编排方法
     * @param taskContext
     * @return
     */
    @Override
    public Boolean run(TaskContext taskContext) {
        // 创建拦截器链
        InvocationChain invocationChain = new InvocationChain(stepInterceptors, 0,
                // 执行调度任务
                (t) -> runScheduleTask(taskContext),
                taskContext,
                stepProcessor);
        // 执行拦截器链
        invocationChain.invoke();

        // 处理定时任务完成后的操作
        handleScheduleTaskCompletion();
        return true;
    }

    /**
     * 调度任务执行逻辑
     * @param taskContext 任务上下文
     * @return 执行结果
     */
    private Boolean runScheduleTask(TaskContext taskContext) {
        // 检查任务执行策略
        if (super.checkScheduleStrategy(taskContext)){
            return false;
        }

        // 获取步骤执行策略
        StepStrategy stepStrategy = stepProcessor.getStepStrategy(taskContext);
        long period = stepStrategy.getPeriod();

        // 记录开启的线程后续用于销毁
        Map<String, Future<?>> futureMap = new ConcurrentHashMap<>();

        // 生成调度任务
        Runnable scheduleTask = generateScheduleTask(futureMap, stepStrategy, taskContext);

        ScheduledFuture<?> scheduledFuture;

        // 执行调度任务
        if (isFirstStep() && taskContext.getStartTime() != null) {
            if (period > 0) {
                // 在指定开始时间之后，以固定的速率调度，间隔为 period 毫秒
                scheduledFuture = stepScheduler.scheduleAtFixedRate(scheduleTask, taskContext.getStartTime(), stepStrategy.getPeriod());
            } else {
                // 在指定开始时间之后，调度执行一次
                scheduledFuture = stepScheduler.schedule(scheduleTask, taskContext.getStartTime());
            }
        } else {
            if (period > 0) {
                // 下一个调度任务将在 period 毫秒后运行，不论前一个步骤执行的状态如何
                scheduledFuture = stepScheduler.scheduleAtFixedRate(scheduleTask, stepStrategy.getPeriod());
            } else {
                // 立即执行调度任务一次
                scheduledFuture = stepScheduler.schedule(scheduleTask, Date.from(Instant.now()));
            }
        }

        // 获取第一个完成的步骤任务的UUID
        String firstCompletedStepId;
        try {
            firstCompletedStepId = noticeStopQueue.poll(stepStrategy.getTimeout(), stepStrategy.getTimeoutUnit());
        } catch (InterruptedException e) {
            scheduledFuture.cancel(true);
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        // 设置步骤元组的属性
        stepTuple.setFirstCompletedStepId(firstCompletedStepId);
        stepTuple.setConcurrentStepFutureMap(futureMap);
        stepTuple.setScheduledFuture(scheduledFuture);
        return true;
    }

    /**
     * 根据不同的子类实现创建调度任务
     * @param futureMap 存储步骤任务的Future的Map
     * @param stepStrategy 步骤执行策略
     * @param taskContext 任务上下文
     * @return 调度任务
     */
    public abstract Runnable generateScheduleTask(Map<String, Future<?>> futureMap, StepStrategy stepStrategy, TaskContext taskContext);

    /**
     * 步骤调度链路执行，step 1 -> step 2 -> step 3 ····
     * @param stepProcessorUUID
     * @param taskContext
     * @return
     */
    public Runnable generateStepTask(String stepProcessorUUID, TaskContext taskContext){
        return () -> {
            try {
                InvocationChain invocationChain = new InvocationChain(stepProcessorInterceptors, 0,
                        // 执行步骤处理器的逻辑
                        (t) -> this.stepProcessor.run(t),
                        taskContext,
                        stepProcessor);
                // 执行拦截器链
                boolean proceed = invocationChain.invoke();

                // 如果返回当前结果为true并且是第一个完成的就继续往下执行
                if (proceed && firstCompleted.compareAndSet(false, true) && noticeStopQueue.offer(stepProcessorUUID)){
                    if (this.hasNext()){
                        this.next.run(taskContext);
                    }
                }
            } catch (TaskInterruptedException taskInterruptedException) {
                // 如果是任务终止异常提前结束
                if(firstCompleted.compareAndSet(false, true) && noticeStopQueue.offer(stepProcessorUUID)) {
                    logger.error("收到任务提前终止异常" + taskInterruptedException.getMessage());
                    throw taskInterruptedException;
                }
            }
        };
    }

    /**
     * 处理定时任务完成后的操作
     */
    protected abstract void handleScheduleTaskCompletion();
}
