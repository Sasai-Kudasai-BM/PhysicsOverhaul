package net.skds.physo.mixins;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerChunkCache.class)
public interface ChunkSourceInvoker {

	@Invoker("getVisibleChunkIfPresent")
	ChunkHolder getLoadedChunkAsync(long pos);
}
