package net.skds.lib2.mat;


public record QuatD(double x, double y, double z, double w) implements Quat {

	public static final QuatD ONE = new QuatD(0, 0, 0, 1);
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof Quat quat) {
			return Quat.equals(this, quat);
		}
		return false;
	}

	public int hashCode() {
		int i = Float.floatToIntBits((float) this.x);
		i = 31 * i + Float.floatToIntBits((float) this.y);
		i = 31 * i + Float.floatToIntBits((float) this.z);
		return 31 * i + Float.floatToIntBits((float) this.w);
	}

}