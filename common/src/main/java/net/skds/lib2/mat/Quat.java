package net.skds.lib2.mat;

@SuppressWarnings("unused")
public sealed interface Quat extends Vec4 permits QuatD, QuatF {

	Quat ONE = QuatD.ONE;

	static boolean equals(Quat q1, Quat q2) {
		if (q1 == q2) {
			return true;
		} else if ((q1 == null) != (q2 == null)) {
			return false;
		} else {
			if (Double.compare(q1.x(), q2.x()) != 0) {
				return false;
			} else if (Double.compare(q1.y(), q2.y()) != 0) {
				return false;
			} else if (Double.compare(q1.z(), q2.z()) != 0) {
				return false;
			} else {
				return Double.compare(q1.w(), q2.w()) == 0;
			}
		}
	}

	static QuatD fromAxisDegrees(Vec3 axis, double angle) {
		if (Math.abs(angle) < 1E-30) {
			return QuatD.ONE;
		}
		double f = FastMath.sinDegr(angle / 2.0);
		return new QuatD(
				axis.x() * f,
				axis.y() * f,
				axis.z() * f,
				FastMath.cosDegr(angle / 2.0)
		);
	}

	static QuatD fromAxisRad(Vec3 axis, double angle) {
		if (Math.abs(angle) < 1E-30) {
			return QuatD.ONE;
		}
		double f = FastMath.sinRad(angle / 2.0);
		return new QuatD(
				axis.x() * f,
				axis.y() * f,
				axis.z() * f,
				FastMath.cosRad(angle / 2.0)
		);
	}

	static QuatD fromMatrix(Matrix3 m) {
		double x;
		double y;
		double z;
		double w;
		double tr = m.m00() + m.m11() + m.m22();
		if (tr > 0) {
			double s = Math.sqrt(tr + 1.0) * 2; // S=4*qw
			w = 0.25 * s;
			x = (m.m21() - m.m12()) / s;
			y = (m.m02() - m.m20()) / s;
			z = (m.m10() - m.m01()) / s;
		} else if ((m.m00() > m.m11()) & (m.m00() > m.m22())) {
			double s = Math.sqrt(1.0 + m.m00() - m.m11() - m.m22()) * 2; // S=4*qx
			w = (m.m21() - m.m12()) / s;
			x = 0.25 * s;
			y = (m.m01() + m.m10()) / s;
			z = (m.m02() + m.m20()) / s;
		} else if (m.m11() > m.m22()) {
			double s = Math.sqrt(1.0 + m.m11() - m.m00() - m.m22()) * 2; // S=4*qy
			w = (m.m02() - m.m20()) / s;
			x = (m.m01() + m.m10()) / s;
			y = 0.25 * s;
			z = (m.m12() + m.m21()) / s;
		} else {
			double s = Math.sqrt(1.0 + m.m22() - m.m00() - m.m11()) * 2; // S=4*qz
			w = (m.m10() - m.m01()) / s;
			x = (m.m02() + m.m20()) / s;
			y = (m.m12() + m.m21()) / s;
			z = 0.25 * s;
		}
		return new QuatD(x, y, z, w);
	}

	static QuatD fromForward(Vec3 forward) {
		return fromMatrix(Matrix3.fromForward(forward));
	}

	static QuatF fromAxisDegreesF(Vec3 axis, float angle) {
		if (Math.abs(angle) < 1E-30) {
			return QuatF.ONE;
		}
		float f = FastMath.sinDegr(angle / 2.0f);
		return new QuatF(
				axis.xf() * f,
				axis.yf() * f,
				axis.zf() * f,
				FastMath.cosDegr(angle / 2.0f)
		);
	}

	static QuatF fromAxisRadF(Vec3 axis, float angle) {
		if (Math.abs(angle) < 1E-30) {
			return QuatF.ONE;
		}
		float f = FastMath.sinRad(angle / 2.0f);
		return new QuatF(
				axis.xf() * f,
				axis.yf() * f,
				axis.zf() * f,
				FastMath.cosRad(angle / 2.0f)
		);
	}

