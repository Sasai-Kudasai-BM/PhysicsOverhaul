package net.skds.physo;

import net.minecraft.Util;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.skds.physo.mixinglue.ChunkSectionGlue;
import net.skds.physo.utils.ServerInfo;
import net.skds.physo.world.CustomWorldTicks;
import net.skds.physo.world.SectionDataHolder;

import java.util.function.BooleanSupplier;

public class Events {


	public static void reload() {
		Config.load();
	}

	public static void serverTickStart(MinecraftServer server, BooleanSupplier bs) {
		ServerInfo.setServer(server);
		ServerInfo.setTickStart(Util.getNanos());
	}

	public static void serverTickEnd(MinecraftServer server, BooleanSupplier bs) {
		for (ServerLevel level : server.getAllLevels()) {
			((CustomWorldTicks) level.getFluidTicks()).onTickEnd(bs);
		}
	}

	public static void loadChunk(LevelChunk c) {

		final LevelChunkSection[] sections = c.getSections();
		ChunkPos cp = c.getPos();
		for (int i = 0; i < sections.length; i++) {
			((ChunkSectionGlue) sections[i]).getDataHolder().onLoad((LevelChunk) c, SectionPos.of(cp, c.getSectionYFromSectionIndex(i)));
		}
	}

	public static void unloadChunk(LevelChunk c) {

		final LevelChunkSection[] sections = c.getSections();
		ChunkPos cp = c.getPos();
		for (int i = 0; i < sections.length; i++) {
			((ChunkSectionGlue) sections[i]).getDataHolder().onUnload((LevelChunk) c, SectionPos.of(cp, c.getSectionYFromSectionIndex(i)));
		}
	}

	public static void saveChunk(ChunkAccess ca, CompoundTag nbt) {
		if (ca.getPersistedStatus() != ChunkStatus.FULL) {
			return;
		}
		ListTag list = new ListTag();
		var sections = ca.getSections();
		final int max = ca.getMaxSection();
		for (int i = ca.getMinSection(); i < max; i++) {
			int j = ca.getSectionIndexFromSectionY(i);
			LevelChunkSection sec = sections[j];
			if (sec == null) {
				continue;
			}
			SectionDataHolder holder = ((ChunkSectionGlue) sec).getDataHolder();
			CompoundTag sectionTag = holder.writeNBT();
			if (sectionTag != null && !sectionTag.isEmpty()) {
				sectionTag.putByte("Y", (byte) i);
				list.add(sectionTag);
			}
		}
		nbt.put("skdsSectionData", list);
	}

	public static void readChunk(ProtoChunk chunk, CompoundTag nbt, ServerLevel serverLevel) {
		if (!nbt.contains("skdsSectionData")) {
			return;
		}
		ListTag list = nbt.getList("skdsSectionData", ListTag.TAG_COMPOUND);
		if (list.isEmpty()) {
			return;
		}
		final int max = list.size();
		var sections = chunk.getSections();
		for (int i = chunk.getMinSection(); i < max; i++) {
			CompoundTag tag = list.getCompound(i);
			int j = chunk.getSectionIndexFromSectionY(tag.getInt("Y"));
			LevelChunkSection sec = sections[j];
			if (sec == null) {
				continue;
			}
			SectionDataHolder holder = ((ChunkSectionGlue) sec).getDataHolder();
			holder.readNBT(tag, serverLevel);
		}
	}

}
