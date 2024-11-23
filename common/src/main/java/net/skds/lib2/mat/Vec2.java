package net.skds.lib2.mat;

public sealed interface Vec2 extends IVec permits Vec2D, Vec2F {

	@Override
	default int dimension() {
		return 2;
	}

	double x();

	double y();

	float xf();

	float yf();

	default int floorX() {
		return FastMath.floor(x());
	}

	default int floorY() {
		return FastMath.floor(y());
	}

	default int ceilX() {
		return FastMath.ceil(x());
	}

	default int ceilY() {
		return FastMath.ceil(y());
	}

	@Override
	default double get(int i) {
		return switch (i) {
			case 0 -> x();
			case 1 -> y();
			default -> throw new ArrayIndexOutOfBoundsException(i);
		};
	}

	@Override
	default float getF(int i) {
		return switch (i) {
			case 0 -> xf();
			case 1 -> yf();
			default -> throw new ArrayIndexOutOfBoundsException(i);
		};
	}


	@Override
	default int floor(int i) {
		return switch (i) {
			case 0 -> floorX();
			case 1 -> floorY();
			default -> throw new ArrayIndexOutOfBoundsException(i);
		};
	}

	@Override
	default int ceil(int i) {
		return switch (i) {
			case 0 -> ceilX();
			case 1 -> ceilY();
			default -> throw new ArrayIndexOutOfBoundsException(i);
		};
	}
}
