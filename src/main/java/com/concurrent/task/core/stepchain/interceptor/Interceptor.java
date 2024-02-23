package com.concurrent.task.core.stepchain.interceptor;


import com.concurrent.task.model.TaskContext;
import com.concurrent.task.core.process.StepProcessor;

/**
 * 拦截器实现接口
 * @author : kenny
 * @since : 2024/2/20
 **/
public interface Interceptor {
    void before(Invocation invocation);
    Boolean intercept(Invocation invocation);
    void after(Invocation invocation);
    void onException(Invocation invocation, Exception ex);
    String getInterceptorName();

    interface Invocation{
        StepProcessor getStepProcessor();
        TaskContext getTaskContext();
        boolean invoke();
    }
}
