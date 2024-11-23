package net.skds.lib2.mat;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
@SuppressWarnings({"ManualMinMaxCalculation", "unused"})
public class FastMath {

	public static final Random RANDOM = new Random();

	public static final float PI = (float) Math.PI;
	public static final float TWO_PI = PI * 2;
	public static final float HALF_PI = PI / 2;

	public static final float RAD_2_DGR = (180 / PI);
	public static final float DGR_2_RAD = (PI / 180);

	private static final float[] sinTable = new float[1024 * 4];

	public static boolean roll(double chance) {
		if (chance <= 0) {
			return false;
		} else if (chance >= 1) {
			return true;
		} else {
			return RANDOM.nextDouble() <= chance;
		}
	}

	public static double avg(double a, double b) {
		return (a + b) / 2;
	}

	public static double avg(double a, double b, double c) {
		return (a + b + c) / 3;
	}

	public static double avg(double a, double b, double c, double d) {
		return (a + b + c + d) / 4;
	}

	public static double avg(double... arr) {
		double r = 0;
		for (int i = 0; i < arr.length; i++) {
			r += arr[i];
		}
		return r / arr.length;
	}

	public static float avg(float a, float b) {
		return (a + b) / 2;
	}

	public static float avg(float a, float b, float c) {
		return (a + b + c) / 3;
	}

	public static float avg(float a, float b, float c, float d) {
		return (a + b + c + d) / 4;
	}

	public static float avg(float... arr) {
		float r = 0;
		for (int i = 0; i < arr.length; i++) {
			r += arr[i];
		}
		return r / arr.length;
	}

	public static double gaussian() {
		double v1, v2, s;
		do {
			v1 = 2 * RANDOM.nextDouble() - 1; // between -1 and 1
			v2 = 2 * RANDOM.nextDouble() - 1; // between -1 and 1
			s = v1 * v1 + v2 * v2;
		} while (s >= 1 || s == 0);
		double multiplier = Math.sqrt(-2 * Math.log(s) / s);
		return v1 * multiplier;
	}

	public static double modInt(double a, int b) {
		int div = ((int) a) / b;
		//log.info(div);
		return a - (div * b);
	}

	public static float modInt(float a, int b) {
		int div = ((int) a) / b;
		//log.info(div);
		return a - (div * b);
	}

	public static double sinOld(double x) {
		x = aprSinDegr(x);
		double a;
		if (x > 180) {
			x = 360 - x;
			a = -1;
		} else {
			a = 1;
		}
		a *= 4 * x * (180 - x);
		double b = 40500 - (x * (180 - x));

		return a / b;
	}

	public static float sinOld(float x) {
		x = aprSinDegr(x);
		float a;
		if (x > 180) {
			x = 360 - x;
			a = -1;
		} else {
			a = 1;
		}
		a *= 4 * x * (180 - x);
		float b = 40500 - (x * (180 - x));

		return a / b;
	}

	public static double cosOld(double x) {
		return sinOld(x + 90);
	}

	public static float cosOld(float x) {
		return sinOld(x + 90);
	}

	private static double aprSinDegr(double x) {
		x = modInt(x, 360);
		if (x < 0) {
			x = 360 + x;
		}
		return x;
	}

	private static float aprSinDegr(float x) {
		x = modInt(x, 360);
		if (x < 0) {
			x += 360;
		}
		return x;
	}

	public static double wrapDegrees(double degrees) {
		double d = modInt(degrees, 360);
		if (d >= 180.0) {
			d -= 360.0;
		} else if (d < -180.0) {
			d += 360.0;
		}
		return d;
	}

	public static float wrapDegrees(float degrees) {
		float d = modInt(degrees, 360);
		if (d >= 180.0f) {
			d -= 360.0f;
		} else if (d < -180.0f) {
			d += 360.0f;
		}
		return d;
	}

	public static double clamp(double value, double min, double max) {
		if (value < min) {
			return min;
		}
		if (value > max) {
			return max;
		}
		return value;
	}

	public static float clamp(float value, float min, float max) {
		if (value < min) {
			return min;
		}
		if (value > max) {
			return max;
		}
		return value;
	}

	public static int clamp(int value, int min, int max) {
		if (value < min) {
			return min;
		}
		if (value > max) {
			return max;
		}
		return value;
	}

	public static double clampAngle(double angle, double limit) {
		double a = wrapDegrees(angle);
		a = clamp(a, -limit, limit);
		return a;
	}

