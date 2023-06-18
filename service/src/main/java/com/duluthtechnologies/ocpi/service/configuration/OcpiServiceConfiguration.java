package com.duluthtechnologies.ocpi.service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class OcpiServiceConfiguration {

	@Bean("service-task-executor")
	public TaskExecutor serviceTaskExecutor() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setAwaitTerminationSeconds(10);
		threadPoolTaskExecutor.setCorePoolSize(5);
		threadPoolTaskExecutor.setThreadNamePrefix("service-task-executor-");
		threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
		return threadPoolTaskExecutor;
	}

	@Bean("service-task-scheduler")
	public TaskScheduler serviceTaskScheduler() {
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setAwaitTerminationSeconds(10);
		threadPoolTaskScheduler.setPoolSize(5);
		threadPoolTaskScheduler.setThreadNamePrefix("service-task-scheduler-");
		threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true);
		return threadPoolTaskScheduler;
	}

	@Bean("emspSyncEnabled")
	public boolean emspSyncEnabled(OcpiServiceProperties ocpiServiceProperties) {
		return ocpiServiceProperties.getEmsp().getSync().isEnabled();
	}

	@Bean("emspSyncIntervalInSeconds")
	public Integer emspSyncIntervalInSeconds(OcpiServiceProperties ocpiServiceProperties) {
		return ocpiServiceProperties.getEmsp().getSync().getIntervalInSeconds();
	}

}
