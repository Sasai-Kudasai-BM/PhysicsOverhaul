package net.skds.lib2.utils.logger;


final class SKDSLoggerImpl extends SKDSLogger {

	private final Class<?> loggingClass;
	private final StringBuffer stringBuffer = new StringBuffer(256);

	public SKDSLoggerImpl(Class<?> loggingClass) {
		this.loggingClass = loggingClass;
	}

	@Override
	public void log0(LoggerLevel level, Object msg) {
		String message = String.valueOf(msg);
		long time = System.currentTimeMillis();
		String thread = Thread.currentThread().getName();
		var trace = Thread.currentThread().getStackTrace();

		StackTraceElement e = trace[3];

		System.out.println(e.getClassName() + "." + e.getMethodName() + ":" + e.getLineNumber());

	}

	private boolean isLoggingLevel(LoggerLevel level) {
		return SKDSLoggerConfig.LEVELS.contains(level);
	}

	public static void main(String[] args) {
		SKDSLogger log = SKDSLoggerFactory.getLogger(SKDSLoggerImpl.class);

		log.log("kek");
	}
}
