package net.skds.physo;

import net.skds.physo.fluids.DirectFluidSup;
import net.skds.physo.fluids.FluidSectionData;
import net.skds.physo.item.POItems;
import net.skds.physo.utils.WorldSide;
import net.skds.physo.world.SectionDataRegistry;
import net.skds.physo.world.SectionDataRegistryEntry;

public final class PhysicsOverhaul {
	public static final String MOD_ID = "physo";

	public static final SectionDataRegistryEntry<FluidSectionData> FLUID_SECTION_DATA = SectionDataRegistry
			.regSectionData(FluidSectionData::new, WorldSide.BOTH, "fluidData");

	public static void init() {
		Events.reload();
		POItems.init();
		DirectFluidSup.INSTANCE.init();
	}
}
