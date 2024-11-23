package net.skds.physo.fluids;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.skds.lib2.storage.palette.PalettedData;
import net.skds.lib2.storage.palette.PalettedStorage;
import net.skds.lib2.utils.Holders;
import net.skds.physo.world.ChunkSectionData;
import net.skds.physo.world.CustomWorldTicks;
import net.skds.physo.world.SectionDataHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FluidSectionData implements ChunkSectionData {

	private static final DirectFluidSup sup = DirectFluidSup.INSTANCE;
	private static final FluidState defaultValue = Fluids.EMPTY.defaultFluidState();

	//private short[] pressure;
	private PalettedStorage<FluidState> storage;
	private final SectionDataHolder holder;
	private final List<FluidTickTask> ticks = new ArrayList<>();

	public FluidSectionData(SectionDataHolder holder) {
		this.holder = holder;
	}

	public void addTick(FluidTickTask tick) {
		ticks.add(tick);
	}

	@Override
	public void onLoad(LevelChunk chunk, SectionPos sectionPos) {
		if (!ticks.isEmpty() && chunk.getLevel() instanceof ServerLevel w) {
			var tickTasks = ((CustomWorldTicks) w.getFluidTicks());
			for (FluidTickTask task : ticks) {
				tickTasks.addTickTask(task);
			}
			ticks.clear();
		}
	}

	@Override
	public void onUnload(LevelChunk chunk, SectionPos sectionPos) {
		if (chunk.getLevel() instanceof ServerLevel w) {
			var tickTasks = ((CustomWorldTicks) w.getFluidTicks()).tickTasks;
			//this.ticks = new ArrayList<>();
			tickTasks.stream()
					.filter(t -> SectionPos.asLong(t.pos) == sectionPos.asLong())
					.forEach(t -> {
						ticks.add(t);
						tickTasks.remove(t);
					});
		}
	}

	public void setFluidState(int x, int y, int z, FluidState state) {
		PalettedStorage<FluidState> s = this.storage;
		if (s == null) {
			s = new PalettedStorage<>(4096, defaultValue, sup);
			this.storage = s;
		}
		s.set(SectionDataHolder.getIndex(x, y, z), state);
	}


	public FluidState getFluidState(int x, int y, int z) {
		PalettedStorage<FluidState> s = this.storage;
		if (s == null) {
			return holder.getSection().getBlockState(x, y, z).getFluidState();
		}
		return s.get(SectionDataHolder.getIndex(x, y, z));
	}

	//public int getPressure(int x, int y, int z) {
	//	short[] arr = this.pressure;
	//	if (arr == null) {
	//		return 0;
	//	}
	//	return Short.toUnsignedInt(arr[SectionDataHolder.getIndex(x, y, z)]);
	//}

	//public void setPressure(int x, int y, int z, int pressure) {
	//	short[] arr = this.pressure;
	//	if (arr == null) {
	//		arr = new short[4096];
	//		this.pressure = arr;
	//	}
	//	arr[SectionDataHolder.getIndex(x, y, z)] = (short) pressure;
	//}

	@Override
	public int getDataSize() {
		if (storage == null) return 1;
		return storage.getDataSize();
	}

	@Override
	public void read(FriendlyByteBuf buf) {
		byte bits = buf.readByte();
		if (bits == -1) {
			this.storage = null;
		} else {
			if (this.storage == null) {
				this.storage = new PalettedStorage<>(4096, defaultValue, sup);
			}
			if (bits == 0) {
				int id = buf.readVarInt();
				this.storage.setDefaultValue(sup.get(id));
			} else {
				final int len = buf.readVarInt();
				long[] data = new long[len];
				for (int i = 0; i < len; i++) {
					data[i] = buf.readLong();
				}
				this.storage.setData(new PalettedData(bits, 4096, data));
			}
		}
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		if (storage == null) {
			buf.writeByte(-1);
		} else if (storage.isSingle()) {
			buf.writeByte(0); // Bits Per Entry
			buf.writeVarInt(sup.getIndex(defaultValue)); // Palette
			//buf.writeVarInt(0); //Data Array Length
			// empty //Data Array
		} else {
			buf.writeByte(storage.bits); // Bits Per Entry
			buf.writeVarInt(storage.getData().words.length); //Data Array Length
			for (int i = 0; i < storage.getData().words.length; i++) {
				buf.writeLong(storage.getData().words[i]);
			}
		}
	}

	@Override
	public CompoundTag writeNBT() {
		final CompoundTag nbt = new CompoundTag();
		if (!ticks.isEmpty()) {
			ListTag list = new ListTag();
			for (FluidTickTask t : this.ticks) {
				CompoundTag taskTag = new CompoundTag();
				taskTag.putString("id", BuiltInRegistries.FLUID.getKey(t.fluid).toString());
				taskTag.putLong("pos", t.pos.asLong());
				taskTag.putInt("time", t.startTick);
			}
			nbt.put("ticks", list);
		}
		if (storage != null) {
			final ListTag states = new ListTag();
			if (storage.isSingle()) {
				states.add(NbtUtils.writeFluidState(storage.getDefaultValue()));
			} else {
				final PalettedData remappedData = getRemappedData(states, storage.getData());
				nbt.put("data", new LongArrayTag(remappedData.words));
			}
			nbt.put("states", states);
		}
		//short[] pressure = this.pressure;
		//if (pressure != null) {
		//	int[] pa = new int[4096];
		//	for (int i = 0; i < 4096; i++) {
		//		pa[i] = pressure[i];
		//	}
		//	IntArrayTag pl = new IntArrayTag(pa);
		//	nbt.put("pressure", pl);
		//}
		return nbt;
	}

	private PalettedData getRemappedData(ListTag states, PalettedData data) {
		final Int2IntMap values = new Int2IntOpenHashMap(4);
		final Holders.IntHolder c = new Holders.IntHolder(-1);
		for (int i = 0; i < 4096; i++) {
			int v = data.getValue(i);
			values.computeIfAbsent(v, v2 -> {
				states.add(NbtUtils.writeFluidState(sup.get(v)));
				return c.increment(1);
			});
		}
		final PalettedData remappedData = new PalettedData(PalettedStorage.calcBits(values.size()), 4096);
		for (int i = 0; i < 4096; i++) {
			int v = data.getValue(i);
			remappedData.setValue(i, values.get(v));
		}
		return remappedData;
	}

	@Override
	public void readNBT(CompoundTag nbt, ServerLevel serverLevel) {
		final ListTag tickList = nbt.getList("ticks", Tag.TAG_COMPOUND);
		if (!tickList.isEmpty()) {
			//this.ticks = new ArrayList<>(tickList.size());
			for (int i = 0; i < tickList.size(); i++) {
				CompoundTag tickTag = tickList.getCompound(i);
				Fluid f = BuiltInRegistries.FLUID.get(ResourceLocation.parse(tickTag.getString("id")));
				if (!(f instanceof FlowingFluid flowing)) {
					continue;
				}
				BlockPos pos = BlockPos.of(tickTag.getLong("pos"));
				int time = tickTag.getInt("time");
				this.ticks.add(new FluidTickTask(pos, flowing, time));
			}
		}

		//int[] pl = nbt.getIntArray("pressure");
		//if (pl.length == 4096) {
		//	short[] pressure = new short[4096];
		//	for (int i = 0; i < 4096; i++) {
		//		pressure[i] = (short) pl[i];
		//	}
		//	this.pressure = pressure;
		//}

		ListTag statesNbt = nbt.getList("states", ListTag.TAG_COMPOUND);
		if (!statesNbt.isEmpty()) {

			List<FluidState> states = new ArrayList<>();
			for (int i = 0; i < statesNbt.size(); i++) {
				states.add(readFluidState(statesNbt.getCompound(i)));
			}
			if (storage == null) {
				storage = new PalettedStorage<>(4096, defaultValue, sup);
			}
			if (states.size() == 1) {
				storage.setDefaultValue(states.get(0));
			} else {
				long[] data = nbt.getLongArray("data");
				final PalettedData remappedData = new PalettedData(PalettedStorage.calcBits(states.size()), 4096, data);
				final PalettedData myData = storage.getData();
				for (int i = 0; i < 4096; i++) {
					int v = remappedData.getValue(i);
					int v2 = sup.getIndex(states.get(v));
					myData.setValue(i, v2);
				}
			}
		}
	}

	private FluidState readFluidState(CompoundTag tag) {
		if (!tag.contains("Name", ListTag.TAG_STRING)) {
			return defaultValue;
		} else {
			ResourceLocation resourcelocation = ResourceLocation.parse(tag.getString("Name"));
			Fluid fluid = BuiltInRegistries.FLUID.get(resourcelocation);
			FluidState fluidState = fluid.defaultFluidState();
			if (tag.contains("Properties", 10)) {
				CompoundTag compoundtag = tag.getCompound("Properties");
				StateDefinition<Fluid, FluidState> statedefinition = fluid.getStateDefinition();
				for (String s : compoundtag.getAllKeys()) {
					Property<?> property = statedefinition.getProperty(s);
					if (property != null) {
						fluidState = setValueHelper(fluidState, property, s, compoundtag);
					}
				}
			}

			return fluidState;
		}
	}

	private static <S extends StateHolder<?, S>, T extends Comparable<T>> S setValueHelper(S state, Property<T> property, String name, CompoundTag tag) {
		Optional<T> optional = property.getValue(tag.getString(name));
		//LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", name, tag.getString(name), p_129209_);
		return optional.map(t -> state.setValue(property, t)).orElse(state);
	}
}
