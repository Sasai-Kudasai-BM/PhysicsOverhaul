package net.skds.physo.neoforge.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.skds.physo.PhysicsOverhaul;
import net.skds.physo.client.PhysicsOverhaulClient;

@Mod(value = PhysicsOverhaul.MOD_ID, dist = Dist.CLIENT)
public final class PhysicsOverhaulForgeClient {
	public PhysicsOverhaulForgeClient() {
		PhysicsOverhaulClient.init();
	}
}
