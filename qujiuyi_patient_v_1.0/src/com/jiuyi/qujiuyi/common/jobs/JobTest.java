package com.jiuyi.qujiuyi.common.jobs;

import java.util.concurrent.TimeUnit;

/**
 * @Author: xutaoyang @Date: 下午3:38:45
 *
 * @Description
 *
 * @Copyright @ 2015 重庆玖壹健康管理有限公司
 */
public class JobTest {

	public static void main(String[] args) {
		JobContext jobContext = new JobContext(JobType.SCHEDULED, new MyRunnable(), 1, 1, TimeUnit.SECONDS);
		JobService jobService = new JobService();
		jobService.submitJob(jobContext);

		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		jobService.destroy();
	}

	private static class MyRunnable implements Runnable {

		int index = 0;

		@Override
		public void run() {
			System.out.println("i won't love u any more" + (index++));
		}

	}

}
