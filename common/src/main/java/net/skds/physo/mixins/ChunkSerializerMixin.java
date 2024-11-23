package net.skds.physo.mixins;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import net.skds.physo.Events;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkSerializer.class)
public abstract class ChunkSerializerMixin {


	@Inject(method = "read", at = @At("RETURN"))
	private static void read(ServerLevel serverLevel, PoiManager poiManager, RegionStorageInfo regionStorageInfo, ChunkPos chunkPos, CompoundTag compoundTag, CallbackInfoReturnable<ProtoChunk> ci) {
		ProtoChunk chunk = ci.getReturnValue();
		Events.readChunk(chunk, compoundTag, serverLevel);
	}

	@Inject(method = "write", at = @At("RETURN"))
	private static void write(ServerLevel serverLevel, ChunkAccess chunkAccess, CallbackInfoReturnable<CompoundTag> ci) {
		CompoundTag tag = ci.getReturnValue();
		Events.saveChunk(chunkAccess, tag);
	}
}
