package com.jiuyi.qujiuyi.common.jobs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import javax.annotation.PreDestroy;

import org.springframework.stereotype.Service;

/**
 * @Author: xutaoyang @Date: 上午11:40:17
 *
 * @Description
 *
 * @Copyright @ 2015 重庆玖壹健康管理有限公司
 */
@Service
public class JobService {

	private ExecutorService executor = Executors.newFixedThreadPool(4, new DaemonThreadFactory());
	private ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(4, new DaemonThreadFactory());

	@PreDestroy
	public void destroy() {
		if (!executor.isShutdown()) {
			this.executor.shutdown();
		}
		if (!scheduledExecutor.isShutdown()) {
			this.scheduledExecutor.shutdown();
		}
	}

	public void submitJob(JobContext jobContext) {
		if (jobContext.runnable == null) {
			return;
		}
		if (jobContext.runOnce()) {
			executor.submit(jobContext.runnable);
		} else {
			scheduledExecutor.scheduleWithFixedDelay(jobContext.runnable, jobContext.delay, jobContext.period, jobContext.timeUnit);
		}
	}

	private class DaemonThreadFactory implements ThreadFactory {

		@Override
		public Thread newThread(Runnable r) {
			Thread thread = new Thread(r);
			thread.setDaemon(true);
			return thread;
		}

	}

}
