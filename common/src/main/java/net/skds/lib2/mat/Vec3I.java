package net.skds.lib2.mat;

@SuppressWarnings("unused")
public record Vec3I(int xi, int yi, int zi) implements Vec3 {

	public static final Vec3I XN = new Vec3I(-1, 0, 0);
	public static final Vec3I XP = new Vec3I(1, 0, 0);
	public static final Vec3I YN = new Vec3I(0, -1, 0);
	public static final Vec3I YP = new Vec3I(0, 1, 0);
	public static final Vec3I ZN = new Vec3I(0, 0, -1);
	public static final Vec3I ZP = new Vec3I(0, 0, 1);
	public static final Vec3I SINGLE = new Vec3I(1, 1, 1);
	public static final Vec3I ZERO = new Vec3I(0, 0, 0);

	@Override
	public double x() {
		return xi;
	}

	@Override
	public double y() {
		return yi;
	}

	@Override
	public double z() {
		return zi;
	}

	@Override
	public float xf() {
		return xi;
	}

	@Override
	public float yf() {
		return yi;
	}

	@Override
	public float zf() {
		return zi;
	}

	@Override
	public int floorX() {
		return xi;
	}

	@Override
	public int floorY() {
		return yi;
	}

	@Override
	public int floorZ() {
		return zi;
	}

	@Override
	public int ceilX() {
		return xi;
	}

	@Override
	public int ceilY() {
		return yi;
	}

	@Override
	public int ceilZ() {
		return zi;
	}

	public Vec3I(Vec3 vec) {
		this(vec.floorX(), vec.floorY(), vec.floorZ());
	}

	public Vec3I(int size) {
		this(size, size, size);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof Vec3 vec) {
			return Vec3.equals(this, vec);
		}
		return false;
	}

	public int hashCode() {
		int i = xi;
		i = 31 * i + yi;
		return 31 * i + zi;
	}
}