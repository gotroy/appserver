package org.z.cloud.appserver.module;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.z.cloud.common.module.Module;

public class ModuleDemo implements Module {

	private final Logger logger = LoggerFactory.getLogger(ModuleDemo.class);

	private ExecutorService pool = null;
	private String moduleName = this.getClass().getSimpleName();

	@Override
	public Object service(Object... params) {
		logger.info("[{}] execute with params [{}]", new Object[] { moduleName, params });
		return null;
	}

	@Override
	public boolean start() {
		pool = Executors.newCachedThreadPool();
		return true;
	}

	@Override
	public boolean stop() {
		if (pool == null)
			return true;
		pool.shutdown();
		while (!pool.isTerminated())
			await();
		return true;
	}

	private void await() {
		try {
			pool.awaitTermination(1, TimeUnit.SECONDS);
			logger.info("[{}] is destroying", moduleName);
		} catch (InterruptedException e) {
			loggerError(e);
		}
	}

	protected void loggerError(Exception e) {
		logger.error(e.getMessage(), e);
	}

	@Override
	public boolean reStart() {
		stop();
		return start();
	}

}