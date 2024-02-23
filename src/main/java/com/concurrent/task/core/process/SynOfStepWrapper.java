package com.concurrent.task.core.process;

import com.concurrent.task.core.stepchain.interceptor.Interceptor;
import com.concurrent.task.core.stepchain.interceptor.InvocationChain;
import com.concurrent.task.exception.TaskException;
import com.concurrent.task.model.TaskContext;
import com.concurrent.task.model.enums.MatchOps;

import java.util.List;

/**
 * 同步步骤执行器
 * 只有一个线程执行步骤
 * @author : kenny
 * @since : 2024/2/23
 **/
public class SynOfStepWrapper extends StepWrapper{

    public SynOfStepWrapper(StepProcessor stepProcessor, List<Interceptor> stepInterceptors, List<Interceptor> stepProcessorInterceptors) {
        this.stepProcessor = stepProcessor;
        this.matchOps = MatchOps.SYN;
        this.stepInterceptors = stepInterceptors;
        this.stepProcessorInterceptors = stepProcessorInterceptors;
    }

    @Override
    public Boolean run(TaskContext taskContext) {
        InvocationChain realInterceptorChain = new InvocationChain(stepInterceptors, 0,
                (t) -> run(t,""),
                taskContext,
                stepProcessor);
        realInterceptorChain.invoke();
        return true;
    }

    private Boolean run(TaskContext taskContext, String x) {
        InvocationChain realInterceptorChain = new InvocationChain(stepProcessorInterceptors, 0,
                (tt) -> this.stepProcessor.run(tt),
                taskContext,
                stepProcessor);
        boolean p = realInterceptorChain.invoke();
        if(p){
            if(hasNext()) {
                next.run(taskContext);
            }
        }else {
            throw new TaskException("同步步骤任务执行失败");
        }
        return true;
    }
}
