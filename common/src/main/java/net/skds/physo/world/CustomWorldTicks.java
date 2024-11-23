package net.skds.physo.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.ticks.LevelChunkTicks;
import net.minecraft.world.ticks.LevelTicks;
import net.minecraft.world.ticks.ScheduledTick;
import net.skds.lib2.utils.ThreadUtil;
import net.skds.physo.PhysicsOverhaul;
import net.skds.physo.fluids.FluidSectionData;
import net.skds.physo.fluids.FluidTickTask;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.LongPredicate;
import java.util.function.Supplier;

public class CustomWorldTicks extends LevelTicks<Fluid> {

	private final LongPredicate tickPredicate;
	public final ServerLevel world;
	public final AsyncWorldViewer viewer;
	public final LinkedBlockingQueue<FluidTickTask> tickTasks = new LinkedBlockingQueue<>();
	private final LinkedBlockingQueue<FluidTickTask> postProcessTasks = new LinkedBlockingQueue<>(10);

	private boolean run = true;

	public CustomWorldTicks(LongPredicate p, Supplier<ProfilerFiller> s, ServerLevel world) {
		super(p, s);
		this.tickPredicate = p;
		this.world = world;
		this.viewer = new AsyncWorldViewer(world);
		ThreadUtil.runNewThreadMainGroupDaemon(this::executorLoop, "FluidAnalyzer");
	}

	@Override
	public void schedule(ScheduledTick<Fluid> st) {
		if (st.type() instanceof FlowingFluid flowing) {
			FluidTickTask tickTask = new FluidTickTask(st.pos(), flowing, (int) world.getLevelData().getGameTime());
			long chunkPos = ChunkPos.asLong(st.pos());
			if (!tickPredicate.test(chunkPos)) {
				FluidSectionData fd = viewer.getSectionDataForBlock(PhysicsOverhaul.FLUID_SECTION_DATA, st.pos());
				if (fd != null) {
					fd.addTick(tickTask);
				}
				return;
			}
			addTickTask(tickTask);
		}
	}

	public void unload() {
		run = false;
	}

	private void executorLoop() {
		while (run) {
			try {
				FluidTickTask task = tickTasks.poll(100, TimeUnit.MILLISECONDS);
				if (task != null) {
					task.run();
				}
			} catch (InterruptedException ignored) {
			}
		}
	}

	public void onTickEnd(BooleanSupplier timeRemains) {
		FluidTickTask postTask;
		while (timeRemains.getAsBoolean() && (postTask = postProcessTasks.poll()) != null) {
			postTask.run();
		}
	}

	public void addTickTask(FluidTickTask task) {
		task.setWorld(viewer, t -> {
			try {
				postProcessTasks.offer(t, 10, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		});
		tickTasks.add(task);
	}


	@Override
	public void tick(long p_193226_, int p_193227_, BiConsumer<BlockPos, Fluid> p_193228_) {
	}

	@Override
	public void addContainer(ChunkPos p_193232_, LevelChunkTicks<Fluid> p_193233_) {
	}

	@Override
	public void removeContainer(ChunkPos p_193230_) {
	}

	@Override
	public boolean hasScheduledTick(BlockPos p_193254_, Fluid p_193255_) {
		return false;
	}

	@Override
	public boolean willTickThisTick(BlockPos p_193282_, Fluid p_193283_) {
		return false;
	}

	@Override
	public void clearArea(BoundingBox p_193235_) {
	}

	@Override
	public void copyArea(BoundingBox p_193243_, Vec3i p_193244_) {
	}

	@Override
	public void copyAreaFrom(LevelTicks<Fluid> p_265554_, BoundingBox p_265172_, Vec3i p_265318_) {
	}

	@Override
	public int count() {
		return 0;
	}

}
