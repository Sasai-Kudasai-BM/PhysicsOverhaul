package net.skds.lib2.utils.logger;

import java.util.EnumSet;

public final class SKDSLoggerConfig {
	private SKDSLoggerConfig() {
	}

	static EnumSet<LoggerLevel> LEVELS = EnumSet.of(LoggerLevel.INFO, LoggerLevel.LOG, LoggerLevel.WARN, LoggerLevel.ERROR);

	public static void setLevels(LoggerLevel level, LoggerLevel... levels) {
		SKDSLoggerConfig.LEVELS = EnumSet.of(level, levels);
	}

	@SuppressWarnings("ManualArrayToCollectionCopy")
	public static void setLevelsFromAbove(LoggerLevel level) {
		EnumSet<LoggerLevel> newLevels = EnumSet.of(level);
		LoggerLevel[] values = LoggerLevel.values();
		for (int i = level.ordinal() + 1; i < values.length; i++) {
			newLevels.add(values[i]);
		}
		SKDSLoggerConfig.LEVELS = newLevels;
	}
}
