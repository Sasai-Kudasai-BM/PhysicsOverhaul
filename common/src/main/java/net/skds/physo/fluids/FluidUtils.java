package net.skds.physo.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.skds.physo.PhysicsOverhaul;
import net.skds.physo.world.AsyncWorldViewer;

public class FluidUtils {

	public static final int SET_BLOCK_BIT = 1 << 20;

	private static final FluidState EMPTY = Fluids.EMPTY.defaultFluidState();
	private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public static FluidState getStateForLevel(final FlowingFluid fluid, final int level) {

		if (level < 0 || level > 8) {
			throw new IllegalStateException("level: " + level);
		}

		//===============
		if (level == 8) {
			return fluid.getSource().defaultFluidState();
		} else if (level == 0) {
			return EMPTY;
		} else {
			return fluid.getFlowing(level, false);
		}
	}

	public static void setFluidState(final AsyncWorldViewer viewer, final BlockPos pos, final FluidState state) {
		FluidSectionData fd = viewer.getSectionDataForBlock(PhysicsOverhaul.FLUID_SECTION_DATA, pos);
		if (fd == null) {
			return;
		}
		final BlockState bs = viewer.getBlockState(pos);
		if (bs == null) {
			return;
		}
		fd.setFluidState(pos.getX() & 0xF, pos.getY() & 0xF, pos.getZ() & 0xF, state);
		final ServerLevel w = viewer.world;
		if (state != bs.getFluidState()) {
			BlockState setState = transformState(state, bs);
			int flags = 2 | SET_BLOCK_BIT;
			if (!(setState.getBlock() instanceof LiquidBlock)) {
				flags |= 1;
			}
			w.setBlock(pos, setState, flags);
		}
	}

	//public static void setPressure(final AsyncWorldViewer viewer, final BlockPos pos, final int pressure) {
	//	FluidSectionData fd = viewer.getSectionDataForBlock(PhysicsOverhaul.FLUID_SECTION_DATA, pos);
	//	if (fd == null) {
	//		return;
	//	}
	//	fd.setPressure(pos.getX() & 0xF, pos.getY() & 0xF, pos.getZ() & 0xF, pressure);
	//}

	public static void putFluid(AsyncWorldViewer viewer, BlockPos pos, Fluid fluid, int amount) {

	}

	public static BlockState transformState(FluidState fluidState, BlockState blockState) {
		if (blockState.hasProperty(WATERLOGGED)) {
			blockState = blockState.setValue(WATERLOGGED, !fluidState.isEmpty());
		} else {
			blockState = fluidState.createLegacyBlock();
		}

		return blockState;
	}

	public static void onSetBlockSection(FluidSectionData fd, int x, int y, int z, BlockState oldState, BlockState newState) {
		System.out.println("setBlockState " + oldState + " -> " + newState);
	}
}
