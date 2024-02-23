import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.concurrent.task.core.TaskExecutor;
import com.concurrent.task.core.process.DefaultTaskCallback;
import com.concurrent.task.core.process.StepProcessor;
import com.concurrent.task.model.TaskParam;
import com.concurrent.task.model.TaskStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @author : kenny
 * @since : 2024/2/20
 **/
public class TaskExecutorTest {
    private static Logger logger = LogManager.getLogger(TaskExecutorTest.class);

    @Test
    public void run(){
        // 默认定时执行器
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(20);
        threadPoolTaskScheduler.setThreadNamePrefix("taskExecutor-");
        threadPoolTaskScheduler.initialize();
        //1. 各环节执行任务
        StepProcessor stepProcessor1 = new Step1Processor();
        StepProcessor stepProcessor2 = new Step2Processor();
        StepProcessor stepProcessor3 = new Step3Processor();

        //2. 任务编制
        TaskExecutor taskExecutor = new TaskExecutor.Builder(threadPoolTaskScheduler)
                .callback(new DefaultTaskCallback())
                .anyOf(stepProcessor1)
                .allOf(stepProcessor2)
                .syncOf(stepProcessor3)
                .build();

        //3. 任务调用，参数设置
        TaskParam taskParam = new TaskParam();
        TaskStrategy taskStrategy = new TaskStrategy(DateUtil.offset(DateUtil.date(), DateField.MILLISECOND,50));
        taskStrategy.setEndTime(DateUtil.offset(DateUtil.date(), DateField.MILLISECOND,500000));
        taskParam.setTaskStrategy(taskStrategy);
        logger.info(String.format("taskStrategy:%s%n", JSONUtil.toJsonStr(taskStrategy)));
        taskExecutor.run(taskParam);
    }
}