	static QuatF fromMatrixF(Matrix3 m) {
		float x;
		float y;
		float z;
		float w;
		float tr = m.m00f() + m.m11f() + m.m22f();
		if (tr > 0) {
			float s = (float) (Math.sqrt(tr + 1.0f)) * 2; // S=4*qw
			w = 0.25f * s;
			x = (m.m21f() - m.m12f()) / s;
			y = (m.m02f() - m.m20f()) / s;
			z = (m.m10f() - m.m01f()) / s;
		} else if ((m.m00() > m.m11()) & (m.m00() > m.m22())) {
			float s = (float) (Math.sqrt(1.0f + m.m00f() - m.m11f() - m.m22f())) * 2; // S=4*qx
			w = (m.m21f() - m.m12f()) / s;
			x = 0.25f * s;
			y = (m.m01f() + m.m10f()) / s;
			z = (m.m02f() + m.m20f()) / s;
		} else if (m.m11() > m.m22()) {
			float s = (float) (Math.sqrt(1.0f + m.m11f() - m.m00f() - m.m22f())) * 2; // S=4*qy
			w = (m.m02f() - m.m20f()) / s;
			x = (m.m01f() + m.m10f()) / s;
			y = 0.25f * s;
			z = (m.m12f() + m.m21f()) / s;
		} else {
			float s = (float) (Math.sqrt(1.0f + m.m22() - m.m00() - m.m11())) * 2; // S=4*qz
			w = (m.m10f() - m.m01f()) / s;
			x = (m.m02f() + m.m20f()) / s;
			y = (m.m12f() + m.m21f()) / s;
			z = 0.25f * s;
		}
		return new QuatF(x, y, z, w);
	}

	default Quat rotate(Vec3 spin) {
		double angle = spin.length();
		if (angle < 1E-7) {
			return this;
		}

		double axisX = spin.x() / angle;
		double axisY = spin.y() / angle;
		double axisZ = spin.z() / angle;

		double f0 = FastMath.sinRad(angle / 2.0);
		double f = axisX * f0;
		double f1 = axisY * f0;
		double f2 = axisZ * f0;
		double f3 = FastMath.cosRad(angle / 2.0);

		double x = f3 * x() + f * w() + f1 * z() - f2 * y();
		double y = f3 * y() - f * z() + f1 * w() + f2 * x();
		double z = f3 * z() + f * y() - f1 * x() + f2 * w();
		double w = f3 * w() - f * x() - f1 * y() - f2 * z();
		return new QuatD(x, y, z, w);
	}

	default Quat rotateF(Vec3 spin) {
		float angle = spin.lengthF();
		if (angle < 1E-7) {
			return this;
		}

		float axisX = spin.xf() / angle;
		float axisY = spin.yf() / angle;
		float axisZ = spin.zf() / angle;

		float f0 = FastMath.sinRad(angle / 2.0f);
		float f = axisX * f0;
		float f1 = axisY * f0;
		float f2 = axisZ * f0;
		float f3 = FastMath.cosRad(angle / 2.0f);

		float x = f3 * xf() + f * wf() + f1 * zf() - f2 * yf();
		float y = f3 * yf() - f * zf() + f1 * wf() + f2 * xf();
		float z = f3 * zf() + f * yf() - f1 * xf() + f2 * wf();
		float w = f3 * wf() - f * xf() - f1 * yf() - f2 * zf();
		return new QuatF(x, y, z, w);
	}

	default QuatD multiply(Quat q) {
		return new QuatD(
				w() * q.x() + x() * q.w() + y() * q.z() - z() * q.y(),
				w() * q.y() - x() * q.z() + y() * q.w() + z() * q.x(),
				w() * q.z() + x() * q.y() - y() * q.x() + z() * q.w(),
				w() * q.w() - x() * q.x() - y() * q.y() - z() * q.z()
		);
	}

