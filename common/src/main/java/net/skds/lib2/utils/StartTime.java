package net.skds.lib2.utils;

@SuppressWarnings("unused")
public class StartTime {

	private long start;

	StartTime(long start) {
		this.start = start;
	}

	public double query() {
		return (System.nanoTime() - start) / 1_000_000.0;
	}

	public void reset() {
		start = System.nanoTime();
	}
}
