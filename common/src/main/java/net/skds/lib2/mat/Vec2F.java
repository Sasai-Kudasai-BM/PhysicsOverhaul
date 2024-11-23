package net.skds.lib2.mat;

public record Vec2F(float xf, float yf) implements Vec2 {
	@Override
	public double x() {
		return xf;
	}

	@Override
	public double y() {
		return yf;
	}
}
