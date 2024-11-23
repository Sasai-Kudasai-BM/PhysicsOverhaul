package net.skds.lib2.utils;

import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
public class SKDSTimer {

	private static final AtomicInteger idCounter = new AtomicInteger();
	private static final ConcurrentSkipListSet<TimerEntry> queue = new ConcurrentSkipListSet<>();

	private static void check() throws InterruptedException {
		long time = System.currentTimeMillis();

		TimerEntry entry = queue.pollFirst();
		if (entry == null) {
			Thread.sleep(10);
			return;
		}
		if (time >= entry.time) {
			entry.task.run();
		} else {
			queue.add(entry);
			Thread.sleep(10);
		}

	}

	public static void scheduleTaskAbsolute(Runnable task, long time) {
		queue.add(new TimerEntry(time, task));
	}

	public static void scheduleTask(Runnable task, long time) {
		queue.add(new TimerEntry(System.currentTimeMillis() + time, task));
	}

	private record TimerEntry(long time, int id, Runnable task) implements Comparable<TimerEntry> {

		public TimerEntry(long time, Runnable task) {
			this(time, idCounter.getAndIncrement(), task);
		}

		@Override
		public int compareTo(TimerEntry o) {
			if (time > o.time) {
				return 1;
			} else if (time < o.time) {
				return -1;
			}
			return Integer.compare(id, o.id);
		}
	}

	static {
		ThreadUtil.runNewThreadMainGroupDaemon(() -> {
			while (true) {
				try {
					check();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, "SKDS-Timer");
	}
}
