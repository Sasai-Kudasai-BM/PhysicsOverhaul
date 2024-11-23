package net.skds.lib2.mat;

import net.skds.lib2.utils.linkiges.Obj2DoublePair;
import net.skds.lib2.utils.linkiges.Obj2FloatPair;

@SuppressWarnings("unused")
public sealed interface Matrix3 permits Matrix3D, Matrix3F {

	Matrix3 SINGLE = Matrix3D.SINGLE;

	double m00();

	double m01();

	double m02();

	double m10();

	double m11();

	double m12();

	double m20();

	double m21();

	double m22();

	default float m00f() {
		return (float) m00();
	}

	default float m01f() {
		return (float) m01();
	}

	default float m02f() {
		return (float) m02();
	}

	default float m10f() {
		return (float) m10();
	}

	default float m11f() {
		return (float) m11();
	}

	default float m12f() {
		return (float) m12();
	}

	default float m20f() {
		return (float) m20();
	}

	default float m21f() {
		return (float) m21();
	}

	default float m22f() {
		return (float) m22();
	}

	static boolean equals(Matrix3 m1, Matrix3 m2) {
		if (m1 == m2) {
			return true;
		} else if ((m1 == null) != (m2 == null)) {
			return false;
		} else {
			return Double.compare(m1.m00(), m2.m00()) == 0 && Double.compare(m1.m01(), m2.m01()) == 0
					&& Double.compare(m1.m02(), m2.m02()) == 0 && Double.compare(m1.m10(), m2.m10()) == 0
					&& Double.compare(m1.m11(), m2.m11()) == 0 && Double.compare(m1.m12(), m2.m12()) == 0
					&& Double.compare(m1.m20(), m2.m20()) == 0 && Double.compare(m1.m21(), m2.m21()) == 0
					&& Double.compare(m1.m22(), m2.m22()) == 0;
		}
	}

	static Matrix3D fromNormals(Vec3[] normals) {
		return new Matrix3D(
				normals[0].x(),
				normals[1].x(),
				normals[2].x(),
				normals[0].y(),
				normals[1].y(),
				normals[2].y(),
				normals[0].z(),
				normals[1].z(),
				normals[2].z()
		);
	}

	static Matrix3F fromNormalsF(Vec3[] normals) {
		return new Matrix3F(
				normals[0].xf(),
				normals[1].xf(),
				normals[2].xf(),
				normals[0].yf(),
				normals[1].yf(),
				normals[2].yf(),
				normals[0].zf(),
				normals[1].zf(),
				normals[2].zf()
		);
	}

	static Matrix3D fromNormals(Vec3 l, Vec3 u, Vec3 f) {
		return new Matrix3D(
				l.x(),
				u.x(),
				f.x(),
				l.y(),
				u.y(),
				f.y(),
				l.z(),
				u.z(),
				f.z()
		);
	}

	static Matrix3F fromNormalsF(Vec3 l, Vec3 u, Vec3 f) {
		return new Matrix3F(
				l.xf(),
				u.xf(),
				f.xf(),
				l.yf(),
				u.yf(),
				f.yf(),
				l.zf(),
				u.zf(),
				f.zf()
		);
	}

	static Matrix3D fromQuat(Quat q) {
		double x = q.x();
		double y = q.y();
		double z = q.z();
		double w = q.w();
		double fx22 = 2.0 * x * x;
		double fy22 = 2.0 * y * y;
		double fz22 = 2.0 * z * z;
		double xy = x * y;
		double yz = y * z;
		double zx = z * x;
		double xw = x * w;
		double yw = y * w;
		double zw = z * w;

		return new Matrix3D(
				1.0 - fy22 - fz22,
				2.0 * (xy - zw),
				2.0 * (zx + yw),
				2.0 * (xy + zw),
				1.0 - fz22 - fx22,
				2.0 * (yz - xw),
				2.0 * (zx - yw),
				2.0 * (yz + xw),
				1.0 - fx22 - fy22
		);
	}

