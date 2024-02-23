package com.concurrent.task.core.process;

import com.concurrent.task.model.TaskContext;

/**
 * @author : kenny
 * @since : 2024/2/20
 **/
public interface TaskCallback {
    /**
     * 任务执行成功后回调
     * @param context
     */
    void onSuccess(TaskContext context);

    /**
     * 任务执行失败后回调
     * @param context
     * @param e
     */
    void onFailure(TaskContext context, Throwable e);

    /**
     * 任务最终执行后回调
     * @param context
     */
    void onFinally(TaskContext context);
}
