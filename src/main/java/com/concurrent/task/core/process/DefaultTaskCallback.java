package com.concurrent.task.core.process;

import cn.hutool.json.JSONUtil;
import com.concurrent.task.model.TaskContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author : kenny
 * @since : 2024/2/20
 **/
public class DefaultTaskCallback implements TaskCallback {
    private static final Logger logger = LogManager.getLogger(DefaultTaskCallback.class);
    @Override
    public void onSuccess(TaskContext context) {
        logger.info(String.format("默认任务回调，任务成功：%s, 任务ID: %s", JSONUtil.toJsonStr(context), context.getTaskId()));
    }

    @Override
    public void onFailure(TaskContext context, Throwable e) {
        logger.error(String.format("默认任务回调，任务失败：%s, 任务ID: %s, 失败原因: %s",
                JSONUtil.toJsonStr(context), context.getTaskId(), e));
    }

    @Override
    public void onFinally(TaskContext context) {
        logger.info(String.format("默认任务回调，最终执行：%s, 任务ID: %s", JSONUtil.toJsonStr(context), context.getTaskId()));
    }
}
