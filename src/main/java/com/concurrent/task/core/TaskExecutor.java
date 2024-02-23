package com.concurrent.task.core;

import com.concurrent.task.core.stepchain.interceptor.Interceptor;
import com.concurrent.task.core.stepchain.interceptor.InvocationChain;
import com.concurrent.task.core.stepchain.interceptor.LogStepProcessorInterceptor;
import com.concurrent.task.core.stepchain.interceptor.LogTaskInterceptor;
import com.concurrent.task.core.stepchain.interceptor.LogTaskStepInterceptor;
import com.concurrent.task.core.process.AllOfStepWrapper;
import com.concurrent.task.core.process.AnyOfStepWrapper;
import com.concurrent.task.core.process.DefaultTaskCallback;
import com.concurrent.task.core.process.StepProcessor;
import com.concurrent.task.core.process.StepWrapper;
import com.concurrent.task.core.process.SynOfStepWrapper;
import com.concurrent.task.core.process.TaskCallback;
import com.concurrent.task.exception.TaskException;
import com.concurrent.task.metric.TaskExecutorMetric;
import com.concurrent.task.model.DefaultTaskContext;
import com.concurrent.task.model.TaskContext;
import com.concurrent.task.model.TaskParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author : kenny
 * @since : 2024/2/19
 **/
public class TaskExecutor {
    private static final Logger logger = LogManager.getLogger(TaskExecutor.class);

    /** 定时任务调度线程池 */
    private ThreadPoolTaskScheduler SCHEDULER;

    /** 任务调度线程池 */
    public static ThreadPoolTaskExecutor TASK_EXECUTOR;

    /** 任务调度现场监控 **/
    public static TaskExecutorMetric metric = new TaskExecutorMetric();

    private final static int CORE_POOL_SIZE = 1200;
    private final static int MAX_POOL_SIZE = 1200;
    private final static int QUEUE_CAPACITY = 10000;
    /** 任务包装器，开始 */
    private StepWrapper startStepWrapper;
    /** 任务包装器，结束 */
    private StepWrapper endStepWrapper;
    /** 任务回调 */
    private TaskCallback callback;
    /** 任务日志拦截器列表 */
    private List<Interceptor> interceptors;

    static {
        TASK_EXECUTOR = new ThreadPoolTaskExecutor();
        // 核心线程数
        TASK_EXECUTOR.setCorePoolSize(CORE_POOL_SIZE);
        // 最大线程数
        TASK_EXECUTOR.setMaxPoolSize(MAX_POOL_SIZE);
        // 队列容量
        TASK_EXECUTOR.setQueueCapacity(QUEUE_CAPACITY);
        // 部分线程空闲最大存活时间
        TASK_EXECUTOR.setKeepAliveSeconds(60);
        // 线程池前缀名称
        TASK_EXECUTOR.setThreadNamePrefix("Concurrent-Task-Tool-");
        // 线程池对拒绝任务的处理策略
        TASK_EXECUTOR.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 初始化
        TASK_EXECUTOR.initialize();


    }

    public Long runAsync(TaskParam taskParam){
        TASK_EXECUTOR.submit(() -> {
           run(taskParam);
        });

        return taskParam.getId();
    }

    /**
     * 阻塞方式执行任务
     * @param taskParam
     * @return
     */
    public TaskContext run(TaskParam taskParam){

        if (taskParam == null || taskParam.getId() == null){
            throw new TaskException("任务参数不能为空");
        }

        TaskContext taskContext = new DefaultTaskContext(taskParam);
        logger.info(String.format("run -> 任务开始执行, 任务ID %s", taskContext.getTaskId()));
        try {
            InvocationChain invocationChain = new InvocationChain(interceptors, 0,
                    (t) -> startStepWrapper.run(t), taskContext);
            invocationChain.invoke();
            callback.onSuccess(taskContext);
        }catch (Exception ex){
            callback.onFailure(taskContext, ex);
        }finally {
            callback.onFinally(taskContext);
        }
        return taskContext;
    }

    private TaskExecutor(Builder builder) {
        this.SCHEDULER = builder.scheduler;
        this.callback = builder.callback;
        this.interceptors = builder.interceptors;
        this.startStepWrapper = builder.startStepWrapper;
    }

    public static class Builder{
        private final ThreadPoolTaskScheduler scheduler;
        private TaskCallback callback = new DefaultTaskCallback(); // 默认回调
        private final List<Interceptor> interceptors = Collections.singletonList(new LogTaskInterceptor()); // 任务日志拦截器
        private final List<Interceptor> stepInterceptors = Collections.singletonList(new LogTaskStepInterceptor()); // 步骤任务日志执行拦截器
        private final List<Interceptor> stepProcessorInterceptors = Collections.singletonList(new LogStepProcessorInterceptor()); // 步骤任务日志执行拦截器

        private StepWrapper startStepWrapper;
        private StepWrapper endStepWrapper;
        public Builder(ThreadPoolTaskScheduler scheduler) {
            this.scheduler = scheduler;
        }

        public Builder callback(TaskCallback callback) {
            this.callback = callback;
            return this;
        }

        public Builder startStepWrapper(StepWrapper stepWrapper) {
            this.startStepWrapper = stepWrapper;
            return this;
        }

        public Builder endStepWrapper(StepWrapper stepWrapper) {
            this.endStepWrapper = stepWrapper;
            return this;
        }

        public TaskExecutor build(){
            return new TaskExecutor(this);
        }

        /**
         * 任何一个步骤执行返回true后执行下一个步骤
         * @param stepProcessor
         * @return
         */
        public Builder anyOf(StepProcessor stepProcessor){
            addStepWrap(new AnyOfStepWrapper(stepProcessor, stepInterceptors, stepProcessorInterceptors));
            return this;
        }

        /**
         * 等所有任务执行完成后执行下一步骤
         * @param stepProcessor
         * @return
         */
        public Builder allOf(StepProcessor stepProcessor) {
            addStepWrap(new AllOfStepWrapper(stepProcessor, stepInterceptors, stepProcessorInterceptors));
            return this;
        }

        /**
         * 使用主线程完成所有任务
         * @param stepProcessor
         * @return
         */
        public Builder syncOf(StepProcessor stepProcessor) {
            addStepWrap(new SynOfStepWrapper(stepProcessor, stepInterceptors, stepProcessorInterceptors));
            return this;
        }

        public void addStepWrap(StepWrapper stepWrapper){
            stepWrapper.setScheduler(scheduler);
            if (startStepWrapper == null){
                stepWrapper.setFirstStep(true);
                startStepWrapper = stepWrapper;
            }
            if (endStepWrapper != null){
                StepWrapper preStepWrapper = this.endStepWrapper;
                preStepWrapper.setNext(stepWrapper);
            }
            this.endStepWrapper = stepWrapper;
        }

        public Builder addTaskInterceptor(Interceptor interceptor) {
            interceptors.add(interceptor);
            return this;
        }

        public Builder addStepInterceptor(Interceptor interceptor) {
            stepInterceptors.add(interceptor);
            return this;
        }
    }

}
