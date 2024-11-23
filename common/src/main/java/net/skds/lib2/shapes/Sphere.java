package net.skds.lib2.shapes;

import net.skds.lib2.mat.*;

public final class Sphere implements ConvexShape {

	private static final Vec3[] empty = {};

	public final Vec3 center;
	public final double radius;

	private AABB boxCache;
	private Object attachment;

	public Sphere(Vec3 center, double radius) {
		this.center = center;
		this.radius = radius;
	}

	private Sphere(Vec3 center, double radius, Object attachment) {
		this.center = center;
		this.radius = radius;
		this.attachment = attachment;
	}

	@Override
	public Object getAttachment() {
		return attachment;
	}

	@Override
	public Object setAttachment(Object attachment) {
		Object old = this.attachment;
		this.attachment = attachment;
		return old;
	}

	@Override
	public Vec3[] getNormals() {
		return empty;
	}

	@Override
	public Vec3[] getPoints() {
		return empty;
	}

	@Override
	public double surfaceArea() {
		return Math.PI * radius * radius;
	}

	@Override
	public AABB getBoundingBox() {
		AABB bc = boxCache;
		if (bc == null) {
			bc = AABB.fromRadius(center, radius);
			boxCache = bc;
		}
		return bc;
	}

	@Override
	public Sphere rotate(Quat q) {
		return this;
	}

	@Override
	public Sphere rotate(Matrix3 m3) {
		return this;
	}

	@Override
	public Sphere move(Vec3 delta) {
		return new Sphere(center.add(delta), radius, attachment);
	}

	@Override
	public Sphere moveRotScale(Vec3 pos, Matrix3 m3, double scale) {
		return new Sphere(center.add(pos), radius * scale, attachment);
	}

	@Override
	public Sphere moveRotScale(Vec3 pos, Quat q, double scale) {
		return new Sphere(center.add(pos), radius * scale, attachment);
	}


	@Override
	public Sphere scale(double scale) {
		return new Sphere(center, radius * scale, attachment);
	}

	@Override
	public Vec3 getCenter() {
		return center;
	}

	@Override
	public Vec2D getProjection(Vec3 axis) {
		double dot = center.projOn(axis);
		return new Vec2D(dot - radius, dot + radius);
	}

	@Override
	public double getProjectionMin(Vec3 axis) {
		return center.projOn(axis) - radius;
	}

	@Override
	public double getProjectionMax(Vec3 axis) {
		return center.projOn(axis) + radius;
	}

	@Override
	public double getProjectionMin(Direction.Axis axis) {
		return axis.choose(center) - radius;
	}

	@Override
	public double getProjectionMax(Direction.Axis axis) {
		return axis.choose(center) + radius;
	}

	@Override
	public Collision raytrace(Vec3 from, Vec3 to) {

		double dist = from.distanceTo(center);
		double dirL = from.distanceTo(to);
		if (dist <= radius) {
			Vec3 normal = from.sub(center).normalize();
			return new Collision(0, radius - dist, normal, from, null, this, null);
		}
		Vec3 dir = to.sub(from).scale(1 / dirL);

		double proj = center.sub(from).dot(dir);
		if (proj > dirL + radius) {
			return null;
		}
		Vec3 pp = from.add(dir.scale(proj));
		double r2 = radius * radius;
		double k2 = pp.squareDistanceTo(center);
		double delta = Math.sqrt(r2 - k2);
		if (proj > dirL + delta) {
			return null;
		}
		Vec3 point = pp.sub(dir.scale(delta));

		return new Collision(delta, 0, point.sub(center).normalizeScale(radius), point, null, this, null);
	}
}
