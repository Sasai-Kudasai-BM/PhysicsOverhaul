package net.skds.physo.utils;

import net.minecraft.world.level.Level;

import java.util.function.Predicate;

public enum WorldSide implements Predicate<Level> {
	CLIENT {
		@Override
		public boolean test(Level level) {
			return level.isClientSide;
		}
	},
	SERVER {
		@Override
		public boolean test(Level level) {
			return !level.isClientSide;
		}
	},
	BOTH {
		@Override
		public boolean test(Level level) {
			return true;
		}
	};

}
