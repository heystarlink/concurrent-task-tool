package com.concurrent.task.core.stepchain.interceptor;


import com.concurrent.task.model.TaskContext;
import com.concurrent.task.core.process.StepProcessor;
import java.util.List;
import java.util.function.Function;

/**
 * @author : kenny
 * @since : 2024/2/20
 **/
public class InvocationChain implements Interceptor.Invocation{
    private final List<Interceptor> interceptors;
    private final int index;
    private final Function<TaskContext, Boolean> runBiz;
    private final TaskContext taskContext;
    private StepProcessor stepProcessor;

    /**
     * 任务拦截器构造
     * @param interceptors
     * @param index
     * @param runBiz
     * @param taskContext
     */
    public InvocationChain(
            List<Interceptor> interceptors, int index, Function<TaskContext, Boolean> runBiz,
            TaskContext taskContext) {
        this.interceptors = interceptors;
        this.index = index;
        this.runBiz = runBiz;
        this.taskContext = taskContext;
    }

    /**
     * 步骤任务拦截器构造
     * @param interceptors
     * @param index
     * @param runBiz
     * @param taskContext
     * @param stepProcessor
     */
    public InvocationChain(
            List<Interceptor> interceptors, int index, Function<TaskContext, Boolean> runBiz,
            TaskContext taskContext,
            StepProcessor stepProcessor) {
        this.interceptors = interceptors;
        this.index = index;
        this.runBiz = runBiz;
        this.taskContext = taskContext;
        this.stepProcessor = stepProcessor;
    }

    @Override
    public StepProcessor getStepProcessor() {
        return this.stepProcessor;
    }

    @Override
    public TaskContext getTaskContext() {
        return this.taskContext;
    }

    @Override
    public boolean invoke() {
        if (index >= interceptors.size()){
            // 业务逻辑
            return runBiz.apply(taskContext);
        }else {
            Interceptor interceptor = interceptors.get(index);
            InvocationChain invocationChain = new InvocationChain(interceptors, index + 1, runBiz, taskContext, stepProcessor);
            return interceptor.intercept(invocationChain);
        }
    }
}
