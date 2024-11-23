package net.skds.lib2.mat;

public record Vec2D(double x, double y) implements Vec2 {

	@Override
	public float xf() {
		return (float) x;
	}

	@Override
	public float yf() {
		return (float) y;
	}
}
