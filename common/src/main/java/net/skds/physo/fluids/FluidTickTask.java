package net.skds.physo.fluids;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.skds.physo.world.AsyncWorldViewer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class FluidTickTask implements Comparable<FluidTickTask>, Runnable {

	private static final Direction[] horizontal = {Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.WEST};
	private static final Random r = new Random();
	private static final int invalidSlope = Integer.MIN_VALUE + 1;
	private static final Vec3i XP = new Vec3i(1, 0, 0);
	private static final Vec3i XN = new Vec3i(-1, 0, 0);
	private static final Vec3i YP = new Vec3i(0, 1, 0);
	private static final Vec3i YN = new Vec3i(0, -1, 0);
	private static final Vec3i ZP = new Vec3i(0, 0, 1);
	private static final Vec3i ZN = new Vec3i(0, 0, -1);

	private ServerLevel world;
	private AsyncWorldViewer viewer;
	@Getter
	private int nextTick;
	private Consumer<FluidTickTask> postProcessTasks;
	public final BlockPos pos;
	public final FlowingFluid fluid;
	public final Fluid fluidType;
	public final int startTick;

	private final int dist = 4;

	private boolean analyzed = false;
	private boolean cancel = false;

	private int amount;

	private BlockPos posB;

	private FluidState fluidStateA1;
	private FluidState fluidStateB1;

	private FluidState fluidStateA2;
	private FluidState fluidStateB2;

	public FluidTickTask(BlockPos pos, FlowingFluid fluid, int startTick) {
		this.pos = pos;
		this.fluid = fluid;
		this.fluidType = fluid.getSource();
		this.startTick = startTick;
	}

	public void setWorld(AsyncWorldViewer viewer, Consumer<FluidTickTask> postProcessor) {
		this.viewer = viewer;
		this.world = viewer.world;
		this.postProcessTasks = postProcessor;
		this.nextTick = startTick + Math.max(fluid.getTickDelay(world) / 3, 1);
	}

	private List<Direction> randomizedDirs() {
		List<Direction> dirs = Arrays.asList(horizontal);
		Collections.shuffle(dirs, r);
		return dirs;
	}

	private int getGrad(Vec3i offset) {
		int dh = 0;
		BlockPos p = pos;
		for (int i = dist; i > 0; i--) {
			BlockPos p2 = p.offset(offset);
			if (!canFlow(p, p2)) {
				break;
			}
			FluidState s2 = viewer.getFluidState(p2);
			if (s2.getType().isSame(fluidType)) {
				int l2 = s2.getAmount();
				dh += i * (amount - l2);
			} else if (fluidReplaceAble(s2)) {
				dh += i * amount;
			}
			p = p2;
		}
		return dh;
	}


	private Vec3i detectSlope() {
		int dx = getGrad(XP) - getGrad(XN);
		int dz = getGrad(ZP) - getGrad(ZN);

		if (dx == 0 && dz == 0) {
			return horizontal[r.nextInt(horizontal.length)].getNormal();
		}
		return new Vec3i(dx, 0, dz);
	}

	private void preFlow(Vec3i slope) {
		Vec3i dir;
		if (Math.abs(slope.getX()) > Math.abs(slope.getZ())) {
			dir = new Vec3i(slope.getX() > 0 ? 1 : -1, 0, 0);
		} else {
			dir = new Vec3i(0, 0, slope.getZ() > 0 ? 1 : -1);
		}

		posB = pos.offset(dir);
		if (!canFlow(pos, posB) || amount == 1) { //TODO
			cancel = true;
			return;
		}
		FluidState s2 = viewer.getFluidState(posB);
		int a2 = 0;
		if (s2.getType().isSame(fluidType)) {
			a2 = s2.getAmount();
		}
		int d = (amount - a2) / 2;
		if (d < 1) {
			//if (d < 0) {
			System.out.println(" Pizdyakus000 " + amount + "  " + a2 + "  " + pos);
			cancel = true;
			return;
			//}
			//d = 1;
		}
		if (d > this.amount) {
			d = this.amount;
		}
		if (d + a2 > 8) {
			d = 8 - a2;
			System.out.println(" Pizdyakus " + d);
		}
		int na1 = this.amount - d;
		int na2 = a2 + d;
		this.fluidStateA2 = FluidUtils.getStateForLevel(fluid, na1);
		this.fluidStateB1 = s2;
		this.fluidStateB2 = FluidUtils.getStateForLevel(fluid, na2);
	}


	private void analyzeTick() {
		this.fluidStateA1 = viewer.getFluidState(pos);
		if (!fluidStateA1.getType().isSame(fluidType)) {
			return;
		}
		this.amount = this.fluidStateA1.getAmount();
		Vec3i slope = detectSlope();
		if (slope != null) {
			preFlow(slope);
			if (!cancel) {
				postProcessTasks.accept(this);
			}
		}
	}


	private boolean fluidReplaceAble(FluidState state) {
		return state.isEmpty();
	}

	private boolean canFlow(BlockPos from, BlockPos to) {
		BlockState stateFrom = viewer.getBlockState(from);
		if (stateFrom == null) {
			cancel = true;
			return false;
		}
		BlockState stateTo = viewer.getBlockState(to);
		if (stateTo == null) {
			cancel = true;
			return false;
		}
		FluidState other = viewer.getFluidState(to);
		if (!other.getType().isSame(fluidType) && !fluidReplaceAble(other)) {
			return false;
		}
		VoxelShape shapeFrom = stateFrom.getCollisionShape(world, from);
		VoxelShape shapeTo = stateTo.getCollisionShape(world, to);
		if (shapeFrom.isEmpty() && shapeTo.isEmpty()) {
			return true;
		}
		//return !VoxelShapes.doAdjacentCubeSidesFillSquare(voxelShape1, voxelShape2, direction);

		return false;
	}


	private void finalTick() {
		FluidState ca = viewer.getFluidState(pos);
		FluidState cb = viewer.getFluidState(posB);
		if (ca == this.fluidStateA1 && cb == this.fluidStateB1) {
			FluidUtils.setFluidState(viewer, pos, this.fluidStateA2);
			FluidUtils.setFluidState(viewer, posB, this.fluidStateB2);
		}
	}

	@Override
	public void run() {
		if (analyzed) finalTick();
		else {
			analyzeTick();
			analyzed = true;
		}
	}

	@Override
	public int compareTo(@NotNull FluidTickTask o) {
		if (nextTick == o.nextTick) {
			return pos.compareTo(o.pos);
		}
		return nextTick - o.nextTick;
	}

	//private void lookupPressure() {
	//	int pressure = amount + viewer.getPressure(pos);
	//	int maxPressure = pressure;
	//	int minPressure = Integer.MAX_VALUE;
	//	for (int i = 0; i < horizontal.length; i++) {
	//		Direction dir = horizontal[i];
	//		Vec3i offset = dir.getNormal();
	//		BlockPos pos2 = pos.offset(offset);
	//		if (canFlow(pos, pos2)) {
	//			int p2 = viewer.getFullPressure(pos2);
	//			if (p2 > maxPressure) {
	//				maxPressure = p2;
	//			}
	//			if (p2 < minPressure) {
	//				minPressure = p2;
	//			}
	//		}
	//	}
	//	if (maxPressure > pressure) {
	//		FluidUtils.setPressure(viewer, pos, maxPressure - amount);
	//	}
	//}
/*
	private Vec3i detectSlope0() {
		int xp = getGrad(XP);
		int xn = getGrad(XN);
		int zp = getGrad(ZP);
		int zn = getGrad(ZN);
		int dx = xp - xn;
		int dz = zp - zn;
		Vec3i slope = null;
		if ((xp != invalidSlope || xn != invalidSlope) && Math.abs(dx) > Math.abs(dz)) {
			if (dx < 0) {
				if (xp != invalidSlope) slope = XP;
			} else if (dx > 0) {
				if (xn != invalidSlope) slope = XN;
			}
		} else {
			if (dz < 0) {
				if (zp != invalidSlope) slope = ZP;
			} else if (dz > 0) {
				if (zn != invalidSlope) slope = ZN;
			}
		}
		return slope;
	}

 */
}
