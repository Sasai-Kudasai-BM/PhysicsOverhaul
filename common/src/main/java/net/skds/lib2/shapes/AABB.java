package net.skds.lib2.shapes;


import net.skds.lib2.mat.Direction;
import net.skds.lib2.mat.Matrix3;
import net.skds.lib2.mat.Vec3;
import net.skds.lib2.mat.Vec3D;

import java.util.Collection;

@SuppressWarnings("unused")
public final class AABB implements ConvexShape {

	public static final AABB EMPTY = new AABB(0, 0, 0, 0, 0, 0);
	public static final AABB ONE = new AABB(0, 0, 0, 1, 1, 1);

	private static final Vec3[] normals = {Vec3.XP, Vec3.YP, Vec3.ZP};

	public final double minX, minY, minZ, maxX, maxY, maxZ;

	private Object attachment;

	private Vec3[] pointsCache;

	public AABB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}

	private AABB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Object attachment) {
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
		this.attachment = attachment;
	}


	public AABB(Vec3 pos1, Vec3 pos2) {
		this(pos1.x(), pos1.y(), pos1.z(), pos2.x(), pos2.y(), pos2.z());
	}

	public AABB(float[] array, int offset) {
		this(array[offset], array[offset + 1], array[offset + 2],
				array[offset + 3], array[offset + 4], array[offset + 5]
		);
	}

	public AABB(double[] array, int offset) {
		this(array[offset], array[offset + 1], array[offset + 2],
				array[offset + 3], array[offset + 4], array[offset + 5]
		);
	}

	public static AABB fromTo(Vec3 from, Vec3 to) {
		return new AABB(from.x(), from.y(), from.z(), to.x(), to.y(), to.z());
	}

	public static AABB fromPos(Vec3 pos) {
		return ONE.move(pos);
	}


	public static AABB fromPos(int x, int y, int z) {
		return ONE.move(x, y, z);
	}

	public static AABB fromPosSize(Vec3 pos, Vec3 size) {
		double dx = size.x() / 2;
		double dy = size.y() / 2;
		double dz = size.z() / 2;
		return new AABB(pos.x() - dx, pos.y() - dy, pos.z() - dz, pos.x() + dx, pos.y() + dy, pos.z() + dz);
	}

	public static AABB fromPosSize(Vec3 pos, double size) {
		size /= 2;
		return new AABB(pos.x() - size, pos.y() - size, pos.z() - size, pos.x() + size, pos.y() + size, pos.z() + size);
	}

	public static AABB fromNormal(Vec3 n) {
		return new AABB(n.x() < 0 ? n.x() : 0, n.y() < 0 ? n.y() : 0, n.z() < 0 ? n.z() : 0,
				n.x() > 0 ? n.x() : 0, n.y() > 0 ? n.y() : 0, n.z() > 0 ? n.z() : 0
		);
	}

	public static AABB fromNormal(double x, double y, double z) {
		return new AABB(x < 0 ? x : 0, y < 0 ? y : 0, z < 0 ? z : 0, x > 0 ? x : 0, y > 0 ? y : 0, z > 0 ? z : 0);
	}


	public static AABB fromCenter(Vec3 center, double dx, double dy, double dz) {
		return new AABB(center.x() - dx / 2.0, center.y() - dy / 2.0, center.z() - dz / 2.0, center.x() + dx / 2.0,
				center.y() + dy / 2.0, center.z() + dz / 2.0);
	}

	public static AABB fromRadius(Vec3 center, double radius) {
		return new AABB(center.x() - radius, center.y() - radius, center.z() - radius, center.x() + radius,
				center.y() + radius, center.z() + radius);
	}

	public static AABB fromSize(Vec3 size) {
		double dx = size.x() / 2;
		double dy = size.y() / 2;
		double dz = size.z() / 2;
		return new AABB(-dx, -dy, -dz, dx, dy, dz);
	}

	public static AABB fromSize(double size) {
		size /= 2;
		return new AABB(-size, -size, -size, size, size, size);
	}

	public static AABB fromSize(double dx, double dy, double dz) {
		dx /= 2;
		dy /= 2;
		dz /= 2;
		return new AABB(-dx, -dy, -dz, dx, dy, dz);
	}

	@Override
	public Vec3[] getNormals() {
		return normals;
	}

	@Override
	public AABB getBoundingBox() {
		return this;
	}

	@Override
	public AABB scale(double scale) {
		Vec3 center = getCenter();
		AABB box = fromCenter(getCenter(), sizeX() * scale, sizeY() * scale, sizeZ() * scale);
		box.attachment = attachment;
		return box;
	}

	@Override
	public OBB rotate(Matrix3 m3) {
		OBB obb = new OBB(getCenter(), m3.scaleNS(sizeX(), sizeY(), sizeZ()));
		obb.setAttachment(attachment);
		return obb;
	}

	@Override
	public OBB moveRotScale(Vec3 pos, Matrix3 m3, double scale) {
		OBB obb = new OBB(getCenter().add(pos), m3.scaleNS(sizeX() * scale, sizeY() * scale, sizeZ() * scale));
		obb.setAttachment(attachment);
		return obb;
	}

	@Override
	public Vec3[] getPoints() {
		Vec3[] pc = pointsCache;
		if (pc == null) {
			pc = new Vec3[]{
					new Vec3D(minX, minY, minZ),
					new Vec3D(minX, minY, maxZ),
					new Vec3D(minX, maxY, minZ),
					new Vec3D(minX, maxY, maxZ),
					new Vec3D(maxX, minY, minZ),
					new Vec3D(maxX, minY, maxZ),
					new Vec3D(maxX, maxY, minZ),
					new Vec3D(maxX, maxY, maxZ)
			};
			pointsCache = pc;
		}
		return pc;
	}

	@Override
	public double surfaceArea() {
		double x = maxX - minX;
		double y = maxY - minY;
		double z = maxZ - minZ;

		return 2 * (x * y + x * z + y * z);
	}

	public double sizeX() {
		return maxX - minX;
	}

	public double sizeY() {
		return maxY - minY;
	}

	public double sizeZ() {
		return maxZ - minZ;
	}

	public Vec3 dimensions() {
		return new Vec3D(maxX - minX, maxY - minY, maxZ - minZ);
	}

	public AABB withMinX(double minX) {
		return new AABB(minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ, attachment);
	}

	public AABB withMinY(double minY) {
		return new AABB(this.minX, minY, this.minZ, this.maxX, this.maxY, this.maxZ, attachment);
	}

	public AABB withMinZ(double minZ) {
		return new AABB(this.minX, this.minY, minZ, this.maxX, this.maxY, this.maxZ, attachment);
	}

	public AABB withMaxX(double maxX) {
		return new AABB(this.minX, this.minY, this.minZ, maxX, this.maxY, this.maxZ, attachment);
	}

	public AABB withMaxY(double maxY) {
		return new AABB(this.minX, this.minY, this.minZ, this.maxX, maxY, this.maxZ, attachment);
	}

	public AABB withMaxZ(double maxZ) {
		return new AABB(this.minX, this.minY, this.minZ, this.maxX, this.maxY, maxZ, attachment);
	}

	public double getMaxWidth() {
		return Math.max(this.maxX - this.minX, this.maxZ - this.minZ);
	}

	public double getMaxDimension() {
		return Math.max(getMaxWidth(), maxY - minY);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof AABB box)) {
			return false;
		}
		if (box.minX != this.minX) {
			return false;
		}
		if (box.minY != this.minY) {
			return false;
		}
		if (box.minZ != this.minZ) {
			return false;
		}
		if (box.maxX != this.maxX) {
			return false;
		}
		if (box.maxY != this.maxY) {
			return false;
		}
		return box.maxZ == this.maxZ;
	}

	public int hashCode() {
		long l = Double.doubleToLongBits(this.minX);
		int i = (int) (l ^ l >>> 32);
		l = Double.doubleToLongBits(this.minY);
		i = 31 * i + (int) (l ^ l >>> 32);
		l = Double.doubleToLongBits(this.minZ);
		i = 31 * i + (int) (l ^ l >>> 32);
		l = Double.doubleToLongBits(this.maxX);
		i = 31 * i + (int) (l ^ l >>> 32);
		l = Double.doubleToLongBits(this.maxY);
		i = 31 * i + (int) (l ^ l >>> 32);
		l = Double.doubleToLongBits(this.maxZ);
		i = 31 * i + (int) (l ^ l >>> 32);
		return i;
	}

	public AABB shrink(double x, double y, double z) {
		double d = this.minX;
		double e = this.minY;
		double f = this.minZ;
		double g = this.maxX;
		double h = this.maxY;
		double i = this.maxZ;
		if (x < 0.0) {
			d -= x;
		} else if (x > 0.0) {
			g -= x;
		}
		if (y < 0.0) {
			e -= y;
		} else if (y > 0.0) {
			h -= y;
		}
		if (z < 0.0) {
			f -= z;
		} else if (z > 0.0) {
			i -= z;
		}
		return new AABB(d, e, f, g, h, i, attachment);
	}

	public AABB stretch(Vec3 scale) {
		return this.stretch(scale.x(), scale.y(), scale.z());
	}

	public AABB stretch(double x, double y, double z) {
		double d = this.minX;
		double e = this.minY;
		double f = this.minZ;
		double g = this.maxX;
		double h = this.maxY;
		double i = this.maxZ;
		if (x < 0.0) {
			d += x;
		} else if (x > 0.0) {
			g += x;
		}
		if (y < 0.0) {
			e += y;
		} else if (y > 0.0) {
			h += y;
		}
		if (z < 0.0) {
			f += z;
		} else if (z > 0.0) {
			i += z;
		}
		return new AABB(d, e, f, g, h, i, attachment);
	}

	public AABB expand(double x, double y, double z) {
		double d = this.minX - x;
		double e = this.minY - y;
		double f = this.minZ - z;
		double g = this.maxX + x;
		double h = this.maxY + y;
		double i = this.maxZ + z;
		return new AABB(d, e, f, g, h, i, attachment);
	}

	public AABB expand(double value) {
		return this.expand(value, value, value);
	}

	public AABB intersection(AABB box) {
		double d = Math.max(this.minX, box.minX);
		double e = Math.max(this.minY, box.minY);
		double f = Math.max(this.minZ, box.minZ);
		double g = Math.min(this.maxX, box.maxX);
		double h = Math.min(this.maxY, box.maxY);
		double i = Math.min(this.maxZ, box.maxZ);
		return new AABB(d, e, f, g, h, i, attachment);
	}

	public AABB union(AABB box) {
		double d = Math.min(this.minX, box.minX);
		double e = Math.min(this.minY, box.minY);
		double f = Math.min(this.minZ, box.minZ);
		double g = Math.max(this.maxX, box.maxX);
		double h = Math.max(this.maxY, box.maxY);
		double i = Math.max(this.maxZ, box.maxZ);
		return new AABB(d, e, f, g, h, i, attachment);
	}

	public AABB move(double x, double y, double z) {
		return new AABB(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z, attachment);
	}

	@Override
	public AABB move(Vec3 vec) {
		return this.move(vec.x(), vec.y(), vec.z());
	}


	public boolean intersects(AABB box) {
		return this.intersects(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
	}

	public boolean contains(AABB box) {
		return this.contains(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
	}

	public boolean intersects(AABB box, Vec3 offset) {
		return this.minX + offset.x() < box.maxX && this.maxX + offset.x() > box.minX
				&& this.minY + offset.y() < box.maxY
				&& this.maxY + offset.y() > box.minY && this.minZ + offset.z() < box.maxZ
				&& this.maxZ + offset.z() > box.minZ;
	}


	public boolean intersects(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		return this.minX < maxX && this.maxX > minX && this.minY < maxY && this.maxY > minY && this.minZ < maxZ
				&& this.maxZ > minZ;
	}

	public boolean contains(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		return this.minX <= minX && this.maxX >= maxX && this.minY <= minY && this.maxY >= maxY && this.minZ <= minZ
				&& this.maxZ >= maxZ;
	}

	public boolean intersects(Vec3 pos1, Vec3 pos2) {
		return this.intersects(Math.min(pos1.x(), pos2.x()), Math.min(pos1.y(), pos2.y()), Math.min(pos1.z(), pos2.z()),
				Math.max(pos1.x(), pos2.x()), Math.max(pos1.y(), pos2.y()), Math.max(pos1.z(), pos2.z()));
	}

	public boolean contains(Vec3 pos) {
		return this.contains(pos.x(), pos.y(), pos.z());
	}

	public boolean contains(double x, double y, double z) {
		return x >= this.minX && x < this.maxX && y >= this.minY && y < this.maxY && z >= this.minZ && z < this.maxZ;
	}

	public double getAverageSideLength() {
		double d = this.sizeX();
		double e = this.sizeY();
		double f = this.sizeZ();
		return (d + e + f) / 3.0;
	}

	public AABB contract(double x, double y, double z) {
		return this.expand(-x, -y, -z);
	}

	public AABB contract(double value) {
		return this.expand(-value);
	}

	@Override
	public double getProjectionMax(Direction.Axis axis) {
		return switch (axis) {
			case X -> maxX;
			case Y -> maxY;
			case Z -> maxZ;
		};
	}


	public double getProjection(Direction.Axis axis) {
		return switch (axis) {
			case X -> maxX - minX;
			case Y -> maxY - minY;
			case Z -> maxZ - minZ;
		};
	}

	@Override
	public double getProjectionMin(Direction.Axis axis) {
		return switch (axis) {
			case X -> minX;
			case Y -> minY;
			case Z -> minZ;
		};
	}

	@Override
	public Collision raytrace(Vec3 from, Vec3 to) {
		Vec3 dir = to.sub(from);

		double tMax = Double.POSITIVE_INFINITY;
		double tMin = Double.NEGATIVE_INFINITY;
		Direction normal = null;
		boolean inverse = false;

		for (int i = 0; i < Direction.Axis.VALUES.length; i++) {
			Direction n = Direction.Axis.VALUES[i].getPositiveDirection();
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
		return new Collision(tMin, 0, normal, from.add(dir.normalizeScale(tMin)), normal, this, null);
	}

	@Override
	public Collision collide(Shape shapeB, Vec3 velocityBA) {
		if (shapeB instanceof AABB sb) {
			return ConvexCollision.collideAABB(this, sb, velocityBA);
		}
		return ConvexShape.super.collide(shapeB, velocityBA);
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
	public String toString() {
		return "AABB[" + this.minX + ", " + this.minY + ", " + this.minZ + "] -> [" + this.maxX + ", " + this.maxY
				+ ", " + this.maxZ + "]";
	}

	public boolean isValid() {
		return Double.isNaN(this.minX) || Double.isNaN(this.minY) || Double.isNaN(this.minZ) || Double.isNaN(this.maxX)
				|| Double.isNaN(this.maxY) || Double.isNaN(this.maxZ);
	}

	@Override
	public Vec3 getCenter() {
		return new Vec3D((this.minX + this.maxX) / 2, (this.minY + this.maxY) / 2, (this.minZ + this.maxZ) / 2);
	}

	public Vec3 getDimensions() {
		return new Vec3D(maxX - minX, maxY - minY, maxZ - minZ);
	}

	public double getMin(Direction.Axis axis) {
		return switch (axis) {
			case X -> minX;
			case Y -> minY;
			case Z -> minZ;
		};
	}

	public double getMax(Direction.Axis axis) {
		return switch (axis) {
			case X -> maxX;
			case Y -> maxY;
			case Z -> maxZ;
		};
	}

	public static AABB unionOf(Collection<AABB> boxes) {
		if (boxes.isEmpty()) {
			return EMPTY;
		}
		var iter = boxes.iterator();
		AABB b = iter.next();
		while (iter.hasNext()) {
			b = b.union(iter.next());
		}
		return b;
	}
}
