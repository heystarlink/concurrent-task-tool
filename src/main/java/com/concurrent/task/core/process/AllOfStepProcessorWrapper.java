package com.concurrent.task.core.process;

import com.concurrent.task.model.TaskContext;
import com.concurrent.task.model.StepStrategy;

/**
 * @author : kenny
 * @since : 2024/2/21
 **/
public class AllOfStepProcessorWrapper implements StepProcessor{
    private final StepProcessor origin;

    public AllOfStepProcessorWrapper(StepProcessor origin) {
        this.origin = origin;
    }

    @Override
    public boolean run(TaskContext taskContext) {
        return origin.run(taskContext);
    }

    @Override
    public StepStrategy getStepStrategy(TaskContext taskContext) {
        return origin.getStepStrategy(taskContext);
    }

    public StepStrategy getOriginStepStrategy(TaskContext taskContext) {
        return origin.getStepStrategy(taskContext);
    }

    @Override
    public String getStepName() {
        return origin.getStepName();
    }
}
