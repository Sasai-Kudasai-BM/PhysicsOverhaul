package net.skds.lib2.mat;

import net.skds.lib2.utils.ArrayUtils;

import java.util.function.Predicate;

@SuppressWarnings("unused")
public enum Direction implements Vec3 {
	DOWN(0, 1, -1, "down", AxisDirection.NEGATIVE, Axis.Y, new Vec3I(0, -1, 0)),
	UP(1, 0, -1, "up", AxisDirection.POSITIVE, Axis.Y, new Vec3I(0, 1, 0)),
	BACKWARD(2, 3, 2, "north", AxisDirection.NEGATIVE, Axis.Z, new Vec3I(0, 0, -1)),
	FORWARD(3, 2, 0, "south", AxisDirection.POSITIVE, Axis.Z, new Vec3I(0, 0, 1)),
	RIGHT(4, 5, 1, "west", AxisDirection.NEGATIVE, Axis.X, new Vec3I(-1, 0, 0)),
	LEFT(5, 4, 3, "east", AxisDirection.POSITIVE, Axis.X, new Vec3I(1, 0, 0));

	public static final Direction[] VALUES = values();
	public static final Direction[] HORIZONTAL = {BACKWARD, FORWARD, RIGHT, LEFT};

	private final int id;
	private final int idOpposite;
	private final int idHorizontal;
	private final String name;
	private final Axis axis;
	private final AxisDirection direction;
	private final Vec3I vector;

	public static final Direction EAST = LEFT;
	public static final Direction WEST = RIGHT;
	public static final Direction SOUTH = FORWARD;
	public static final Direction NORTH = BACKWARD;

	public static final Direction XP = EAST;
	public static final Direction XN = WEST;
	public static final Direction YP = UP;
	public static final Direction YN = DOWN;
	public static final Direction ZP = SOUTH;
	public static final Direction ZN = NORTH;


	Direction(int id, int idOpposite, int idHorizontal, String name, AxisDirection direction, Axis axis,
			  Vec3I vector) {
		this.id = id;
		this.idHorizontal = idHorizontal;
		this.idOpposite = idOpposite;
		this.name = name;
		this.axis = axis;
		this.direction = direction;
		this.vector = vector;
	}

	public int getId() {
		return this.id;
	}

	public int getHorizontal() {
		return this.idHorizontal;
	}

	public AxisDirection getDirection() {
		return this.direction;
	}

	public Direction getOpposite() {
		return VALUES[this.idOpposite];
	}

	@Override
	public Direction inverse() {
		return getOpposite();
	}

	public Direction rotateClockwise(Axis axis) {
		return switch (axis) {
			case X -> {
				if (this == RIGHT || this == LEFT) {
					yield this;
				}
				yield this.rotateXClockwise();
			}
			case Y -> {
				if (this == UP || this == DOWN) {
					yield this;
				}
				yield this.rotateYClockwise();
			}
			case Z -> this == BACKWARD || this == FORWARD ? this : this.rotateZClockwise();
		};
	}

	public Direction rotateCounterclockwise(Axis axis) {
		return switch (axis) {
			case X -> {
				if (this == RIGHT || this == LEFT) {
					yield this;
				}
				yield this.rotateXCounterclockwise();
			}
			case Y -> {
				if (this == UP || this == DOWN) {
					yield this;
				}
				yield this.rotateYCounterclockwise();
			}
			case Z -> this == BACKWARD || this == FORWARD ? this : this.rotateZCounterclockwise();
		};
	}

	public Direction rotateYClockwise() {
		return switch (this) {
			case BACKWARD -> LEFT;
			case LEFT -> FORWARD;
			case FORWARD -> RIGHT;
			case RIGHT -> BACKWARD;
			default -> throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
		};
	}

	private Direction rotateXClockwise() {
		return switch (this) {
			case UP -> BACKWARD;
			case BACKWARD -> DOWN;
			case DOWN -> FORWARD;
			case FORWARD -> UP;
			default -> throw new IllegalStateException("Unable to get X-rotated facing of " + this);
		};
	}

	private Direction rotateXCounterclockwise() {
		return switch (this) {
			case UP -> FORWARD;
			case FORWARD -> DOWN;
			case DOWN -> BACKWARD;
			case BACKWARD -> UP;
			default -> throw new IllegalStateException("Unable to get X-rotated facing of " + this);
		};
	}

