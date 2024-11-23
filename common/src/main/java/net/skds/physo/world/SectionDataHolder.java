package net.skds.physo.world;

import lombok.Getter;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.skds.physo.utils.WorldSide;

public class SectionDataHolder {

	@Getter
	private final LevelChunkSection section;

	private Link first;
	@Getter
	private Level world;

	public SectionDataHolder(LevelChunkSection section) {
		this.section = section;
	}

	public static int getIndex(int x, int y, int z) {
		return (y << 4 | z) << 4 | x;
	}

	@SuppressWarnings("unchecked")
	public <T extends ChunkSectionData> T getData(SectionDataRegistryEntry<T> type) {
		Link lnk = first;
		while (lnk != null) {
			if (lnk.type == type) {
				return (T) lnk.value;
			}
			lnk = lnk.next;
		}
		return createData(type); // TODO
	}

	public <T extends ChunkSectionData> T createData(SectionDataRegistryEntry<T> type) {
		T value = type.constructor.apply(this);
		Link lnk = new Link(type, value);
		lnk.next = first;
		first = lnk;
		return value;
	}

	public void onLoad(LevelChunk chunk, SectionPos sectionPos) {
		this.world = chunk.getLevel();
		Link lnk = first;
		while (lnk != null) {
			lnk.value.onLoad(chunk, sectionPos);
			lnk = lnk.next;
		}
	}

	public void onUnload(LevelChunk chunk, SectionPos sectionPos) {
		this.world = chunk.getLevel();
		Link lnk = first;
		while (lnk != null) {
			lnk.value.onUnload(chunk, sectionPos);
			lnk = lnk.next;
		}
	}

	public int getExtraSize() {
		int size = 1;
		Link lnk = first;
		while (lnk != null) {
			if (lnk.type.side == WorldSide.BOTH) {
				size += VarInt.getByteSize(lnk.type.index) + lnk.value.getDataSize();
			}
			lnk = lnk.next;
		}
		return size;
	}

	public void read(FriendlyByteBuf buf) {
		int count = buf.readByte();
		Link lnk = null;
		while (count-- > 0) {
			lnk = readData(lnk, buf);
		}
	}

	private Link readData(Link last, FriendlyByteBuf buf) {
		int index = buf.readVarInt();
		var entry = SectionDataRegistry.getDataRegistryEntries().get(index);
		var value = entry.constructor.apply(this);
		value.read(buf);
		if (last == null) {
			this.first = new Link(entry, value);
			return this.first;
		}
		return last.next = new Link(entry, value);
	}

	public void write(FriendlyByteBuf buf) {
		int size = 0;
		Link lnk = first;
		while (lnk != null) {
			if (lnk.type.side == WorldSide.BOTH) {
				size++;
			}
			lnk = lnk.next;
		}
		buf.writeByte(size);
		if (size > 0) {
			lnk = first;
			while (lnk != null) {
				if (lnk.type.side == WorldSide.BOTH) {
					buf.writeVarInt(lnk.type.index);
					lnk.value.write(buf);
				}
				lnk = lnk.next;
			}
		}
	}


	public CompoundTag writeNBT() {
		Link lnk = first;
		if (lnk != null) {
			CompoundTag nbt = new CompoundTag();
			while (lnk != null) {
				CompoundTag nbt2 = lnk.value.writeNBT();
				if (nbt2 != null) {
					nbt.put(lnk.type.id, nbt2);
				}
				lnk = lnk.next;
			}
		}
		return null;
	}


	public void readNBT(CompoundTag nbt, ServerLevel serverLevel) {
		final var reg = SectionDataRegistry.getDataRegistryEntryMap();
		SectionDataRegistryEntry<?> e;
		for (String key : nbt.getAllKeys()) {
			e = reg.get(key);
			if (e != null) {
				CompoundTag nbt2 = nbt.getCompound(key);
				ChunkSectionData data = createData(e);
				data.readNBT(nbt2, serverLevel);
			}
		}
	}

	private static class Link {
		private final SectionDataRegistryEntry<?> type;
		private final ChunkSectionData value;
		private Link next;

		public Link(SectionDataRegistryEntry<?> type, ChunkSectionData value) {
			this.type = type;
			this.value = value;
		}
	}
}
