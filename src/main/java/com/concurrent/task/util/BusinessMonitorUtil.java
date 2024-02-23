package com.concurrent.task.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

/**
 * @author : kenny
 * @since : 2023/2/24
 **/
public class BusinessMonitorUtil {
    private static final Logger logger = LogManager.getLogger(BusinessMonitorUtil.class);

    public static void performance() {
        Random random = new Random();
        int or = random.nextInt(1000);
        logger.info("Thread : " + Thread.currentThread().getName() + " Cpu Performance Time: " + or + "ms" + " 执行中");
        try {
            Thread.sleep(or);
        } catch (InterruptedException e) {
            logger.error("Thread : " + Thread.currentThread().getName() + " 被中止了", e);
        }
    }
}