	private Direction rotateZClockwise() {
		return switch (this) {
			case UP -> LEFT;
			case LEFT -> DOWN;
			case DOWN -> RIGHT;
			case RIGHT -> UP;
			default -> throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
		};
	}

	private Direction rotateZCounterclockwise() {
		return switch (this) {
			case UP -> RIGHT;
			case RIGHT -> DOWN;
			case DOWN -> LEFT;
			case LEFT -> UP;
			default -> throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
		};
	}

	public Direction rotateYCounterclockwise() {
		return switch (this) {
			case BACKWARD -> RIGHT;
			case LEFT -> BACKWARD;
			case FORWARD -> LEFT;
			case RIGHT -> FORWARD;
			default -> throw new IllegalStateException("Unable to get CCW facing of " + this);
		};
	}

	@Override
	public double x() {
		return this.vector.x();
	}

	@Override
	public double y() {
		return this.vector.y();
	}

	@Override
	public double z() {
		return this.vector.z();
	}

	@Override
	public float xf() {
		return this.vector.xf();
	}

	@Override
	public float yf() {
		return this.vector.yf();
	}

	@Override
	public float zf() {
		return this.vector.zf();
	}

	@Override
	public int floorX() {
		return this.vector.floorX();
	}

	@Override
	public int floorY() {
		return this.vector.floorY();
	}

	@Override
	public int floorZ() {
		return this.vector.floorZ();
	}

	@Override
	public int ceilX() {
		return this.vector.ceilX();
	}

	@Override
	public int ceilY() {
		return this.vector.ceilY();
	}

	@Override
	public int ceilZ() {
		return this.vector.ceilZ();
	}

	public String getName() {
		return this.name;
	}

	public Axis getAxis() {
		return this.axis;
	}

	public static Direction byId(int id) {
		return VALUES[ArrayUtils.loop(id, VALUES.length)];
	}

	public static Direction fromHorizontal(int value) {
		return HORIZONTAL[Math.abs(value % HORIZONTAL.length)];
	}

	public static Direction fromRotation(double rotation) {
		return Direction.fromHorizontal(FastMath.floor(rotation / 90.0 + 0.5) & 3);
	}

	public static Direction from(Axis axis, AxisDirection direction) {
		return switch (axis) {
			case X -> {
				if (direction == AxisDirection.POSITIVE) {
					yield LEFT;
				}
				yield RIGHT;
			}
			case Y -> {
				if (direction == AxisDirection.POSITIVE) {
					yield UP;
				}
				yield DOWN;
			}
			case Z -> direction == AxisDirection.POSITIVE ? FORWARD : BACKWARD;
		};
	}

	public float asRotation() {
		return (this.idHorizontal & 3) * 90;
	}

	public static Direction getFacing(double x, double y, double z) {
		return Direction.getFacing((float) x, (float) y, (float) z);
	}

	public static Direction getFacing(Vec3 from, Vec3 to) {
		return getFacing((float) (to.x() - from.x()), (float) (to.y() - from.y()), (float) (to.z() - from.z()));
	}

	public static Direction getFacing(float x, float y, float z) {
		Direction direction = BACKWARD;
		float f = Float.MIN_VALUE;
		for (Direction direction2 : VALUES) {
			float g = x * direction2.vector.xf() + y * direction2.vector.yf() + z * direction2.vector.zf();
			if (!(g > f))
				continue;
			f = g;
			direction = direction2;
		}
		return direction;
	}

	public static Direction getHorizontalFacing(Vec3 from, Vec3 to) {
		return getHorizontalFacing((float) (to.x() - from.x()), (float) (to.z() - from.z()));
	}

	public static Direction getHorizontalFacingFrom(Vec3 from, Vec3 to, Iterable<Direction> iterable) {
		return getHorizontalFacingFrom((float) (to.x() - from.x()), (float) (to.z() - from.z()), iterable);
	}

	public static Direction getHorizontalFacing(float x, float z) {
		Direction direction = BACKWARD;
		float f = Float.NEGATIVE_INFINITY;
		for (Direction direction2 : HORIZONTAL) {
			float g = x * direction2.vector.xf() + z * direction2.vector.zf();
			if (!(g > f))
				continue;
			f = g;
			direction = direction2;
		}
		return direction;
	}

