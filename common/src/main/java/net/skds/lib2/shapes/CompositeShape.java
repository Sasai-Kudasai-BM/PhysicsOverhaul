package net.skds.lib2.shapes;


import net.skds.lib2.mat.Matrix3;
import net.skds.lib2.mat.Quat;
import net.skds.lib2.mat.Vec3;

public non-sealed interface CompositeShape extends Shape {


	ConvexShape[] simplify(AABB bounding);

	@Override
	CompositeShape scale(double scale);

	@Override
	CompositeShape rotate(Matrix3 m3);

	@Override
	CompositeShape move(Vec3 delta);

	@Override
	CompositeShape moveRotScale(Vec3 pos, Matrix3 m3, double scale);

	@Override
	default CompositeShape moveRotScale(Vec3 pos, Quat q, double scale) {
		return moveRotScale(pos, Matrix3.fromQuat(q), scale);
	}

	@Override
	AABB getBoundingBox();


	@Override
	default Collision raytrace(Vec3 from, Vec3 to) {
		ConvexShape[] shapes = simplify(AABB.fromTo(from, to));
		if (shapes.length == 0) return null;
		Collision nearest = null;
		for (int i = 0; i < shapes.length; i++) {
			final ConvexShape subShape = shapes[i];
			Collision c = subShape.raytrace(from, to);
			if (c != null && c.compareTo(nearest) < 0) {
				nearest = c;
			}
		}
		return nearest;
	}

	static Collision collideConvex(CompositeShape composite, ConvexShape convex, Vec3 velocityBA) {
		AABB convexAABB = convex.getBoundingBox();
		ConvexShape[] shapes = composite.simplify(convexAABB);
		if (shapes.length == 0) return null;
		Collision nearest = null;
		for (int i = 0; i < shapes.length; i++) {
			final ConvexShape subShape = shapes[i];
			Collision c = subShape.collide(convex, velocityBA);
			if (c != null && c.compareTo(nearest) < 0) {
				nearest = c;
			}
		}
		return nearest;
	}

	static Collision collideComposite(CompositeShape shapeA, CompositeShape shapeB, Vec3 velocityBA) {
		final AABB bAABB = shapeB.getBoundingBox();
		final ConvexShape[] shapesA = shapeA.simplify(bAABB);
		if (shapesA.length == 0) return null;
		final AABB aAABB = shapeA.getBoundingBox();
		final ConvexShape[] shapesB = shapeB.simplify(aAABB);
		if (shapesB.length == 0) return null;

		Collision nearest = null;
		for (int i = 0; i < shapesA.length; i++) {
			final ConvexShape subShapeA = shapesA[i];
			final AABB subShapeAAABB = subShapeA.getBoundingBox();
			for (int j = 0; j < shapesB.length; j++) {
				final ConvexShape subShapeB = shapesB[i];
				if (subShapeAAABB.intersects(subShapeB.getBoundingBox())) {
					final Collision c = subShapeA.collide(subShapeB, velocityBA);
					if (c != null && c.compareTo(nearest) < 0) {
						nearest = c;
					}
				}
			}
		}
		return nearest;
	}

	@Override
	default Collision collide(Shape shapeB, Vec3 velocityBA) {
		if (shapeB instanceof ConvexShape convex) {
			return collideConvex(this, convex, velocityBA);
		} else if (shapeB instanceof CompositeShape composite) {
			return collideComposite(this, composite, velocityBA.inverse());
		}
		throw new UnsupportedOperationException("Unable to collide \"%s\" with \"%s\"".formatted(this, shapeB));

	}

	default AABB createBounding() {
		return new AABBBuilder(simplify(null)).build();
	}
}
