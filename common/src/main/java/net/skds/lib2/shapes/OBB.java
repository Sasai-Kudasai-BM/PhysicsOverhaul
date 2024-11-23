package net.skds.lib2.shapes;

import net.skds.lib2.mat.Matrix3;
import net.skds.lib2.mat.Quat;
import net.skds.lib2.mat.Vec3;

public class OBB implements ConvexShape {

	public final Matrix3 normals;
	public final Vec3 center;
	//public final Vec3 dimensions;

	private Vec3[] vertexCache;
	private AABB boundingCache;

	private Object attachment;

	public OBB(Vec3 center, Vec3 dimensions, Quat q) {
		this.center = center;
		this.normals = Matrix3.fromQuatNS(q, dimensions.x(), dimensions.y(), dimensions.z());
	}

	public OBB(Vec3 center, Matrix3 normals) {
		this.center = center;
		this.normals = normals;
	}

	private OBB(Vec3 center, Matrix3 normals, Object attachment) {
		this.center = center;
		this.normals = normals;
		this.attachment = attachment;
	}


	@Override
	public OBB rotate(Matrix3 m3) {
		return new OBB(center, m3.multiply(normals), attachment);
	}

	@Override
	public OBB move(Vec3 delta) {
		return new OBB(center.add(delta), normals, attachment);
	}

	@Override
	public OBB scale(double scale) {
		return new OBB(center, normals.scale(scale), attachment);
	}

	@Override
	public OBB moveRotScale(Vec3 pos, Matrix3 m3, double scale) {
		return new OBB(center.add(pos), m3.multiply(normals).scale(scale), attachment);
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
	public Vec3 getCenter() {
		return center;
	}

	@Override
	public AABB getBoundingBox() {
		AABB bb = boundingCache;
		if (bb == null) {
			bb = new AABBBuilder(center).expand(getPoints()).build();
			boundingCache = bb;
		}
		return bb;
	}


	@Override
	public Vec3[] getPoints() {
		Vec3[] vc = vertexCache;
		Vec3 l = normals.left();
		Vec3 u = normals.up();
		Vec3 f = normals.forward();
		if (vc == null) {
			vc = new Vec3[]{
					center.addScale(l.sub(u), .5),
					center.addScale(f.sub(u), .5),
					center.addScale(l.invSub(u), .5),
					center.addScale(f.invSub(u), .5),
					center.addScale(l.add(u), .5),
					center.addScale(f.add(u), .5),
					center.addScale(l.invSub(u), .5),
					center.addScale(f.invSub(u), .5)
			};
			vertexCache = vc;
		}
		return vc;
	}

	@Override
	public double surfaceArea() {
		double l = normals.left().length();
		double u = normals.up().length();
		double f = normals.forward().length();
		return 2 * (l * u + l * f + u * f);
	}


	@Override
	public Vec3[] getNormals() {
		return new Vec3[]{
				normals.leftNorm(),
				normals.upNorm(),
				normals.forwardNorm()
		};
	}
}