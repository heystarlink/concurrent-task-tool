package com.concurrent.task.model;

import com.concurrent.task.exception.TaskException;

import java.util.concurrent.TimeUnit;

/**
 * 步骤执行策略
 * @author : kenny
 * @since : 2024/2/20
 **/

public class StepStrategy {

    /**
     * 并发启动执行步骤数量
     */
    private int stepConcurrentCount;

    /**
     * 最大并发数量
     */
    private int maxConcurrentCount = Integer.MAX_VALUE;

    /**
     * 周期执行策略: 发起间隔多久后再次发起
     * period 间隔 单位毫秒 0为只执行一次
     */
    private Long period = 0L;

    /**
     * 步骤执行超时时间 默认30秒
     */
    private Long timeout = 30L;

    /**
     * 步骤单位
     */
    private TimeUnit timeoutUnit = TimeUnit.SECONDS;

    /**
     * 是否提前结束步骤
     */
    private boolean earlyStop;


    public StepStrategy(int stepConcurrentCount) {
        if(stepConcurrentCount < 1) {
            throw new TaskException("并发启动数量 必须大于等于1");
        }
        this.stepConcurrentCount = stepConcurrentCount;
    }

    public int getStepConcurrentCount() {
        return stepConcurrentCount;
    }

    public int getMaxConcurrentCount() {
        return maxConcurrentCount;
    }

    public Long getPeriod() {
        return period;
    }

    public void setPeriod(Long period) {
        this.period = period;
    }

    public void setStepConcurrentCount(int stepConcurrentCount) {
        this.stepConcurrentCount = stepConcurrentCount;
    }

    public void setMaxConcurrentCount(int maxConcurrentCount) {
        this.maxConcurrentCount = maxConcurrentCount;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public TimeUnit getTimeoutUnit() {
        return timeoutUnit;
    }

    public void setTimeoutUnit(TimeUnit timeoutUnit) {
        this.timeoutUnit = timeoutUnit;
    }
}
