package net.skds.lib2.mat;

@SuppressWarnings("unused")
public record Vec3D(double x, double y, double z) implements Vec3 {

	public static final Vec3D XN = new Vec3D(-1.0D, 0.0D, 0.0D);
	public static final Vec3D XP = new Vec3D(1.0D, 0.0D, 0.0D);
	public static final Vec3D YN = new Vec3D(0.0D, -1.0D, 0.0D);
	public static final Vec3D YP = new Vec3D(0.0D, 1.0D, 0.0D);
	public static final Vec3D ZN = new Vec3D(0.0D, 0.0D, -1.0D);
	public static final Vec3D ZP = new Vec3D(0.0D, 0.0D, 1.0D);
	public static final Vec3D SINGLE = new Vec3D(1.0D, 1.0D, 1.0D);
	public static final Vec3D ZERO = new Vec3D(0.0D, 0.0D, 0.0D);
	
	public Vec3D(Vec3 vec) {
		this(vec.x(), vec.y(), vec.z());
	}

	public Vec3D(double size) {
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
		int i = Float.floatToIntBits((float) this.x);
		i = 31 * i + Float.floatToIntBits((float) this.y);
		return 31 * i + Float.floatToIntBits((float) this.z);
	}
}