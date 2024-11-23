package net.skds.physo.mixins;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.ticks.LevelTicks;
import net.skds.physo.world.CustomWorldTicks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level {

	@SuppressWarnings({"DataFlowIssue", "FieldMayBeFinal"})
	@Final
	@Mutable
	@Shadow
	private LevelTicks<Fluid> fluidTicks = new CustomWorldTicks(this::isPositionTickingWithEntitiesLoaded, this.getProfilerSupplier(), (ServerLevel) (Object) this);

	protected ServerLevelMixin(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, RegistryAccess registryAccess, Holder<DimensionType> holder, Supplier<ProfilerFiller> supplier, boolean bl, boolean bl2, long l, int i) {
		super(writableLevelData, resourceKey, registryAccess, holder, supplier, bl, bl2, l, i);
	}

	@Shadow
	private boolean isPositionTickingWithEntitiesLoaded(long p) {
		return false;
	}
}
