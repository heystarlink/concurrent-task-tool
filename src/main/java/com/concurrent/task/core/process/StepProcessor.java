package com.concurrent.task.core.process;

import com.concurrent.task.model.StepStrategy;
import com.concurrent.task.model.TaskContext;

/**
 * @author : kenny
 * @since : 2024/2/20
 **/
public interface StepProcessor {
    /**
     * 步骤任务业务逻辑执行
     * @param taskContext 任务上下文
     * @return  true: 任务执行成功 false: 任务执行失败
     */
    boolean run(TaskContext taskContext);

    /**
     * 获取步骤名称
     * @return  步骤名称
     */
    default String getStepName() {
        return this.getClass().getSimpleName();
    }

    /**
     * 获取步骤执行策略，在并发执行之前执行
     * @param taskContext 任务上下文
     * @return 步骤执行策略
     */
    StepStrategy getStepStrategy(TaskContext taskContext);
}
