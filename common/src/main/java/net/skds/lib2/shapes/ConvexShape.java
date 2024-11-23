package net.skds.lib2.shapes;

import net.skds.lib2.mat.*;

public non-sealed interface ConvexShape extends Shape {

	default Vec2D getProjection(Vec3 axis) {
		final Vec3[] points = getPoints();
		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;
		for (int i = 0; i < points.length; i++) {
			double dot = points[i].dot(axis);
			if (dot < min) {
				min = dot;
			}
			if (dot > max) {
				max = dot;
			}
		}
		return new Vec2D(min, max);
	}

	default double getProjectionMin(Vec3 axis) {
		final Vec3[] points = getPoints();
		double min = Double.MAX_VALUE;
		for (int i = 0; i < points.length; i++) {
			double dot = points[i].dot(axis);
			if (dot < min) {
				min = dot;
			}
		}
		return min;
	}

	default double getProjectionMax(Vec3 axis) {
		final Vec3[] points = getPoints();
		double max = -Double.MAX_VALUE;
		for (int i = 0; i < points.length; i++) {
			double dot = points[i].dot(axis);
			if (dot > max) {
				max = dot;
			}
		}
		return max;
	}

	default double getProjectionMin(Direction.Axis axis) {
		final Vec3[] points = getPoints();
		double min = Double.MAX_VALUE;
		for (int i = 0; i < points.length; i++) {
			double dot = axis.choose(points[i]);
			if (dot < min) {
				min = dot;
			}
		}
		return min;
	}

	default double getProjectionMax(Direction.Axis axis) {
		final Vec3[] points = getPoints();
		double max = -Double.MAX_VALUE;
		for (int i = 0; i < points.length; i++) {
			double dot = axis.choose(points[i]);
			if (dot > max) {
				max = dot;
			}
		}
		return max;
	}

	@Override
	ConvexShape move(Vec3 delta);

	@Override
	ConvexShape rotate(Matrix3 m3);

	@Override
	default ConvexShape rotate(Quat q) {
		return rotate(Matrix3.fromQuat(q));
	}

	@Override
	ConvexShape moveRotScale(Vec3 pos, Matrix3 m3, double scale);

	@Override
	default ConvexShape moveRotScale(Vec3 pos, Quat q, double scale) {
		return moveRotScale(pos, Matrix3.fromQuat(q), scale);
	}

	@Override
	ConvexShape scale(double scale);

	@Override
	default Collision collide(Shape shapeB, Vec3 velocityBA) {
		if (shapeB instanceof ConvexShape convex) {
			return ConvexCollision.collide(this, convex, velocityBA);
		} else if (shapeB instanceof CompositeShape composite) {
			return CompositeShape.collideConvex(composite, this, velocityBA.inverse());
		}
		throw new UnsupportedOperationException("Unable to collide \"%s\" with \"%s\"".formatted(this, shapeB));
	}

	@Override
	default Collision raytrace(Vec3 from, Vec3 to) {
		Vec3 dir = to.sub(from);

		double tMax = Double.POSITIVE_INFINITY;
		double tMin = Double.NEGATIVE_INFINITY;
		Vec3 normal = null;
		boolean inverse = false;

		Vec3[] norms = getNormals();
		for (int i = 0; i < norms.length; i++) {
			Vec3 n = norms[i];
			double nomLen = -n.dot(from);
			double denomLen = n.dot(dir);

			double a = (nomLen + getProjectionMax(n)) / denomLen;
			double b = (nomLen + getProjectionMin(n)) / denomLen;
			double min;
			double max;
			if (a < b) {
				min = a;
				max = b;
			} else {
				min = b;
				max = a;
			}

			if (min > tMin) {
				tMin = min;
				normal = n;
				inverse = a > b;
			}
			if (max < tMax) {
				tMax = max;
			}

			if (tMax < tMin) {
				return null;
			}

		}
		if (inverse) {
			normal = normal.inverse();
		}
		return new Collision(tMin, 0, normal, from.add(dir.normalizeScale(tMin)), null, this, null);
	}

	Vec3[] getNormals();

	Vec3[] getPoints();

	@Override
	default boolean isConvex() {
		return true;
	}

	double surfaceArea();

}
