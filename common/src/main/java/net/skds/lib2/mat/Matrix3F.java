package net.skds.lib2.mat;

public record Matrix3F(
		float m00f, float m01f, float m02f,
		float m10f, float m11f, float m12f,
		float m20f, float m21f, float m22f
) implements Matrix3 {


	public static final Matrix3F SINGLE = new Matrix3F(1, 0, 0, 0, 1, 0, 0, 0, 1);

	@Override
	public double m00() {
		return m00f;
	}

	@Override
	public double m01() {
		return m01f;
	}

	@Override
	public double m02() {
		return m02f;
	}

	@Override
	public double m10() {
		return m10f;
	}

	@Override
	public double m11() {
		return m11f;
	}

	@Override
	public double m12() {
		return m12f;
	}

	@Override
	public double m20() {
		return m20f;
	}

	@Override
	public double m21() {
		return m21f;
	}

	@Override
	public double m22() {
		return m22f;
	}


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
		int i = this.m00f != 0.0F ? Float.floatToIntBits(this.m00f) : 0;
		i = 31 * i + (this.m01f != 0.0F ? Float.floatToIntBits(this.m01f) : 0);
		i = 31 * i + (this.m02f != 0.0F ? Float.floatToIntBits(this.m02f) : 0);
		i = 31 * i + (this.m10f != 0.0F ? Float.floatToIntBits(this.m10f) : 0);
		i = 31 * i + (this.m11f != 0.0F ? Float.floatToIntBits(this.m11f) : 0);
		i = 31 * i + (this.m12f != 0.0F ? Float.floatToIntBits(this.m12f) : 0);
		i = 31 * i + (this.m20f != 0.0F ? Float.floatToIntBits(this.m20f) : 0);
		i = 31 * i + (this.m21f != 0.0F ? Float.floatToIntBits(this.m21f) : 0);
		return 31 * i + (this.m22f != 0.0F ? Float.floatToIntBits(this.m22f) : 0);
	}
}