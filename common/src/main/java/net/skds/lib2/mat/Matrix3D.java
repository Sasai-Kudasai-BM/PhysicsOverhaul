package net.skds.lib2.mat;


public record Matrix3D(
		double m00, double m01, double m02,
		double m10, double m11, double m12,
		double m20, double m21, double m22
) implements Matrix3 {

	public static final Matrix3D SINGLE = new Matrix3D(1, 0, 0, 0, 1, 0, 0, 0, 1);

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof Matrix3 m) {
			return Matrix3.equals(this, m);
		}
		return false;
	}

	public int hashCode() {
		int i = this.m00 != 0.0F ? Float.floatToIntBits((float) this.m00) : 0;
		i = 31 * i + (this.m01 != 0.0F ? Float.floatToIntBits((float) this.m01) : 0);
		i = 31 * i + (this.m02 != 0.0F ? Float.floatToIntBits((float) this.m02) : 0);
		i = 31 * i + (this.m10 != 0.0F ? Float.floatToIntBits((float) this.m10) : 0);
		i = 31 * i + (this.m11 != 0.0F ? Float.floatToIntBits((float) this.m11) : 0);
		i = 31 * i + (this.m12 != 0.0F ? Float.floatToIntBits((float) this.m12) : 0);
		i = 31 * i + (this.m20 != 0.0F ? Float.floatToIntBits((float) this.m20) : 0);
		i = 31 * i + (this.m21 != 0.0F ? Float.floatToIntBits((float) this.m21) : 0);
		return 31 * i + (this.m22 != 0.0F ? Float.floatToIntBits((float) this.m22) : 0);
	}
}