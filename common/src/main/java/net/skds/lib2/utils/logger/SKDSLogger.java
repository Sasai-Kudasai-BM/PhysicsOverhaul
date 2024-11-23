package net.skds.lib2.utils.logger;

public abstract class SKDSLogger {

	protected abstract void log0(LoggerLevel level, Object msg);
	
	public void debug(Object msg) {
		log0(LoggerLevel.DEBUG, msg);
	}

	public void info(Object msg) {
		log0(LoggerLevel.INFO, msg);
	}

	public void log(Object msg) {
		log0(LoggerLevel.LOG, msg);
	}

	public void warn(Object msg) {
		log0(LoggerLevel.WARN, msg);
	}

	public void err(Object msg) {
		log0(LoggerLevel.ERROR, msg);
	}

}
