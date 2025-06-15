package com.example.springpracticerestmvc.config;

import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executors;

@EnableAsync
@Configuration
public class TaskConfig {

    /**
     * Configures the application task executor bean.
     * <p>
     * This method defines a bean for the application task executor, which is used
     * to execute asynchronous tasks. It utilizes a virtual thread-per-task executor
     * introduced in Java, providing lightweight and efficient concurrency.
     *
     * @return an instance of AsyncTaskExecutor backed by a virtual thread-per-task executor
     */
    @Bean(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    public AsyncTaskExecutor asyncTaskExecutor() {
        return new TaskExecutorAdapter(
                Executors.newVirtualThreadPerTaskExecutor()
        );
    }
}
