package net.skds.physo.mixins;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.FluidState;
import net.skds.physo.PhysicsOverhaul;
import net.skds.physo.mixinglue.ChunkSectionGlue;
import net.skds.physo.world.SectionDataHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelChunkSection.class)
public abstract class ChunkSectionMixin implements ChunkSectionGlue {

	@SuppressWarnings("DataFlowIssue")
	SectionDataHolder dataHolder = new SectionDataHolder((LevelChunkSection) (Object) this);

	@Override
	public SectionDataHolder getDataHolder() {
		return dataHolder;
	}

	@Inject(method = "read", at = @At("TAIL"))
	void read(FriendlyByteBuf buf, CallbackInfo ci) {
		dataHolder.read(buf);
	}

	@Inject(method = "write", at = @At("TAIL"))
	void write(FriendlyByteBuf buf, CallbackInfo ci) {
		dataHolder.write(buf);
	}

	@Inject(method = "getSerializedSize", at = @At("RETURN"), cancellable = true)
	void getSerializedSize(CallbackInfoReturnable<Integer> ci) {
		int value = ci.getReturnValueI();
		ci.setReturnValue(value + dataHolder.getExtraSize());
	}

	@Inject(method = "getFluidState", at = @At("HEAD"), cancellable = true)
	void getFluidState(int x, int y, int z, CallbackInfoReturnable<FluidState> ci) {
		FluidState state = getDataHolder().getData(PhysicsOverhaul.FLUID_SECTION_DATA).getFluidState(x, y, z);
		ci.setReturnValue(state);
	}

	//@Inject(method = "setBlockState", at = @At("RETURN"))
	//public void setBlockState(int x, int y, int z, BlockState blockState, boolean bl, CallbackInfoReturnable<BlockState> ci) {
	//	FluidSectionData fd = getDataHolder().getData(PhysicsOverhaul.FLUID_SECTION_DATA);
	//	if (fd.getFluidState(x, y, z) != blockState.getFluidState()) {
	//		FluidUtils.onSetBlockSection(fd, x, y, z, ci.getReturnValue(), blockState);
	//	}
	//}

}