	static Matrix3D fromQuatNS(Quat q, double sx, double sy, double sz) {
		double x = q.x();
		double y = q.y();
		double z = q.z();
		double w = q.w();
		double fx22 = 2.0 * x * x;
		double fy22 = 2.0 * y * y;
		double fz22 = 2.0 * z * z;
		double xy = x * y;
		double yz = y * z;
		double zx = z * x;
		double xw = x * w;
		double yw = y * w;
		double zw = z * w;

		return new Matrix3D(
				sx * (1.0 - fy22 - fz22),
				sy * (2.0 * (xy - zw)),
				sz * (2.0 * (zx + yw)),
				sx * (2.0 * (xy + zw)),
				sy * (1.0 - fz22 - fx22),
				sz * (2.0 * (yz - xw)),
				sx * (2.0 * (zx - yw)),
				sy * (2.0 * (yz + xw)),
				sz * (1.0 - fx22 - fy22)
		);
	}

	static Matrix3F fromQuatF(Quat q) {
		float x = q.xf();
		float y = q.yf();
		float z = q.zf();
		float w = q.wf();
		float fx22 = 2.0f * x * x;
		float fy22 = 2.0f * y * y;
		float fz22 = 2.0f * z * z;
		float xy = x * y;
		float yz = y * z;
		float zx = z * x;
		float xw = x * w;
		float yw = y * w;
		float zw = z * w;

		return new Matrix3F(
				1.0f - fy22 - fz22,
				2.0f * (xy - zw),
				2.0f * (zx + yw),
				2.0f * (xy + zw),
				1.0f - fz22 - fx22,
				2.0f * (yz - xw),
				2.0f * (zx - yw),
				2.0f * (yz + xw),
				1.0f - fx22 - fy22
		);
	}

	static Matrix3F fromQuatNSF(Quat q, float sx, float sy, float sz) {
		float x = q.xf();
		float y = q.yf();
		float z = q.zf();
		float w = q.wf();
		float fx22 = 2.0f * x * x;
		float fy22 = 2.0f * y * y;
		float fz22 = 2.0f * z * z;
		float xy = x * y;
		float yz = y * z;
		float zx = z * x;
		float xw = x * w;
		float yw = y * w;
		float zw = z * w;

		return new Matrix3F(
				sx * (1.0f - fy22 - fz22),
				sy * (2.0f * (xy - zw)),
				sz * (2.0f * (zx + yw)),
				sx * (2.0f * (xy + zw)),
				sy * (1.0f - fz22 - fx22),
				sz * (2.0f * (yz - xw)),
				sx * (2.0f * (zx - yw)),
				sy * (2.0f * (yz + xw)),
				sz * (1.0f - fx22 - fy22)
		);
	}

	static Matrix3D fromForward(Vec3 forward) {
		if (forward.x() == 0 && forward.z() == 0) {
			return fromNormals(Vec3.XP, forward.cross(Vec3.XP), forward);
		}
		Vec3 left = Vec3.normalized(forward.z(), 0, -forward.x());
		return fromNormals(left, forward.cross(left), forward);
	}

	static Matrix3F fromForwardF(Vec3 forward) {
		if (forward.x() == 0 && forward.z() == 0) {
			return fromNormalsF(Vec3.XP, forward.crossF(Vec3.XP), forward);
		}
		Vec3 left = Vec3.normalizedF(forward.zf(), 0, -forward.xf());
		return fromNormalsF(left, forward.crossF(left), forward);
	}

	default Matrix3D transpose() {
		return new Matrix3D(
				m00(), m10(), m20(),
				m01(), m11(), m21(),
				m02(), m12(), m22()
		);
	}

	default Matrix3F transposeF() {
		return new Matrix3F(
				m00f(), m10f(), m20f(),
				m01f(), m11f(), m21f(),
				m02f(), m12f(), m22f()
		);
	}

	default Matrix3D invert() {
		double f = this.m11() * this.m22() - this.m12() * this.m21();
		double f1 = -(this.m10() * this.m22() - this.m12() * this.m20());
		double f2 = this.m10() * this.m21() - this.m11() * this.m20();
		double f3 = -(this.m01() * this.m22() - this.m02() * this.m21());
		double f4 = this.m00() * this.m22() - this.m02() * this.m20();
		double f5 = -(this.m00() * this.m21() - this.m01() * this.m20());
		double f6 = this.m01() * this.m12() - this.m02() * this.m11();
		double f7 = -(this.m00() * this.m12() - this.m02() * this.m10());
		double f8 = this.m00() * this.m11() - this.m01() * this.m10();
		double det = this.m00() * f + this.m01() * f1 + this.m02() * f2;
		return new Matrix3D(
				f * det,
				f1 * det,
				f2 * det,
				f3 * det,
				f4 * det,
				f5 * det,
				f6 * det,
				f7 * det,
				f8 * det
		);
	}

