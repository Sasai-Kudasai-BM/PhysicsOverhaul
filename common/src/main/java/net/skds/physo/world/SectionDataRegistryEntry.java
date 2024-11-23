package net.skds.physo.world;

import net.skds.physo.utils.WorldSide;

import java.util.function.Function;


public class SectionDataRegistryEntry<T extends ChunkSectionData> {

	public final Function<SectionDataHolder, T> constructor;
	public final WorldSide side;
	public final String id;
	public final int index;

	protected SectionDataRegistryEntry(Function<SectionDataHolder, T> constructor, WorldSide side, String id, int index) {
		this.constructor = constructor;
		this.side = side;
		this.id = id;
		this.index = index;
	}
}
