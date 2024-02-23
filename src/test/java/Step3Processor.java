import com.concurrent.task.core.process.StepProcessor;
import com.concurrent.task.model.StepStrategy;
import com.concurrent.task.model.TaskContext;
import com.concurrent.task.util.BusinessMonitorUtil;

/**
 * @author : kenny
 * @since : 2024/2/24
 **/
public class Step3Processor implements StepProcessor {
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
        return new StepStrategy(1);
    }
}
