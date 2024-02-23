package test;

import org.junit.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Date;

/**
 * @author : kenny
 * @since : 2024/2/20
 **/
public class ThreadPoolTaskSchedulerTest {

    @Test
    public void test(){
        ThreadPoolTaskScheduler poolTaskScheduler = new ThreadPoolTaskScheduler();
        poolTaskScheduler.setPoolSize(20);
        poolTaskScheduler.setThreadNamePrefix("taskExecutor-");
        poolTaskScheduler.initialize();
        poolTaskScheduler.scheduleAtFixedRate(() -> {
            System.out.println("hello");
        }, new Date(System.currentTimeMillis() + 3000), 1000);


        while (true){}
    }
}