	public static Direction getHorizontalFacingFrom(float x, float z, Iterable<Direction> iterable) {
		Direction direction = null;
		float f = Float.NEGATIVE_INFINITY;
		for (Direction direction2 : iterable) {
			float g = x * direction2.vector.xf() + z * direction2.vector.zf();
			if (!(g > f))
				continue;
			f = g;
			direction = direction2;
		}
		return direction;
	}

	public static Direction getFromOffset(int x, int y, int z) {

		if (x > 0) {
			return XP;
		}
		if (x < 0) {
			return XN;
		}
		if (y > 0) {
			return YP;
		}
		if (y < 0) {
			return YN;
		}
		if (z > 0) {
			return ZP;
		}
		if (z < 0) {
			return ZN;
		}
		return null;
	}

	public String toString() {
		return this.name;
	}

	public static Direction get(AxisDirection direction, Axis axis) {
		for (Direction direction2 : VALUES) {
			if (direction2.getDirection() != direction || direction2.getAxis() != axis)
				continue;
			return direction2;
		}
		throw new IllegalArgumentException("No such direction: " + direction + " " + axis);
	}

	public Vec3I getOffset() {
		return this.vector;
	}

	@Deprecated // rewrite
	public boolean pointsTo(float yaw) {
		float g = -FastMath.sinDegr(yaw);
		float h = FastMath.cosDegr(yaw);
		return (float) this.vector.x() * g + (float) this.vector.z() * h > 0.0f;
	}


	public enum Axis implements Predicate<Direction> {
		X("xf") {
			@Override
			public int choose(int x, int y, int z) {
				return x;
			}

			@Override
			public double choose(double x, double y, double z) {
				return x;
			}

			@Override
			public Direction getDirection(double value) {
				return value < 0 ? RIGHT : LEFT;
			}

			@Override
			public Direction getPositiveDirection() {
				return LEFT;
			}

			@Override
			public Direction getDirection(boolean positive) {
				return positive ? Direction.XP : Direction.XN;
			}
		},
		Y("yf") {
			@Override
			public int choose(int x, int y, int z) {
				return y;
			}

			@Override
			public double choose(double x, double y, double z) {
				return y;
			}

			@Override
			public Direction getDirection(double value) {
				return value < 0 ? DOWN : UP;
			}

			@Override
			public Direction getPositiveDirection() {
				return UP;
			}

			@Override
			public Direction getDirection(boolean positive) {
				return positive ? Direction.YP : Direction.YN;
			}
		},
		Z("zf") {
			@Override
			public int choose(int x, int y, int z) {
				return z;
			}

			@Override
			public double choose(double x, double y, double z) {
				return z;
			}

			@Override
			public Direction getDirection(double value) {
				return value < 0 ? BACKWARD : FORWARD;
			}

			@Override
			public Direction getPositiveDirection() {
				return FORWARD;
			}

			@Override
			public Direction getDirection(boolean positive) {
				return positive ? Direction.ZP : Direction.ZN;
			}
		};

		public static final Axis[] VALUES;
		private final String name;

		Axis(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public boolean isVertical() {
			return this == Y;
		}

		public boolean isHorizontal() {
			return this == X || this == Z;
		}

		public String toString() {
			return this.name;
		}

		@Override
		public boolean test(Direction direction) {
			return direction != null && direction.getAxis() == this;
		}

		public abstract int choose(int var1, int var2, int var3);

		public abstract double choose(double var1, double var3, double var5);

		public abstract Direction getDirection(double value);

		public abstract Direction getDirection(boolean positive);

		public abstract Direction getPositiveDirection();

		public double choose(Vec3 vec) {
			return choose(vec.x(), vec.y(), vec.z());
		}

		public Direction getDir(Vec3 vec) {
			return getDirection(choose(vec));
		}

		static {
			VALUES = Axis.values();
		}
	}

	public enum AxisDirection {
		POSITIVE(1, "Towards positive"),
		NEGATIVE(-1, "Towards negative");

		private final int offset;
		private final String description;

		AxisDirection(int offset, String description) {
			this.offset = offset;
			this.description = description;
		}

		public int offset() {
			return this.offset;
		}

		public String getDescription() {
			return this.description;
		}

		public String toString() {
			return this.description;
		}

		public AxisDirection getOpposite() {
			return this == POSITIVE ? NEGATIVE : POSITIVE;
		}
	}

}
