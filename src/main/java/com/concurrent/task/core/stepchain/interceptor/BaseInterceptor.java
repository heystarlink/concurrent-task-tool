package com.concurrent.task.core.stepchain.interceptor;

import cn.hutool.json.JSONUtil;
import com.concurrent.task.util.TimeLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author : kenny
 * @since : 2024/2/22
 **/
public abstract class BaseInterceptor implements Interceptor{
    private static final Logger logger = LogManager.getLogger(BaseInterceptor.class);

    @Override
    public void before(Invocation invocation) {
        TimeLogger.startTimer();
        /*logger.info(String.format(getInterceptorName() + " 开始执行，threadName: %s, stepName: %s, taskContext: %s",
                Thread.currentThread().getName(), getStepName(invocation), JSONUtil.toJsonStr(invocation.getTaskContext())));*/
    }

    @Override
    public Boolean intercept(Invocation invocation) {
        try {
            before(invocation);
            boolean result = invocation.invoke();
            after(invocation);
            return result;
        } catch (Exception ex) {
            onException(invocation, ex);
            throw ex;
        }
    }

    @Override
    public void after(Invocation invocation) {
        /*logger.info(String.format(getInterceptorName() + " 执行结束，threadName: %s, stepName: %s, taskContext: %s",
                Thread.currentThread().getName(), getStepName(invocation), JSONUtil.toJsonStr(invocation.getTaskContext())));*/
    }

    @Override
    public void onException(Invocation invocation, Exception ex) {
        /*logger.error(String.format(getInterceptorName() + " 执行异常，threadName: %s, stepName: %s, taskContext: %s",
                Thread.currentThread().getName(), getStepName(invocation), JSONUtil.toJsonStr(invocation.getTaskContext())));*/
    }

    private String getStepName(Invocation invocation){
        if (invocation.getStepProcessor() != null) {
            return invocation.getStepProcessor().getStepName();
        }

        return "";
    }
}
