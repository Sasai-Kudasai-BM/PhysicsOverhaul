package net.skds.lib2.utils.logger;

public final class SKDSLoggerFactory {
	private SKDSLoggerFactory() {
	}

	public static SKDSLogger getLogger(Class<?> c) {
		return new SKDSLoggerImpl(c);
	}
}
