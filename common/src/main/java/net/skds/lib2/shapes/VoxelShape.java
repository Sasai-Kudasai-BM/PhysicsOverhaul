package net.skds.lib2.shapes;

import net.skds.lib2.mat.Matrix3;
import net.skds.lib2.mat.Vec3;

import java.util.Arrays;

public sealed class VoxelShape implements CompositeShape {

	private static final ConvexShape[] empty = {};

	public static final VoxelShape EMPTY = new EmptyVoxelShape();

	private final AABB[] boxes;
	private final AABB bounding;
	private final Vec3 center;
	private Object attachment;

	private VoxelShape(AABB[] boxes, Vec3 center) {
		this.boxes = boxes;
		this.center = center;
		if (boxes.length == 0) {
			this.bounding = AABB.EMPTY;
		} else {
			AABBBuilder b = new AABBBuilder(boxes[0].getBoundingBox());
			for (int i = 1; i < boxes.length; i++) {
				b = b.expand(boxes[i]);
			}
			this.bounding = b.build();
		}
	}

	private VoxelShape(AABB[] boxes, Vec3 center, Object attachment) {
		this.boxes = boxes;
		this.center = center;
		this.attachment = attachment;
		if (boxes.length == 0) {
			this.bounding = AABB.EMPTY;
		} else {
			AABBBuilder b = new AABBBuilder(boxes[0].getBoundingBox());
			for (int i = 1; i < boxes.length; i++) {
				b = b.expand(boxes[i]);
			}
			this.bounding = b.build();
		}
	}

	public static VoxelShape of(AABB[] boxes, Vec3 center) {
		if (boxes == null || boxes.length == 0) {
			return EMPTY;
		}
		return new VoxelShape(boxes, center);
	}

	public static VoxelShape of(AABB[] boxes) {
		if (boxes == null || boxes.length == 0) {
			return EMPTY;
		}
		return new VoxelShape(boxes, Vec3.ZERO);
	}

	public static VoxelShape cuboid(double x1, double y1, double z1, double x2, double y2, double z2) {
		AABB box = new AABB(x1, y1, z1, x2, y2, z2);
		return cuboid(box);
	}

	public static VoxelShape cuboid(AABB box) {
		return new VoxelShape(new AABB[]{box}, Vec3.ZERO);
	}

	public boolean isEmpty() {
		return boxes.length == 0;
	}

	public boolean intersects(Vec3 shapePos, AABB box) {
		if (isEmpty()) {
			return false;
		}
		for (int i = 0; i < boxes.length; i++) {
			if (boxes[i].intersects(box, shapePos)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ConvexShape[] simplify(AABB bounding) {
		if (bounding == null) return boxes;
		if (isEmpty() || !this.bounding.intersects(bounding)) return empty;
		if (bounding.contains(this.bounding)) return boxes;
		ConvexShape[] shapes = new ConvexShape[boxes.length];
		int n = 0;
		for (int i = 0; i < shapes.length; i++) {
			AABB box = boxes[i];
			if (box.intersects(bounding)) {
				shapes[n++] = box;
			}
		}
		if (n < shapes.length) {
			if (n == 0) {
				return empty;
			}
			shapes = Arrays.copyOf(shapes, n);
		}
		return shapes;
	}


	@Override
	public VoxelShape move(Vec3 delta) {
		final AABB[] offBoxes = new AABB[boxes.length];
		for (int i = 0; i < offBoxes.length; i++) {
			offBoxes[i] = boxes[i].move(delta);
		}
		return new VoxelShape(offBoxes, center.add(delta), attachment);
	}

	@Override
	public CompositeSuperShape moveRotScale(Vec3 pos, Matrix3 m3, double scale) {
		final ConvexShape[] convexShapes = new ConvexShape[boxes.length];
		for (int i = 0; i < convexShapes.length; i++) {
			AABB box = boxes[i];
			Vec3 od = box.getCenter().sub(center);
			Vec3 nd = od.add(pos).transform(m3).scale(scale);
			convexShapes[i] = box.moveRotScale(nd.sub(od), m3, scale);
		}
		return new CompositeSuperShape(convexShapes, center.add(pos), attachment);
	}

	@Override
	public CompositeSuperShape rotate(Matrix3 m3) {
		final ConvexShape[] convexShapes = new ConvexShape[boxes.length];
		for (int i = 0; i < convexShapes.length; i++) {
			AABB box = boxes[i];
			Vec3 od = box.getCenter().sub(center);
			Vec3 nd = od.transform(m3);
			convexShapes[i] = box.moveRotScale(nd.sub(od), m3, 1);
		}
		return new CompositeSuperShape(convexShapes, center, attachment);
	}

	@Override
	public VoxelShape scale(double scale) {
		final AABB[] offBoxes = new AABB[boxes.length];
		for (int i = 0; i < offBoxes.length; i++) {
			AABB box = boxes[i];
			Vec3 od = box.getCenter().sub(center);
			Vec3 nd = od.scale(scale);
			offBoxes[i] = box.move(nd.sub(od));
		}
		return new VoxelShape(offBoxes, center, attachment);
	}

	@Override
	public Vec3 getCenter() {
		return center;
	}

	@Override
	public AABB getBoundingBox() {
		return bounding;
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

	private static final class EmptyVoxelShape extends VoxelShape {
		public EmptyVoxelShape() {
			super(new AABB[0], Vec3.ZERO);
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public ConvexShape[] simplify(AABB bounding) {
			return empty;
		}
	}
}
