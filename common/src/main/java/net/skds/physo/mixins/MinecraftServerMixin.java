package net.skds.physo.mixins;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.profiling.ProfilerFiller;
import net.skds.physo.Events;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

	@Shadow
	ProfilerFiller profiler;

	@Inject(method = "tickChildren", at = @At("HEAD"))
	void tickChildren(BooleanSupplier bs, CallbackInfo ci) {
		profiler.push("SKDS AfterTick");
		//noinspection DataFlowIssue
		Events.serverTickStart((MinecraftServer) (Object) this, bs);
		profiler.pop();
	}

	@Inject(method = "tickChildren", at = @At("TAIL"))
	void tickChildren2(BooleanSupplier bs, CallbackInfo ci) {
		profiler.push("SKDS AfterTick");
		//noinspection DataFlowIssue
		Events.serverTickEnd((MinecraftServer) (Object) this, bs);
		profiler.pop();
	}
}
