package net.skds.lib2.mat;

@SuppressWarnings("unused")
public interface Vec4 extends IVec {

	@Override
	default int dimension() {
		return 4;
	}

	double x();

	double y();

	double z();

	double w();

	@Override
	default double get(int i) {
		return switch (i) {
			case 0 -> x();
			case 1 -> y();
			case 2 -> z();
			case 3 -> w();
			default -> throw new ArrayIndexOutOfBoundsException(i);
		};
	}

	@Override
	default float getF(int i) {
		return switch (i) {
			case 0 -> xf();
			case 1 -> yf();
			case 2 -> zf();
			case 3 -> wf();
			default -> throw new ArrayIndexOutOfBoundsException(i);
		};
	}


	@Override
	default int floor(int i) {
		return switch (i) {
			case 0 -> floorX();
			case 1 -> floorY();
			case 2 -> floorZ();
			case 3 -> floorW();
			default -> throw new ArrayIndexOutOfBoundsException(i);
		};
	}

	@Override
	default int ceil(int i) {
		return switch (i) {
			case 0 -> ceilX();
			case 1 -> ceilY();
			case 2 -> ceilZ();
			case 3 -> ceilW();
			default -> throw new ArrayIndexOutOfBoundsException(i);
		};
	}

	default float xf() {
		return (float) x();
	}

	default float yf() {
		return (float) y();
	}

	default float zf() {
		return (float) z();
	}

	default float wf() {
		return (float) w();
	}

	default int floorX() {
		return FastMath.floor(x());
	}

	default int floorY() {
		return FastMath.floor(y());
	}

	default int floorZ() {
		return FastMath.floor(z());
	}

	default int floorW() {
		return FastMath.floor(w());
	}


	default int ceilX() {
		return FastMath.ceil(x());
	}

	default int ceilY() {
		return FastMath.ceil(y());
	}

	default int ceilZ() {
		return FastMath.ceil(z());
	}

	default int ceilW() {
		return FastMath.ceil(w());
	}

	default double length() {
		return Math.sqrt(this.x() * this.x() + this.y() * this.y() + this.z() * this.z() + this.w() * this.w());
	}

	default double lengthSquared() {
		return this.x() * this.x() + this.y() * this.y() + this.z() * this.z() + this.w() * this.w();
	}

	static double length(double x, double y, double z, double w) {
		return Math.sqrt(x * x + y * y + z * z + w * w);
	}

	static double lengthSquared(double x, double y, double z, double w) {
		return x * x + y * y + z * z + w * w;
	}

	static float lengthF(float x, float y, float z, float w) {
		return (float) Math.sqrt(x * x + y * y + z * z + w * w);
	}

	static float lengthSquaredF(float x, float y, float z, float w) {
		return x * x + y * y + z * z + w * w;
	}

	default float lengthF() {
		return (float) Math.sqrt(this.xf() * this.xf() + this.yf() * this.yf() + this.zf() * this.zf() + this.wf() * this.wf());
	}

	default float lengthSquaredF() {
		return this.xf() * this.xf() + this.yf() * this.yf() + this.zf() * this.zf() + this.wf() * this.wf();
	}

}
