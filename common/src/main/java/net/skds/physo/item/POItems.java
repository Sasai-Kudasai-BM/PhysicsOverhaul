package net.skds.physo.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.skds.physo.PhysicsOverhaul;

public class POItems {


	public static final DebugStickItem DEBUG_STICK = register("debug_stick", new DebugStickItem(new Item.Properties()));

	public static void init() {
	}

	private static <T extends Item> T register(String id, T item) {
		return Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(PhysicsOverhaul.MOD_ID, id), item);
	}
}
