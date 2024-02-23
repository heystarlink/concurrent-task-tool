package com.concurrent.task.model;

import java.util.Date;

/**
 * @author : kenny
 * @since : 2024/2/20
 **/
public interface TaskContext {
    /**
     * 获取任务ID
     * @return
     */
    Long getTaskId();

    /**
     * 获取任务策略
     * @return
     */
    TaskStrategy getTaskStrategy();

    /**
     * 获取任务参数
     */
    TaskParam getBizParam();

    /**
     * 获取任务开始时间
     * @return
     */
    Date getStartTime();
}
