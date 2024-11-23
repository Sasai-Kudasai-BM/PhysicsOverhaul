package net.skds.lib2.utils.json;

public interface TypedConfig<ET extends Enum<? extends ConfigType<?>>> {
	ET getConfigType();
}
