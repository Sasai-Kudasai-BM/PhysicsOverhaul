package net.skds.physo.world;

import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;

public interface ChunkSectionData {

	int getDataSize();

	void read(FriendlyByteBuf buf);

	void write(FriendlyByteBuf buf);

	void onLoad(LevelChunk chunk, SectionPos sectionPos);

	void onUnload(LevelChunk chunk, SectionPos sectionPos);

	void readNBT(CompoundTag nbt, ServerLevel serverLevel);

	CompoundTag writeNBT();
}
