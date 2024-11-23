package net.skds.lib2.shapes;


import net.skds.lib2.mat.Direction;
import net.skds.lib2.mat.Vec3;

public record Collision(double distance,
						double depth,
						Vec3 normal,
						Vec3 point,
						Direction direction,
						Shape shapeA,
						Shape shapeB
) implements Comparable<Collision> {

	public Collision(double distance, double depth, Vec3 normal, Shape shapeA, Shape shapeB) {
		this(distance, depth, normal, null, null, shapeA, shapeB);
	}

	public Collision(double distance, double depth, Vec3 normal) {
		this(distance, depth, normal, null, null, null, null);
	}

	@Override
	public int compareTo(Collision o) {
		if (o == null) return -1;
		int d = Double.compare(depth, o.depth);
		if (d == 0) {
			return Double.compare(distance, o.distance);
		}
		return d;
	}
}
