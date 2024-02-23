package com.concurrent.task.core.process;

import cn.hutool.core.date.DateUtil;
import com.concurrent.task.core.stepchain.interceptor.Interceptor;
import com.concurrent.task.model.TaskContext;
import com.concurrent.task.model.TaskStrategy;
import com.concurrent.task.model.enums.MatchOps;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 步骤并发执行器抽象类
 *
 * @author : kenny
 * @since : 2024/2/20
 **/
public abstract class StepWrapper {
    private static final Logger logger = LogManager.getLogger(StepWrapper.class);

    /**
     * 是否为第一个步骤
     */
    protected boolean isFirstStep;

    /**
     * 执行器模式
     */
    protected MatchOps matchOps;
    /**
     * 定时器
     */
    protected ThreadPoolTaskScheduler stepScheduler;
    /**
     * 任务处理器
     */
    protected StepProcessor stepProcessor;
    /**
     * 步骤执行拦截器
     */
    @Getter
    protected List<Interceptor> stepInterceptors;
    /**
     * 步骤任务子线程拦截器
     */
    protected List<Interceptor> stepProcessorInterceptors;
    /**
     * 下一个步骤执行装饰器
     */
    protected StepWrapper next;

    /**
     * 通知队列
     */
    protected final LinkedBlockingQueue<String> noticeStopQueue = new LinkedBlockingQueue<>(1);

    /**
     * 记录是否为第一个完成的任务
     */
    protected volatile AtomicBoolean firstCompleted = new AtomicBoolean(false);

    /**
     * 执行元祖数据
     */
    protected volatile StepTuple stepTuple = new StepTuple();

    /**
     * 异步执行调度包装
     *
     * @param taskContext
     * @return
     */
    public abstract Boolean run(TaskContext taskContext);

    public boolean isFirstStep() {
        return this.isFirstStep;
    }

    public boolean hasNext() {
        return this.next != null;
    }

    public void setScheduler(ThreadPoolTaskScheduler stepScheduler) {
        this.stepScheduler = stepScheduler;
    }

    public void setFirstStep(boolean isFirstStep) {
        this.isFirstStep = isFirstStep;
    }

    public void setNext(StepWrapper next) {
        this.next = next;
    }

    public void setStepInterceptors(List<Interceptor> stepInterceptors) {
        this.stepInterceptors = stepInterceptors;
    }

    public void setStepProcessor(StepProcessor stepProcessor) {
        this.stepProcessor = stepProcessor;
    }

    /**
     * 检查任务策略
     */
    public boolean checkScheduleStrategy(TaskContext taskContext) {
        TaskStrategy taskStrategy = taskContext.getTaskStrategy();
        Date endTime = taskStrategy.getEndTime();
        if (endTime != null && DateUtil.date().compareTo(endTime) > 0) {
            logger.error("执行调度任务异常，当前时间大于任务策略的结束时间" + DateUtil.formatDateTime(endTime));
            return true;
        }
        return false;
    }

}