	default Matrix3F invertF() {
		float f = this.m11f() * this.m22f() - this.m12f() * this.m21f();
		float f1 = -(this.m10f() * this.m22f() - this.m12f() * this.m20f());
		float f2 = this.m10f() * this.m21f() - this.m11f() * this.m20f();
		float f3 = -(this.m01f() * this.m22f() - this.m02f() * this.m21f());
		float f4 = this.m00f() * this.m22f() - this.m02f() * this.m20f();
		float f5 = -(this.m00f() * this.m21f() - this.m01f() * this.m20f());
		float f6 = this.m01f() * this.m12f() - this.m02f() * this.m11f();
		float f7 = -(this.m00f() * this.m12f() - this.m02f() * this.m10f());
		float f8 = this.m00f() * this.m11f() - this.m01f() * this.m10f();
		float det = this.m00f() * f + this.m01f() * f1 + this.m02f() * f2;
		return new Matrix3F(
				f * det,
				f1 * det,
				f2 * det,
				f3 * det,
				f4 * det,
				f5 * det,
				f6 * det,
				f7 * det,
				f8 * det
		);
	}

	default Obj2DoublePair<Matrix3D> adjugateAndDet() {
		double f = this.m11() * this.m22() - this.m12() * this.m21();
		double f1 = -(this.m10() * this.m22() - this.m12() * this.m20());
		double f2 = this.m10() * this.m21() - this.m11() * this.m20();
		double f3 = -(this.m01() * this.m22() - this.m02() * this.m21());
		double f4 = this.m00() * this.m22() - this.m02() * this.m20();
		double f5 = -(this.m00() * this.m21() - this.m01() * this.m20());
		double f6 = this.m01() * this.m12() - this.m02() * this.m11();
		double f7 = -(this.m00() * this.m12() - this.m02() * this.m10());
		double f8 = this.m00() * this.m11() - this.m01() * this.m10();
		double det = this.m00() * f + this.m01() * f1 + this.m02() * f2;
		Matrix3D m = new Matrix3D(
				f,
				f1,
				f2,
				f3,
				f4,
				f5,
				f6,
				f7,
				f8
		);
		return new Obj2DoublePair<>(m, det);
	}

	default Obj2FloatPair<Matrix3F> adjugateAndDetF() {
		float f = this.m11f() * this.m22f() - this.m12f() * this.m21f();
		float f1 = -(this.m10f() * this.m22f() - this.m12f() * this.m20f());
		float f2 = this.m10f() * this.m21f() - this.m11f() * this.m20f();
		float f3 = -(this.m01f() * this.m22f() - this.m02f() * this.m21f());
		float f4 = this.m00f() * this.m22f() - this.m02f() * this.m20f();
		float f5 = -(this.m00f() * this.m21f() - this.m01f() * this.m20f());
		float f6 = this.m01f() * this.m12f() - this.m02f() * this.m11f();
		float f7 = -(this.m00f() * this.m12f() - this.m02f() * this.m10f());
		float f8 = this.m00f() * this.m11f() - this.m01f() * this.m10f();
		float det = this.m00f() * f + this.m01f() * f1 + this.m02f() * f2;
		Matrix3F m = new Matrix3F(
				f,
				f1,
				f2,
				f3,
				f4,
				f5,
				f6,
				f7,
				f8
		);
		return new Obj2FloatPair<>(m, det);
	}

	default double det() {
		double f = this.m11() * this.m22() - this.m12() * this.m21();
		double f1 = -(this.m10() * this.m22() - this.m12() * this.m20());
		double f2 = this.m10() * this.m21() - this.m11() * this.m20();
		return this.m00() * f + this.m01() * f1 + this.m02() * f2;
	}

	default float detF() {
		float f = this.m11f() * this.m22f() - this.m12f() * this.m21f();
		float f1 = -(this.m10f() * this.m22f() - this.m12f() * this.m20f());
		float f2 = this.m10f() * this.m21f() - this.m11f() * this.m20f();
		return this.m00f() * f + this.m01f() * f1 + this.m02f() * f2;
	}

