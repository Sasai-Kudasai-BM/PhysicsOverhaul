package net.skds.physo.fluids;

import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.skds.lib2.storage.palette.PalettedStorage;

import java.util.ArrayList;
import java.util.List;

public class DirectFluidSup implements PalettedStorage.DirectSupplier<FluidState> {

	public static final DirectFluidSup INSTANCE = new DirectFluidSup();
	private static final FluidState defaultValue = Fluids.EMPTY.defaultFluidState();

	private FluidState[] fluidArray;
	private Reference2IntMap<FluidState> stateMap;


	public void init() {

		Reference2IntMap<FluidState> stateIds = new Reference2IntOpenHashMap<>();

		Iterable<Fluid> fluids = BuiltInRegistries.FLUID;
		final List<FluidState> list = new ArrayList<>();
		for (Fluid f : fluids) {
			for (FluidState state : f.getStateDefinition().getPossibleStates()) {
				int i = list.size();
				list.add(state);
				stateIds.put(state, i);
				//System.out.println(state);
			}
		}
		fluidArray = list.toArray(new FluidState[0]);
		stateMap = stateIds;
	}

	@Override
	public FluidState getDefault() {
		return defaultValue;
	}

	@Override
	public FluidState get(int index) {
		return fluidArray[index];
	}

	@Override
	public int getIndex(FluidState value) {
		return stateMap.getOrDefault(value, 0);
	}

	@Override
	public int size() {
		return fluidArray.length;
	}

	@Override
	public int bitThreshold() {
		return 0;
	}

	@Override
	public int minBits() {
		return 0;
	}
}
