package net.skds.physo.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.skds.physo.client.PhysicsOverhaulClient;

public final class PhysicsOverhaulFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PhysicsOverhaulClient.init();
    }
}