	default Matrix3D multiply(Matrix3 m2) {
		return new Matrix3D(
				this.m00() * m2.m00() + this.m01() * m2.m10() + this.m02() * m2.m20(),
				this.m00() * m2.m01() + this.m01() * m2.m11() + this.m02() * m2.m21(),
				this.m00() * m2.m02() + this.m01() * m2.m12() + this.m02() * m2.m22(),
				this.m10() * m2.m00() + this.m11() * m2.m10() + this.m12() * m2.m20(),
				this.m10() * m2.m01() + this.m11() * m2.m11() + this.m12() * m2.m21(),
				this.m10() * m2.m02() + this.m11() * m2.m12() + this.m12() * m2.m22(),
				this.m20() * m2.m00() + this.m21() * m2.m10() + this.m22() * m2.m20(),
				this.m20() * m2.m01() + this.m21() * m2.m11() + this.m22() * m2.m21(),
				this.m20() * m2.m02() + this.m21() * m2.m12() + this.m22() * m2.m22()
		);
	}

	default Matrix3F multiplyF(Matrix3 m2) {
		return new Matrix3F(
				this.m00f() * m2.m00f() + this.m01f() * m2.m10f() + this.m02f() * m2.m20f(),
				this.m00f() * m2.m01f() + this.m01f() * m2.m11f() + this.m02f() * m2.m21f(),
				this.m00f() * m2.m02f() + this.m01f() * m2.m12f() + this.m02f() * m2.m22f(),
				this.m10f() * m2.m00f() + this.m11f() * m2.m10f() + this.m12f() * m2.m20f(),
				this.m10f() * m2.m01f() + this.m11f() * m2.m11f() + this.m12f() * m2.m21f(),
				this.m10f() * m2.m02f() + this.m11f() * m2.m12f() + this.m12f() * m2.m22f(),
				this.m20f() * m2.m00f() + this.m21f() * m2.m10f() + this.m22f() * m2.m20f(),
				this.m20f() * m2.m01f() + this.m21f() * m2.m11f() + this.m22f() * m2.m21f(),
				this.m20f() * m2.m02f() + this.m21f() * m2.m12f() + this.m22f() * m2.m22f()
		);
	}

	default Matrix3D multiply(Quat q) {
		return this.multiply(fromQuat(q));
	}

	default Matrix3F multiplyF(Quat q) {
		return this.multiplyF(fromQuatF(q));
	}

	default Matrix3D scale(double scale) {
		return new Matrix3D(
				m00() * scale,
				m01() * scale,
				m02() * scale,
				m10() * scale,
				m11() * scale,
				m12() * scale,
				m20() * scale,
				m21() * scale,
				m22() * scale
		);
	}

	default Matrix3D scaleNS(double sx, double sy, double sz) {
		return new Matrix3D(
				m00() * sx,
				m01() * sy,
				m02() * sz,
				m10() * sx,
				m11() * sy,
				m12() * sz,
				m20() * sx,
				m21() * sy,
				m22() * sz
		);
	}

	default Matrix3F scaleF(float scale) {
		return new Matrix3F(
				m00f() * scale,
				m01f() * scale,
				m02f() * scale,
				m10f() * scale,
				m11f() * scale,
				m12f() * scale,
				m20f() * scale,
				m21f() * scale,
				m22f() * scale
		);
	}

	default Matrix3F scaleNSF(float sx, float sy, float sz) {
		return new Matrix3F(
				m00f() * sx,
				m01f() * sy,
				m02f() * sz,
				m10f() * sx,
				m11f() * sy,
				m12f() * sz,
				m20f() * sx,
				m21f() * sy,
				m22f() * sz
		);
	}

	default Vec3D getYPRAngles() {
		double yaw = 0;
		double pitch = 0;
		double roll = 0;
		// Assuming the angles are in radians.
		if (m10() > 0.999999) { // singularity at north pole
			yaw = Math.atan2(m02(), m22());
			pitch = Math.PI / 2;
			roll = 0;
		} else if (m10() < -0.999999) { // singularity at south pole
			yaw = Math.atan2(m02(), m22());
			pitch = -Math.PI / 2;
			roll = 0;
		} else {
			yaw = Math.atan2(-m20(), m00());
			pitch = Math.atan2(-m12(), m11());
			roll = Math.asin(m10());
		}

		return new Vec3D(yaw, pitch, roll);
	}

