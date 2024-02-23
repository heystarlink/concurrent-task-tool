package com.concurrent.task.model;

import java.util.Date;

/**
 * 任务执行策略
 * @author : kenny
 * @since : 2024/2/20
 **/
public class TaskStrategy {
    /** 开始时间 */
    private Date startTime;
    /** 结束时间 */
    private Date endTime;

    public TaskStrategy(Date startTime) {
        this.startTime = startTime;
    }

    public TaskStrategy(Date startTime, Date endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