	default QuatD multiply(double qx, double qy, double qz, double qw) {
		return new QuatD(
				w() * qx + x() * qw + y() * qz - z() * qy,
				w() * qy - x() * qz + y() * qw + z() * qx,
				w() * qz + x() * qy - y() * qx + z() * qw,
				w() * qw - x() * qx - y() * qy - z() * qz
		);
	}

	default QuatF multiplyF(Quat q) {
		return new QuatF(
				wf() * q.xf() + xf() * q.wf() + yf() * q.zf() - zf() * q.yf(),
				wf() * q.yf() - xf() * q.zf() + yf() * q.wf() + zf() * q.xf(),
				wf() * q.zf() + xf() * q.yf() - yf() * q.xf() + zf() * q.wf(),
				wf() * q.wf() - xf() * q.xf() - yf() * q.yf() - zf() * q.zf()
		);
	}

	default QuatF multiplyF(float qx, float qy, float qz, float qw) {
		return new QuatF(
				wf() * qx + xf() * qw + yf() * qz - zf() * qy,
				wf() * qy - xf() * qz + yf() * qw + zf() * qx,
				wf() * qz + xf() * qy - yf() * qx + zf() * qw,
				wf() * qw - xf() * qx - yf() * qy - zf() * qz
		);
	}

	default QuatD rotateAxisDegrees(Vec3 axis, double angle) {

		double f0 = FastMath.sinDegr(angle / 2.0);
		double x = axis.x() * f0;
		double y = axis.y() * f0;
		double z = axis.z() * f0;
		double w = FastMath.cosDegr(angle / 2.0);

		double f = this.x();
		double f1 = this.y();
		double f2 = this.z();
		double f3 = this.w();

		return new QuatD(
				f3 * x + f * w + f1 * z - f2 * y,
				f3 * y - f * z + f1 * w + f2 * x,
				f3 * z + f * y - f1 * x + f2 * w,
				f3 * w - f * x - f1 * y - f2 * z
		);
	}

	default QuatF rotateAxisDegreesF(Vec3 axis, float angle) {

		float f0 = FastMath.sinDegr(angle / 2.0f);
		float x = axis.xf() * f0;
		float y = axis.yf() * f0;
		float z = axis.zf() * f0;
		float w = FastMath.cosDegr(angle / 2.0f);

		float f = this.xf();
		float f1 = this.yf();
		float f2 = this.zf();
		float f3 = this.wf();

		return new QuatF(
				f3 * x + f * w + f1 * z - f2 * y,
				f3 * y - f * z + f1 * w + f2 * x,
				f3 * z + f * y - f1 * x + f2 * w,
				f3 * w - f * x - f1 * y - f2 * z
		);
	}

	default QuatD conjugate() {
		return new QuatD(
				-x(),
				-y(),
				-z(),
				+w()
		);
	}

	default QuatF conjugateF() {
		return new QuatF(
				-xf(),
				-yf(),
				-zf(),
				+wf()
		);
	}

	default QuatD normalize() {
		double x = x();
		double y = y();
		double z = z();
		double w = w();
		double f = x * x + y * y + z * z + w * w;
		double g = FastMath.invSqrt(f);
		return new QuatD(x * g, y * g, z * g, w * g);
	}

	default QuatF normalizeF() {
		float x = xf();
		float y = yf();
		float z = zf();
		float w = wf();
		float f = x * x + y * y + z * z + w * w;
		float g = FastMath.invSqrt(f);
		return new QuatF(x * g, y * g, z * g, w * g);
	}

	default Vec3D forward() {
		double f4 = 2.0 * x() * x();
		double f5 = 2.0 * y() * y();
		double f8 = y() * z();
		double f9 = z() * x();
		double f10 = x() * w();
		double f11 = y() * w();
		double vx = 2.0 * (f9 + f11);
		double vy = 2.0 * (f8 - f10);
		double vz = 1.0 - f4 - f5;

		return new Vec3D(vx, vy, vz);
	}

