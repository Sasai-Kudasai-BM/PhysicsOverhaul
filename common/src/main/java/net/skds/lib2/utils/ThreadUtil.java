package net.skds.lib2.utils;

import lombok.SneakyThrows;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ThreadUtil {

	public static final ThreadGroup MAIN_GROUP = new ThreadGroup("Main");
	public static final ThreadGroup UTIL_GROUP = new ThreadGroup("Util");

	private static final int threads = Math.max(4, Runtime.getRuntime().availableProcessors());

	private static final ThreadFactory VT_FACTORY = Thread.ofVirtual().name("SKDS-VT-", 0).factory();

	public static final Executor EXECUTOR = r -> VT_FACTORY.newThread(r).start();

	//ForkJoinPool.commonPool();
			/*
			new ForkJoinPool(threads,
					p -> {
						ForkJoinWorkerThread t = new ForkJoinWorkerThread(UTIL_GROUP, p, true) {
						};
						t.setName("SKDS-ForkJoinPool-" + t.getPoolIndex());
						t.setDaemon(true);
						return t;
					},
					(r, e) -> {
						throw new RuntimeException(e);
					},
					false
			);
	// */

	public static void main(String[] args) {
		for (int i = 0; i < 100; i++) {
			int j = i;
			EXECUTOR.execute(() -> {
				System.out.println(Thread.currentThread());
				ThreadUtil.await(10_000);
			});
		}

		ThreadUtil.await(10_000);
	}

	public static void runTaskNewThread(Runnable runnable) {
		new SKDSThread(UTIL_GROUP, runnable).start();
	}

	@SneakyThrows
	public static void await(long millis) {
		Thread.sleep(millis);
	}

	public static SKDSThread runNewThreadMainGroup(Runnable runnable, String name) {
		SKDSThread t = new SKDSThread(MAIN_GROUP, runnable, name);
		t.start();
		return t;
	}

	public static SKDSThread runNewThreadMainGroupDaemon(Runnable runnable, String name) {
		SKDSThread t = new SKDSThread(MAIN_GROUP, runnable, name);
		t.setDaemon(true);
		t.start();
		return t;
	}

	public static SKDSThread runTickableDaemon(FiniteTickable tickable, String name, int period) {
		SKDSThread t = new SKDSThread(MAIN_GROUP, name) {
			@Override
			public void run() {
				while (tick(tickable, period)) ;
			}

		};
		t.setDaemon(true);
		t.start();
		return t;
	}

	public static SKDSThread runTickableDaemon(FiniteTickable tickable, Runnable initRun, String name, int period, Consumer<Exception> exceptionHandler) {
		SKDSThread t = new SKDSThread(MAIN_GROUP, name) {
			@Override
			public synchronized void run() {
				try {
					initRun.run();
					while (tick(tickable, period)) ;
				} catch (Exception e) {
					exceptionHandler.accept(e);
				}
			}

		};
		t.setDaemon(true);
		t.start();
		return t;
	}

	public static SKDSThread runTickable(FiniteTickable tickable, String name, int period) {
		SKDSThread t = new SKDSThread(MAIN_GROUP, name) {
			@Override
			public void run() {
				while (tick(tickable, period)) ;
			}

		};
		t.start();
		return t;
	}

	public static SKDSThread runTickable(FiniteTickable tickable, Runnable initRun, String name, int period, Consumer<Exception> exceptionHandler) {
		SKDSThread t = new SKDSThread(MAIN_GROUP, name) {
			@Override
			public synchronized void run() {
				try {
					initRun.run();
					while (tick(tickable, period)) ;
				} catch (Exception e) {
					exceptionHandler.accept(e);
				}
			}

		};
		t.start();
		return t;
	}

	private static boolean tick(FiniteTickable tickable, int period) {
		long t0 = System.nanoTime();
		boolean tick = tickable.tick();
		long waitTime = t0 + (long) period * 1000_000L - System.nanoTime();
		LockSupport.parkNanos(waitTime);
		return tick;
	}

	public static void runAsync(Runnable runnable) {
		EXECUTOR.execute(runnable);
	}

	public static void runNThreads(UnsafeRunnable runnable, int n) {
		for (int i = 0; i < n; i++) {
			new SKDSThread(UTIL_GROUP, runnable).start();
		}
	}

	public static void runNThreads(Runnable runnable, int n) {
		for (int i = 0; i < n; i++) {
			new SKDSThread(UTIL_GROUP, runnable).start();
		}
	}

	public static void runAsync(UnsafeRunnable runnable) {
		EXECUTOR.execute(runnable);
	}

	public static class SKDSThread extends Thread {

		private static volatile int threadInitNumber;
		private final Runnable task;

		private static synchronized int nextThreadNum() {
			return threadInitNumber++;
		}

		public SKDSThread(ThreadGroup group, Runnable task) {
			super(group, task, "SKDS-" + group.getName() + "-" + nextThreadNum(), 0);
			this.task = task;
		}

		public SKDSThread(ThreadGroup group, Runnable task, String name) {
			super(group, task, name, 0);
			this.task = task;
		}

		public SKDSThread(String name) {
			super(MAIN_GROUP, null, name, 0);
			this.task = null;
		}

		public SKDSThread(ThreadGroup group, String name) {
			super(group, null, name, 0);
			this.task = null;
		}

		@Override
		public void run() {
			task.run();
		}

	}


	static {
		if (SKDSUtils.OS_TYPE == SKDSUtils.OSType.WINDOWS) {
			new Thread() { // dirty Windows timer fix
				{
					this.setName("Dirty-Windows-timer-fix");
					this.setDaemon(true);
					this.start();
				}

				public void run() {
					try {
						Thread.sleep(Long.MAX_VALUE);
					} catch (InterruptedException ignored) {
					}
				}
			};
		}
	}
}
