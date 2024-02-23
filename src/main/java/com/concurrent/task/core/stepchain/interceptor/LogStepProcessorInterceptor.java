package com.concurrent.task.core.stepchain.interceptor;

/**
 * 步骤任务日志执行拦截器
 * @author : kenny
 * @since : 2024/2/20
 **/
public class LogStepProcessorInterceptor extends BaseInterceptor{
    @Override
    public String getInterceptorName() {
        return "步骤任务：" + LogStepProcessorInterceptor.class.getName(); // 或者你想要的其他名字
    }
}
