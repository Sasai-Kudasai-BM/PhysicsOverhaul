package net.skds.physo.fabric;

import net.fabricmc.api.ModInitializer;

import net.skds.physo.PhysicsOverhaul;

public final class PhysicsOverhaulFabric implements ModInitializer {
    @Override
    public void onInitialize() {

        PhysicsOverhaul.init();
    }
}
