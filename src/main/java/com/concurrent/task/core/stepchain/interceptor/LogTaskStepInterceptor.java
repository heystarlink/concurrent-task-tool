package com.concurrent.task.core.stepchain.interceptor;

/**
 * @author : kenny
 * @since : 2024/2/20
 **/
public class LogTaskStepInterceptor extends BaseInterceptor{
    @Override
    public String getInterceptorName() {
        return "步骤：" + LogTaskStepInterceptor.class.getName();
    }
}
