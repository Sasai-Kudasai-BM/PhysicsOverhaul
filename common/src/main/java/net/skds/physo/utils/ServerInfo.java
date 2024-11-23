package net.skds.physo.utils;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.Util;
import net.minecraft.server.MinecraftServer;

public class ServerInfo {

	@Getter
	@Setter
	private static volatile long tickStart;
	@Getter
	@Setter
	private static MinecraftServer server;


	public static int getTickTimeMillis() {
		if (server == null) return 50;
		return (int) server.tickRateManager().millisecondsPerTick();
	}

	public static float remainTimeMillis() {
		if (server == null) return 50;
		return (float) (server.tickRateManager().millisecondsPerTick() - ((Util.getNanos() - tickStart) / 1E6));
	}
}
