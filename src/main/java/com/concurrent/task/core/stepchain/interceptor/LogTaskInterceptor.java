package com.concurrent.task.core.stepchain.interceptor;

/**
 * 任务执行日志拦截器
 * @author : kenny
 * @since : 2024/2/20
 **/
public class LogTaskInterceptor extends BaseInterceptor{
    @Override
    public String getInterceptorName() {
        return "任务：" + LogTaskInterceptor.class.getName();
    }
}