	default Vec3F forwardF() {
		float f4 = 2.0f * xf() * xf();
		float f5 = 2.0f * yf() * yf();
		float f8 = yf() * zf();
		float f9 = zf() * xf();
		float f10 = xf() * wf();
		float f11 = yf() * wf();
		float vx = 2.0f * (f9 + f11);
		float vy = 2.0f * (f8 - f10);
		float vz = 1.0f - f4 - f5;

		return new Vec3F(vx, vy, vz);
	}

	static Quat sLerp(Quat qa, Quat qb, double t) {
		// Calculate angle between them.
		double cosHalfTheta = qa.w() * qb.w() + qa.x() * qb.x() + qa.y() * qb.y() + qa.z() * qb.z();
		// if qa=qb or qa=-qb then theta = 0 and we can return qa
		if (Math.abs(cosHalfTheta) >= 1.0) {
			return qa;
		}
		double qmw, qmx, qmy, qmz;
		// Calculate temporary values.
		double halfTheta = Math.acos(cosHalfTheta);
		double sinHalfTheta = Math.sqrt(1.0 - cosHalfTheta * cosHalfTheta);
		// if theta = 180 degrees then result is not fully defined
		// we could rotate around any axis normal to qa or qb
		if (Math.abs(sinHalfTheta) < 1E-15) { // fabs is floating point absolute
			qmw = (qa.w() * 0.5 + qb.w() * 0.5);
			qmx = (qa.x() * 0.5 + qb.x() * 0.5);
			qmy = (qa.y() * 0.5 + qb.y() * 0.5);
			qmz = (qa.z() * 0.5 + qb.z() * 0.5);
			return new QuatD(qmw, qmx, qmy, qmz);
		}
		double ratioA = FastMath.sinRad((1 - t) * halfTheta) / sinHalfTheta;
		double ratioB = FastMath.sinRad(t * halfTheta) / sinHalfTheta;
		//calculate Quaternion.
		qmw = (qa.w() * ratioA + qb.w() * ratioB);
		qmx = (qa.x() * ratioA + qb.x() * ratioB);
		qmy = (qa.y() * ratioA + qb.y() * ratioB);
		qmz = (qa.z() * ratioA + qb.z() * ratioB);
		return new QuatD(qmw, qmx, qmy, qmz);
	}

	static Quat sLerpF(Quat qa, Quat qb, float t) {
		// Calculate angle between them.
		float cosHalfTheta = qa.wf() * qb.wf() + qa.xf() * qb.xf() + qa.yf() * qb.yf() + qa.zf() * qb.zf();
		// if qa=qb or qa=-qb then theta = 0 and we can return qa
		if (Math.abs(cosHalfTheta) >= 1.0) {
			return qa;
		}
		float qmw, qmx, qmy, qmz;
		// Calculate temporary values.
		float halfTheta = (float) Math.acos(cosHalfTheta);
		float sinHalfTheta = (float) Math.sqrt(1.0 - cosHalfTheta * cosHalfTheta);
		// if theta = 180 degrees then result is not fully defined
		// we could rotate around any axis normal to qa or qb
		if (Math.abs(sinHalfTheta) < 1E-15) { // fabs is floating point absolute
			qmw = (qa.wf() * 0.5f + qb.wf() * 0.5f);
			qmx = (qa.xf() * 0.5f + qb.xf() * 0.5f);
			qmy = (qa.yf() * 0.5f + qb.yf() * 0.5f);
			qmz = (qa.zf() * 0.5f + qb.zf() * 0.5f);
			return new QuatF(qmw, qmx, qmy, qmz);
		}
		float ratioA = FastMath.sinRad((1 - t) * halfTheta) / sinHalfTheta;
		float ratioB = FastMath.sinRad(t * halfTheta) / sinHalfTheta;
		//calculate Quaternion.
		qmw = (qa.wf() * ratioA + qb.wf() * ratioB);
		qmx = (qa.xf() * ratioA + qb.xf() * ratioB);
		qmy = (qa.yf() * ratioA + qb.yf() * ratioB);
		qmz = (qa.zf() * ratioA + qb.zf() * ratioB);
		return new QuatF(qmw, qmx, qmy, qmz);
	}
}
