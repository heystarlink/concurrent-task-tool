package com.concurrent.task.model;

import com.concurrent.task.util.SnowflakeIdUtil;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author : kenny
 * @since : 2024/2/20
 **/
public class TaskParam extends HashMap<String, Object> implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 任务id 默认雪花id */
    private Long id = SnowflakeIdUtil.nextId();

    /** 任务策略 */
    private TaskStrategy taskStrategy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public TaskStrategy getTaskStrategy() {
        return taskStrategy;
    }

    public void setTaskStrategy(TaskStrategy taskStrategy) {
        this.taskStrategy = taskStrategy;
    }
}