	public static float clampAngle(float angle, float limit) {
		float a = wrapDegrees(angle);
		a = clamp(a, -limit, limit);
		return a;
	}

	public static double clampAngle(double start, double end, double speed) {
		double a = wrapDegrees(end - start);
		a = clamp(a, -speed, speed);
		return a;
	}

	public static float clampAngle(float start, float end, float speed) {
		float a = wrapDegrees(end - start);
		a = clamp(a, -speed, speed);
		return a;
	}

	public static double angleDiffDeg(double a1, double a2) {
		double diff = (a1 - a2) % 360;
		return diff > 180 ? diff - 360 : diff;
	}

	public static float angleDiffDeg(float a1, float a2) {
		float diff = (a1 - a2) % 360;
		if (diff > 180) {
			diff -= 360;
		} else if (diff < -180) {
			diff += 360;
		}
		return diff;
	}

	public static double lerp(double t, double min, double max) {
		return (max - min) * t + min;
	}

	public static float lerp(float t, float min, float max) {
		return (max - min) * t + min;
	}

	public static float lerpAngle(float t, float min, float max) {
		return wrapDegrees(wrapDegrees(max - min) * t + min);
	}

	public static float sinDegr(float a) {
		float pos = modInt(a * sinTable.length / 360f, sinTable.length);
		if (pos < 0) {
			pos += sinTable.length;
		}
		int b1 = ((int) pos) % sinTable.length;
		int b2 = (b1 + 1) % sinTable.length;
		float part = pos - b1;
		return sinTable[b1] * (1 - part) + sinTable[b2] * part;
	}

	public static float sinRad(float a) {
		return sinDegr(a * RAD_2_DGR);
	}

	public static float cosDegr(final float x) {
		return sinDegr(x + 90);
	}

	public static float cosRad(final float x) {
		return sinRad(x + HALF_PI);
	}

	public static double sinDegr(double a) {
		double pos = modInt(a * sinTable.length / 360f, sinTable.length);
		if (pos < 0) {
			pos += sinTable.length;
		}
		int b1 = ((int) pos) % sinTable.length;
		int b2 = (b1 + 1) % sinTable.length;
		double part = pos - b1;
		return sinTable[b1] * (1 - part) + sinTable[b2] * part;
	}

	public static double sinRad(double a) {
		return sinDegr(a * RAD_2_DGR);
	}

	public static double cosDegr(final double x) {
		return sinDegr(x + 90);
	}

	public static double cosRad(final double x) {
		return sinRad(x + HALF_PI);
	}


	public static int floor(double value) {
		int i = (int) value;
		return i <= value ? i : i - 1;
	}

	public static int ceil(double value) {
		int i = (int) value;
		return i >= value ? i : i + 1;
	}

	public static int floor(float value) {
		int i = (int) value;
		return i <= value ? i : i - 1;
	}

	public static int ceil(float value) {
		int i = (int) value;
		return i >= value ? i : i + 1;
	}

	public static float invSqrt(float x) {
		float half = 0.5f * x;
		int i = Float.floatToRawIntBits(x);
		i = 0x5F3759DF - (i >> 1);
		x = Float.intBitsToFloat(i);
		x *= (1.5f - half * x * x);
		x *= (1.5f - half * x * x);
		return x;
	}

	public static double invSqrt(double x) {
		double half = 0.5d * x;
		long i = Double.doubleToRawLongBits(x);
		i = 0x5FE6EC85E7DE30DAL - (i >> 1);
		x = Double.longBitsToDouble(i);
		x *= (1.5d - half * x * x);
		x *= (1.5d - half * x * x);
		x *= (1.5d - half * x * x);
		return x;
	}

	public static int smallestEncompassingPowerOfTwo(int value) {
		int i = value - 1;
		i |= i >> 1;
		i |= i >> 2;
		i |= i >> 4;
		i |= i >> 8;
		i |= i >> 16;
		return i + 1;
	}

	public static boolean approxEqualSq(double a, double b, double eps) {
		double d = a - b;
		return d * d < eps;
	}

	public static boolean approxEqual(double a, double b, double eps) {
		return Math.abs(a - b) < eps;
	}


	static {
		for (int i = 0; i < sinTable.length; i++) {
			sinTable[i] = (float) Math.sin(2 * Math.PI * i / sinTable.length);
		}
	}
}
