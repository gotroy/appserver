package org.z.cloud.appserver.module;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.z.cloud.common.module.Module;

public class ModuleProcessor implements Module {

	private ExecutorService executor = null;

	@Override
	public boolean start() {
		init();
		return true;
	}

	protected void executeFuture(final Processor processor) {
		Runnable job = new Runnable() {
			@Override
			public void run() {
				sendMessage(processor);
			}
		};
		Future<?> task = executor.submit(job);
		try {
			task.get(1000, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		} finally {
			task.cancel(true);
		}
	}

	protected void executeRunnable(final Processor processor) {
		Runnable job = new Runnable() {
			@Override
			public void run() {
				sendMessage(processor);
			}
		};
		executor.execute(job);
	}

	protected boolean sendMessage(Processor processor) {
		return true;
	}

	private boolean init() {
		executor = Executors.newCachedThreadPool();
		return executor != null;
	}

	@Override
	public boolean stop() {
		if (executor == null)
			return true;
		try {
			executor.shutdown();
			executor.awaitTermination(100, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
		} finally {
		}
		return false;
	}

	protected void saveUnFinishedJob() {
		saveList(executor.shutdownNow());
	}

	private void saveList(List<Runnable> runnables) {
		for (Runnable runnable : runnables)
			save(runnable);
	}

	private void save(Runnable runnable) {
	}

	@Override
	public boolean reStart() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long getStartTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object service(Object... params) {
		// TODO Auto-generated method stub
		return null;
	}

}
