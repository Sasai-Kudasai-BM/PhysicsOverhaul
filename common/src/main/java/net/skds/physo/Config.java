package net.skds.physo;

import lombok.Getter;
import net.skds.lib2.utils.json.JsonUtils;

@SuppressWarnings("FieldMayBeFinal")
@Getter
public class Config {

	@Getter
	private static Config instance;

	private boolean wpoEnable = true;
	private boolean debug = false;

	public static void load() {
		Config cfg = JsonUtils.readConfig("config/physo.cfg.json", Config.class);
		if (cfg == null) {
			cfg = new Config();
			JsonUtils.saveConfig("config/physo.cfg.json", cfg);
		}
		instance = cfg;
	}
}
