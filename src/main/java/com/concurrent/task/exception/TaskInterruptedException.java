package com.concurrent.task.exception;

/**
 * @author : kenny
 * @since : 2024/2/20
 **/
public class TaskInterruptedException extends TaskException {
    public TaskInterruptedException() {
        super();
    }

    public TaskInterruptedException(String message) {
        super(message);
    }

    public TaskInterruptedException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskInterruptedException(Throwable cause) {
        super(cause);
    }

    public TaskInterruptedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}