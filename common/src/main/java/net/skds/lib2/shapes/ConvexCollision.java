package net.skds.lib2.shapes;

import lombok.experimental.UtilityClass;
import net.skds.lib2.mat.Direction;
import net.skds.lib2.mat.Vec3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@UtilityClass
public class ConvexCollision {

	public static Collision collide(ConvexShape s1, ConvexShape s2, Vec3 velocity21) {

		Vec3[] s1Norm = s1.getNormals();
		Vec3[] s2Norm = s2.getNormals();

		List<Vec3> terminators = new ArrayList<>();
		if (s1Norm.length * s2Norm.length == 0) {
			terminators.add(velocity21.normalize());
		} else {
			Collections.addAll(terminators, s1Norm);
			Collections.addAll(terminators, s2Norm);

			for (int i = 0; i < s1Norm.length; i++) {
				for (int j = 0; j < s2Norm.length; j++) {
					Vec3 cross = s1Norm[i].cross(s2Norm[j]);
					double len = cross.length();
					if (len > 1E-30) {
						terminators.add(cross.scale(1 / len));
					}
				}
			}
		}

		return intersectionMoving(s1, s2, terminators, velocity21);
	}

	private static Collision intersectionMoving(ConvexShape a, ConvexShape b, List<Vec3> terminators, Vec3 velocityBA) {

		double pMin = 0;
		double pMax = 1;

		for (int i = 0; i < terminators.size(); i++) {
			Vec3 terminator = terminators.get(i);
			double v = velocityBA.dot(terminator);
			double aMin = a.getProjectionMin(terminator);
			double aMax = a.getProjectionMax(terminator);
			double bMin = b.getProjectionMin(terminator);
			double bMax = b.getProjectionMax(terminator);

			double tMin = (aMin - bMax) / v;
			double tMax = (aMax - bMin) / v;

			if (v < 0) {
				double d = tMin;
				tMin = tMax;
				tMax = d;
			}

			if (tMin > pMin) {
				pMin = tMin;
			}
			if (tMax < pMax) {
				pMax = tMax;
			}
			if (pMax < pMin) {
				return null;
			}
		}

		final double distance = pMin;

		double termLen = Double.MAX_VALUE;
		Vec3 minTerm = null;
		boolean nInv = false;
		for (int i = 0; i < terminators.size(); i++) {
			Vec3 terminator = terminators.get(i);
			double v = velocityBA.dot(terminator);
			double aMin = a.getProjectionMin(terminator);
			double aMax = a.getProjectionMax(terminator);
			double bMin = b.getProjectionMin(terminator) + v * distance;
			double bMax = b.getProjectionMax(terminator) + v * distance;

			double tMin = Math.max(aMin, bMin);
			double tMax = Math.min(aMax, bMax);

			double d = tMax - tMin;

			if (d < termLen) {
				double dc = (aMax - aMin) - (bMax - bMin);
				nInv = v * dc < 0;
				termLen = d;
				minTerm = terminator;
			}
		}

		assert minTerm != null : "nan or infinite values";
		if (nInv) {
			minTerm = minTerm.inverse();
		}
		if (distance > 0) {
			termLen = 0;
		} else if (termLen < 0) {
			termLen = -termLen;
		}
		return new Collision(distance, termLen, minTerm, Vec3.ZERO, null, a, b);
	}


	public static Collision collideAABB(AABB a, AABB b, Vec3 velocityBA) {

		double pMin = 0;
		double pMax = 1;

		var axs = Direction.Axis.VALUES;

		for (int i = 0; i < axs.length; i++) {
			Direction.Axis axis = axs[i];
			double v = axis.choose(velocityBA);
			double aMin = a.getProjectionMin(axis);
			double aMax = a.getProjectionMax(axis);
			double bMin = b.getProjectionMin(axis);
			double bMax = b.getProjectionMax(axis);

			double tMin = (aMin - bMax) / v;
			double tMax = (aMax - bMin) / v;

			if (v < 0) {
				double d = tMin;
				tMin = tMax;
				tMax = d;
			}

			if (tMin > pMin) {
				pMin = tMin;
			}
			if (tMax < pMax) {
				pMax = tMax;
			}
			if (pMax < pMin) {
				return null;
			}
		}

		final double distance = pMin;

		double termLen = Double.MAX_VALUE;
		Direction minTerm = null;
		boolean nInv = false;

		for (int i = 0; i < axs.length; i++) {
			Direction.Axis axis = axs[i];
			double v = axis.choose(velocityBA);
			double aMin = a.getProjectionMin(axis);
			double aMax = a.getProjectionMax(axis);
			double bMin = b.getProjectionMin(axis) + v * distance;
			double bMax = b.getProjectionMax(axis) + v * distance;

			double tMin = Math.max(aMin, bMin);
			double tMax = Math.min(aMax, bMax);

			double d = tMax - tMin;

			if (d < termLen) {
				double dc = (aMax - aMin) - (bMax - bMin);
				nInv = v * dc < 0;
				termLen = d;
				minTerm = axis.getDirection(!nInv);
			}
		}

		assert minTerm != null : "nan or infinite values";
		if (nInv) {
			minTerm = minTerm.inverse();
		}
		if (distance > 0) {
			termLen = 0;
		} else if (termLen < 0) {
			termLen = -termLen;
		}
		return new Collision(distance, termLen, minTerm, Vec3.ZERO, minTerm, a, b);
	}


}