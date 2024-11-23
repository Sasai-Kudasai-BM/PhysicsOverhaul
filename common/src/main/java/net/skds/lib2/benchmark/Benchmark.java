package net.skds.lib2.benchmark;

import net.skds.lib2.utils.SKDSUtils;

public abstract class Benchmark {

	private final int warmup;
	private final int iterations;

	private double resultWarmup;
	private double resultBench;

	protected Benchmark(int warmup, int iterations) {
		this.warmup = warmup;
		this.iterations = iterations;
	}


	public final void run() {
		run(SKDSUtils.EMPTY_RUNNABLE);
	}

	public final void run(Runnable onFinish) {
		prepare();
		long t = System.nanoTime();

		for (int i = 0; i < warmup; i++) {
			bench();
		}
		resultWarmup = (double) (System.nanoTime() - t);
		//System.gc();
		t = System.nanoTime();
		for (int i = 0; i < iterations; i++) {
			bench();
		}
		resultBench = (double) (System.nanoTime() - t);
		onFinish.run();
	}

	public final String result() {
		return "======== Bench =========\n" +
				"Warmup: " + (resultWarmup / 1000_000) + " ms\n" +
				((resultWarmup / 1000) / warmup) + " us/op\n" +
				"Bench: " + (resultBench / 1000_000) + " ms\n" +
				((resultBench / 1000) / iterations) + " us/op\n" +
				"======= Bench end =======";
	}

	protected abstract void prepare();

	protected abstract void bench();
}
