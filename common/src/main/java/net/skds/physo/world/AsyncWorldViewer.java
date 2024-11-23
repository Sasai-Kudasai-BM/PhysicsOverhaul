package net.skds.physo.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.FluidState;
import net.skds.physo.mixinglue.ChunkSectionGlue;
import net.skds.physo.mixins.ChunkSourceInvoker;

public class AsyncWorldViewer {

	public final ServerLevel world;
	public final BlockState voidState = Blocks.VOID_AIR.defaultBlockState();
	public final FluidState voidFluidState = voidState.getFluidState();


	public AsyncWorldViewer(ServerLevel world) {
		this.world = world;
	}

	public BlockState getBlockState(BlockPos pos) {
		return getBlockState(pos.getX(), pos.getY(), pos.getZ());
	}

	public BlockState getBlockState(int x, int y, int z) {
		if (world.isOutsideBuildHeight(y)) {
			return voidState;
		}
		LevelChunkSection section = getSection(x >> 4, world.getSectionIndex(y), z >> 4);
		if (section == null) {
			return null;
		}
		return section.getBlockState(x & 15, y & 15, z & 15);
	}

	public FluidState getFluidState(BlockPos pos) {
		return getFluidState(pos.getX(), pos.getY(), pos.getZ());
	}

	public FluidState getFluidState(int x, int y, int z) {
		if (world.isOutsideBuildHeight(y)) {
			return voidFluidState;
		}
		LevelChunkSection section = getSection(x >> 4, world.getSectionIndex(y), z >> 4);
		if (section == null) {
			return null;
		}
		return section.getFluidState(x & 15, y & 15, z & 15);
	}


	//public int getPressure(BlockPos pos) {
	//	return getPressure(pos.getX(), pos.getY(), pos.getZ());
	//}

	//public int getFullPressure(BlockPos pos) {
	//	return getPressure(pos.getX(), pos.getY(), pos.getZ()) + getFluidState(pos.getX(), pos.getY(), pos.getZ()).getAmount();
	//}

	//public int getPressure(int x, int y, int z) {
	//	if (world.isOutsideBuildHeight(y)) {
	//		return 0;
	//	}
	//	FluidSectionData data = getSectionDataForBlock(PhysicsOverhaul.FLUID_SECTION_DATA, x, y, z);
	//	if (data == null) {
	//		return 0;
	//	}
	//	return data.getPressure(x & 15, y & 15, z & 15);
	//}

	public <T extends ChunkSectionData> T getSectionDataForBlock(SectionDataRegistryEntry<T> type, BlockPos pos) {
		return getSectionDataForBlock(type, pos.getX(), pos.getY(), pos.getZ());
	}

	public <T extends ChunkSectionData> T getSectionDataForBlock(SectionDataRegistryEntry<T> type, int x, int y, int z) {
		LevelChunkSection section = getSection(x >> 4, world.getSectionIndex(y), z >> 4);
		if (section == null) {
			return null;
		}
		return ((ChunkSectionGlue) section).getDataHolder().getData(type);
	}

	public LevelChunkSection getSection(int cx, int cy, int cz) {
		LevelChunk chunk = getChunk(cx, cz);
		if (chunk == null) {
			return null;
		}
		if (cy < 0) {
			return null;
		}
		LevelChunkSection[] sections = chunk.getSections();
		if (sections.length <= cy) {
			return null;
		}
		return sections[cy];
	}

	public LevelChunk getChunk(int cx, int cz) {
		long pos = ChunkPos.asLong(cx, cz);
		ChunkHolder holder = ((ChunkSourceInvoker) world.getChunkSource()).getLoadedChunkAsync(pos);
		return holder.getTickingChunk();
	}
}
