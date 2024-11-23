package net.skds.lib2.utils;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class Console {

	private static final DecimalFormat percentForm = new DecimalFormat("00.0%");

	private final Scanner scanner;
	private final Map<String, Command> commands = new ConcurrentHashMap<>();
	private boolean started = false;
	private boolean basicInit = false;

	public Console() {
		this.scanner = new Scanner(System.in);
	}

	public void addBasicCommands() {
		if (basicInit) {
			return;
		}
		basicInit = true;


		regCommand("kill", arg -> System.exit(0));
		regCommand("gc", arg -> {
			System.out.println("Performing gc...");
			System.gc();
			mem();
		});
		regCommand("mem", arg -> mem());
		regCommand("dump", arg -> SKDSUtils.dumpHeap("heap.hprof"));
	}

	private void mem() {
		Runtime runtime = Runtime.getRuntime();
		long max = runtime.maxMemory();
		long free = runtime.freeMemory();
		long allocated = runtime.totalMemory();
		long used = allocated - free;
		float allocatedP = (float) allocated / max;
		float usedP = (float) used / max;
		System.out.println("=========== Memory ===========");
		System.out.println("Total:     " + SKDSUtils.memoryCompact(max));
		System.out.println("Allocated: " + percentForm.format(allocatedP) + "   " + SKDSUtils.memoryCompact(allocated));
		System.out.println("Used:      " + percentForm.format(usedP) + "   " + SKDSUtils.memoryCompact(used));
		System.out.println("========= Memory end =========");
	}

	public void start() {
		if (started) {
			throw new RuntimeException("This console has been already started!");
		}
		started = true;
		ThreadUtil.runNewThreadMainGroupDaemon(() -> {
			while (true) {
				try {
					processLine(scanner.nextLine());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, "Console");

	}

	private void processLine(String line) {
		String[] args = line.split(" ");
		if (args.length > 0) {
			Command c = commands.get(args[0]);
			if (c != null) {
				c.action.accept(args);
				return;
			}
		}
		System.out.println("Unknown command \"" + line + "\"");
	}

	public void regCommand(String name, Consumer<String[]> action) {
		Command command = new Command(name, action);
		commands.put(name, command);
	}

	private record Command(String name, Consumer<String[]> action) {

	}

}
