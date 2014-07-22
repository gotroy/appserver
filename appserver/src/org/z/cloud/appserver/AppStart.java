package org.z.cloud.appserver;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.z.cloud.appserver.module.ModuleDemo;
import org.z.cloud.appserver.module.Processor;
import org.z.cloud.common.module.ModuleFactory;
import org.z.cloud.common.processor.Request;
import org.z.cloud.common.processor.Response;
import org.z.cloud.scheduler.job.CloudJob;
import org.z.cloud.scheduler.job.CloudJob.JobType;
import org.z.cloud.scheduler.job.CloudJobFactory;
import org.z.cloud.scheduler.job.TestJob;
import org.z.cloud.scheduler.module.ClientImpl;
import org.z.cloud.scheduler.module.ServerImpl;

public class AppStart {

	public static final Logger logger = LoggerFactory.getLogger(AppStart.class);

	private static long begin = System.currentTimeMillis();

	private static void printLibraryPath() {
		logger.info("java.library.path={}", new Object[] { System.getProperty("java.library.path") });
	}

	public static void registerModule() {
		ModuleFactory.INSTANCES.registerModule(ModuleDemo.class.getName());
		ModuleFactory.INSTANCES.registerModule(ClientImpl.class.getName());
		ModuleFactory.INSTANCES.registerModule(ServerImpl.class.getName());
	}

	private static void startModules() {
		ModuleFactory.INSTANCES.startModules();
	}

	private static void printStartTime() {
		logger.info("AppServer starting success in {} ms.", (System.currentTimeMillis() - begin));
	}

	public static void main(String[] args) {
		printLibraryPath();
		registerModule();
		startModules();
		printStartTime();
		test();
	}

	private static void test() {
		for (int i = 0; i < 100; i++) {
			Response response = Processor.execute(new Request(ClientImpl.class.getName(), null, createCloudJob("test", i + "")));
			System.out.println(response.getResult());
		}
	}

	private static CloudJob createCloudJob(String jobGroup, String jobName) {
		JobDetail jobDetail = JobBuilder.newJob(TestJob.class).withIdentity(jobName, jobGroup).build();
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroup).startNow().build();
		return CloudJobFactory.builder(jobDetail, trigger, JobType.REMOTE).build();
	}

}