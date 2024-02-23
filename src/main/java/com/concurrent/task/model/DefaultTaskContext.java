package com.concurrent.task.model;

import java.util.Date;

/**
 * @author : kenny
 * @since : 2024/2/20
 **/
public class DefaultTaskContext implements TaskContext {
    /**
     * 任务参数
     */
    private final TaskParam taskParam;

    public DefaultTaskContext(TaskParam taskParam) {
        this.taskParam = taskParam;
    }

    @Override
    public Long getTaskId() {
        return taskParam.getId();
    }

    @Override
    public TaskStrategy getTaskStrategy() {
        return taskParam.getTaskStrategy();
    }

    @Override
    public TaskParam getBizParam() {
        return taskParam;
    }

    @Override
    public Date getStartTime() {
        return taskParam.getTaskStrategy().getStartTime();
    }
}
