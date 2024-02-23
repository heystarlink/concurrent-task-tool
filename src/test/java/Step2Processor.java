import com.concurrent.task.core.process.StepProcessor;
import com.concurrent.task.model.StepStrategy;
import com.concurrent.task.model.TaskContext;
import com.concurrent.task.util.BusinessMonitorUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Step2Processor implements StepProcessor {
    private static Logger logger = LogManager.getLogger(Step2Processor.class);

    @Override
    public boolean run(TaskContext taskContext) {
        try {
            BusinessMonitorUtil.performance();
        }catch (Exception ex){
            return false;
        }
        return true;
    }

    @Override
    public StepStrategy getStepStrategy(TaskContext taskContext) {
        StepStrategy stepStrategy = new StepStrategy(3);
        stepStrategy.setPeriod(200L);
        return stepStrategy;
    }
}
