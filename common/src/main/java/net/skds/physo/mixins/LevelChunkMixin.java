package net.skds.physo.mixins;

import net.minecraft.world.level.chunk.LevelChunk;
import net.skds.physo.Events;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin {

	@Shadow
	private boolean loaded;

	@SuppressWarnings("DataFlowIssue")
	@Inject(method = "setLoaded", at = @At("HEAD"))
	public void setLoaded(boolean bl, CallbackInfo ci) {
		if (loaded != bl) {
			if (bl) {
				Events.loadChunk((LevelChunk) (Object) this);
			} else {
				Events.unloadChunk((LevelChunk) (Object) this);
			}
		}
	}
}
