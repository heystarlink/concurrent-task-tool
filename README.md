# Concurrent Task Tool

`并发任务编排`

## 🤔 总体思路

### 概念:

1. **任务 (Task)**: 代表整个执行流程，包含多个步骤。
2. **步骤 (Step)**: 表示具体的业务执行逻辑，一个步骤可以是同步执行，也可以是异步执行。

### 流程:

1. 定义步骤任务。
2. 编排步骤任务，同时指定并发策略。

```java
// 1. 定义任务步骤
StepProcessor stepProcessor1 = new Step1Processor();
StepProcessor stepProcessor2 = new Step2Processor();
StepProcessor stepProcessor3 = new Step3Processor();

// 2. 编排你的步骤，指定并发策略
TaskExecutor taskExecutor = new TaskExecutor.Builder(threadPoolTaskScheduler)
        .callback(new DefaultTaskCallback())
        .anyOf(stepProcessor1)
        .allOf(stepProcessor2)
        .syncOf(stepProcessor3)
        .build();
```

### 并发策略:

1. **AnyOf**: 步骤中任意任务完成后执行下一个步骤。
2. **AllOf**: 步骤中所有任务完成后执行下一个步骤。
3. **SyncOf**: 使用同一线程完成所有步骤。

### 相关组件:

1. **拦截器 (Interceptor)**: 拦截操作。

2. **步骤包装器 (StepWrapper)**: 步骤的装饰器。
   
   - AbstractConcurrentStepWrapper
   - AnyOfStepWrapper
   - AllOfStepWrapper
   - SynOfStepWrapper
   
3. **步骤处理器 (StepProcessor)**: 处理任务处理。

4. **任务执行器 (TaskExecutor)**: 执行任务。
   - 任务周期调度线程池。
   - 任务执行线程池。
   - 任务执行线程监控。
   
5. **任务回调 (TaskCallback)**: 处理任务回调。

6. **步骤策略 (StepStrategy)**: 指定并发执行数量、周期性调度策略。

   

使用示例可参考 `TaskExecutorTest`。

如有需要，欢迎调整术语以适应您的偏好或特定领域术语。如需进一步帮助，请随时告知！
