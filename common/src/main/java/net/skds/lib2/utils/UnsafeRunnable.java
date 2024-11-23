package net.skds.lib2.utils;

@FunctionalInterface
public interface UnsafeRunnable extends Runnable {

	@Override
	default void run() {
		try {
			runUnsafe();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void runUnsafe() throws Exception;
}