	default Vec3D[] asNormals() {
		Vec3D[] norms = new Vec3D[3];
		norms[0] = new Vec3D(m00(), m10(), m20());
		norms[1] = new Vec3D(m01(), m11(), m21());
		norms[2] = new Vec3D(m02(), m12(), m22());
		return norms;
	}

	default Vec3F[] asNormalsF() {
		Vec3F[] norms = new Vec3F[3];
		norms[0] = new Vec3F(m00f(), m10f(), m20f());
		norms[1] = new Vec3F(m01f(), m11f(), m21f());
		norms[2] = new Vec3F(m02f(), m12f(), m22f());
		return norms;
	}

	default Vec3D left() {
		return new Vec3D(m00(), m10(), m20());
	}

	default Vec3D leftNorm() {
		return Vec3.normalized(m00(), m10(), m20());
	}

	default Vec3F leftF() {
		return new Vec3F(m00f(), m10f(), m20f());
	}

	default Vec3F leftNormF() {
		return Vec3.normalizedF(m00f(), m10f(), m20f());
	}

	default Vec3D up() {
		return new Vec3D(m01(), m11(), m21());
	}

	default Vec3D upNorm() {
		return Vec3.normalized(m01(), m11(), m21());
	}

	default Vec3F upF() {
		return new Vec3F(m01f(), m11f(), m21f());
	}

	default Vec3F upNormF() {
		return Vec3.normalizedF(m01f(), m11f(), m21f());
	}

	default Vec3D forward() {
		return new Vec3D(m02(), m12(), m22());
	}

	default Vec3D forwardNorm() {
		return Vec3.normalized(m02(), m12(), m22());
	}

	default Vec3F forwardF() {
		return new Vec3F(m02f(), m12f(), m22f());
	}

	default Vec3F forwardNormF() {
		return Vec3.normalizedF(m02f(), m12f(), m22f());
	}

	static Matrix3D rotationFromAToB(Vec3 a, Vec3 b) {
		Vec3 cross = a.cross(b);
		double len = cross.length();
		if (len < 1E-20) {
			return Matrix3D.SINGLE;
		}

		double lenXLen = a.length() * b.length();
		double dot = a.dot(b);
		cross.div(len);

		double c = dot / lenXLen;
		double s = len / lenXLen;

		double oneMinusC = 1.0 - c;
		double xy = cross.x() * cross.y();
		double yz = cross.y() * cross.z();
		double xz = cross.x() * cross.z();
		double xs = cross.x() * s;
		double ys = cross.y() * s;
		double zs = cross.z() * s;

		return new Matrix3D(
				cross.x() * cross.x() * oneMinusC + c,
				xy * oneMinusC + zs,
				xz * oneMinusC - ys,
				xy * oneMinusC - zs,
				cross.y() * cross.y() * oneMinusC + c,
				yz * oneMinusC + xs,
				xz * oneMinusC + ys,
				yz * oneMinusC - xs,
				cross.z() * cross.z() * oneMinusC + c
		);
	}

	static Matrix3F rotationFromAToBF(Vec3 a, Vec3 b) {
		Vec3 cross = a.crossF(b);
		float len = cross.lengthF();
		if (len < 1E-20) {
			return Matrix3F.SINGLE;
		}

		float lenXLen = a.lengthF() * b.lengthF();
		float dot = a.dotF(b);
		cross = cross.divF(len);

		float c = dot / lenXLen;
		float s = len / lenXLen;

		float oneMinusC = 1.0f - c;
		float xy = cross.xf() * cross.yf();
		float yz = cross.yf() * cross.zf();
		float xz = cross.xf() * cross.zf();
		float xs = cross.xf() * s;
		float ys = cross.yf() * s;
		float zs = cross.zf() * s;

		return new Matrix3F(
				cross.xf() * cross.xf() * oneMinusC + c,
				xy * oneMinusC + zs,
				xz * oneMinusC - ys,
				xy * oneMinusC - zs,
				cross.yf() * cross.yf() * oneMinusC + c,
				yz * oneMinusC + xs,
				xz * oneMinusC + ys,
				yz * oneMinusC - xs,
				cross.zf() * cross.zf() * oneMinusC + c
		);
	}
}
