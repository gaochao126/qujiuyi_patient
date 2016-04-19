package com.jiuyi.qujiuyi.common.jobs;

import java.util.concurrent.TimeUnit;

/**
 * @Author: xutaoyang @Date: 上午11:39:59
 *
 * @Description
 *
 * @Copyright @ 2015 重庆玖壹健康管理有限公司
 */
public class JobContext {

	public final JobType jobType;
	public final Runnable runnable;
	public final int delay;
	public final int period;
	public final TimeUnit timeUnit;

	public JobContext(JobType jobType, Runnable runnable) {
		this.jobType = jobType;
		this.runnable = runnable;
		this.delay = 10;
		this.period = 10;
		this.timeUnit = TimeUnit.SECONDS;
	}

	public JobContext(JobType jobType, Runnable runnable, int delay, int period, TimeUnit timeUnit) {
		this.jobType = jobType;
		this.runnable = runnable;
		this.delay = delay;
		this.period = period;
		this.timeUnit = timeUnit;
	}

	public boolean runOnce() {
		return JobType.RUN_ONCE.equals(this.jobType);
	}
}
