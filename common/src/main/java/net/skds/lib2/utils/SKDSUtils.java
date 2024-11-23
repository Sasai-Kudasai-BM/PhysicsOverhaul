package net.skds.lib2.utils;

import com.sun.management.HotSpotDiagnosticMXBean;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.management.MBeanServer;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.math.BigInteger;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@SuppressWarnings("unused")
public class SKDSUtils {

	public static final HexFormat HEX_FORMAT_LC = HexFormat.of();
	public static final OSType OS_TYPE = getOS();
	public static final String OS_ARC = getOSAndArc();

	private static final ThreadLocal<MessageDigest> tlSHA1 = ThreadLocal.withInitial(() -> getMDSafe("SHA1"));
	private static final ThreadLocal<MessageDigest> tlSHA256 = ThreadLocal.withInitial(() -> getMDSafe("SHA256"));
	private static final ThreadLocal<MessageDigest> tlMD5 = ThreadLocal.withInitial(() -> getMDSafe("MD5"));

	private static final ThreadLocal<Zipper> zippers = ThreadLocal.withInitial(Zipper::new);

	public static final Runnable EMPTY_RUNNABLE = () -> {
	};

	public static final Random R = new Random();

	private static MessageDigest getMDSafe(String algorithm) {
		try {
			return MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static String hashFile(File f) {
		try (InputStream is = new BufferedInputStream(new FileInputStream(f))) {
			byte[] buffer = new byte[1024];
			MessageDigest md = getSHA1();
			if (!f.exists()) {
				return HEX_FORMAT_LC.formatHex(md.digest());
			}
			int c;
			while ((c = is.read(buffer)) >= 0) {
				md.update(buffer, 0, c);
			}
			return HEX_FORMAT_LC.formatHex(md.digest());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] hashFileArray(File f) {
		try (InputStream is = new BufferedInputStream(new FileInputStream(f))) {
			byte[] buffer = new byte[1024];
			MessageDigest md = getSHA1();
			if (!f.exists()) {
				return md.digest();
			}
			int c;
			while ((c = is.read(buffer)) >= 0) {
				md.update(buffer, 0, c);
			}
			return md.digest();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String hashInput(InputStream is) {
		try {
			byte[] buffer = new byte[1024];
			MessageDigest md = getSHA1();
			int c;
			while ((c = is.read(buffer)) >= 0) {
				md.update(buffer, 0, c);
			}
			return HEX_FORMAT_LC.formatHex(md.digest());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String hashArray(byte[] data) {
		MessageDigest md = getSHA1();
		return HEX_FORMAT_LC.formatHex(md.digest(data));
	}

	public static StartTime startTimeMeasure() {
		return new StartTime(System.nanoTime());
	}

	public static void openInBrowser(String link) {
		try {
			Desktop.getDesktop().browse(new URI(link));
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static int arrToInt(byte[] arr) {
		return (((arr[0] & 255) << 8 | arr[1] & 255) << 8 | arr[2] & 255) << 8 | arr[3] & 255;
	}

	public static long arrToLong(byte[] arr) {
		BigInteger bi = new BigInteger(arr);
		return ((((((((long) (arr[0] & 255) << 8 | arr[1] & 255) << 8 | arr[2] & 255) << 8 | arr[3] & 255) << 8 | arr[4] & 255) << 8) | arr[5] & 255) << 8 | arr[6] & 255) << 8 | arr[7] & 255;
	}

	public static int[] UUID2IntArray(UUID uuid) {
		long l = uuid.getLeastSignificantBits();
		long m = uuid.getMostSignificantBits();
		return new int[]{(int) (m >> 32), (int) m, (int) (l >> 32), (int) l};
	}

	public static UUID IntArray2UUID(int[] uuid) {
		assert (uuid.length == 4) : "uuid int array length must be 4";
		long m = (long) uuid[0] << 32;
		m |= uuid[1];
		long l = (long) uuid[2] << 32;
		l |= uuid[3];
		return new UUID(m, l);
	}

	public static String memoryCompact(long bytes) {
		if (bytes < 1L << 10) {
			return bytes + " Bytes";
		} else if (bytes < 1L << 20) {
			return "%.2f kBytes".formatted(bytes / 1024d);
		} else if (bytes < 1L << 30) {
			return "%.2f MBytes".formatted(bytes / (1024d * 1024));
		} else if (bytes < 1L << 40) {
			return "%.2f GBytes".formatted(bytes / (1024d * 1024 * 1024));
		} else {
			return "%.2f TBytes".formatted(bytes / (1024d * 1024 * 1024 * 1024));
		}
	}

	public static String memoryCompact(long bytes, long of) {
		if (of < 1L << 10) {
			return "%s / %s Bytes".formatted(bytes, of);
		} else if (of < 1L << 20) {
			return "%.2f / %.2f kBytes".formatted(bytes / 1024d, of / 1024d);
		} else if (of < 1L << 30) {
			return "%.2f / %.2f MBytes".formatted(bytes / (1024d * 1024), of / (1024d * 1024));
		} else if (of < 1L << 40) {
			return "%.2f / %.2f GBytes".formatted(bytes / (1024d * 1024 * 1024), of / (1024d * 1024 * 1024));
		} else {
			return "%.2f / %.2f TBytes".formatted(bytes / (1024d * 1024 * 1024 * 1024), of / (1024d * 1024 * 1024 * 1024));
		}
	}

	public static String stringHash(MessageDigest md, String value) {
		return HEX_FORMAT_LC.formatHex(md.digest(value.getBytes(StandardCharsets.UTF_8)));
	}

	public static <T> void consumeIfNotNull(T object, Consumer<T> action) {
		if (object != null) {
			action.accept(object);
		}
	}

	public static Zipper getZipper(int bufferSize) {
		Zipper zipper = zippers.get();
		if (zipper.buffer == null || zipper.buffer.capacity() < bufferSize) {
			zipper.buffer = ByteBuffer.allocate(bufferSize);
		}
		return zipper;
	}

	//public static UUID uuidFromName(String name) {
	//	byte[] data = name.getBytes(StandardCharsets.UTF_8);
	//	long msb = 0;
	//	long lsb = 0;
	//	assert data.length <= 16 : "name must be no longer 16 symbols";
	//	int to = Math.min(8, data.length);
	//	for (int i = 0; i < to; i++)
	//		msb = (msb << 8) | (data[i] & 0xff);
	//	if (data.length >= 8) {
	//		to = Math.min(16, data.length);
	//		for (int i = 8; i < to; i++)
	//			lsb = (lsb << 8) | (data[i] & 0xff);
	//	}
	//	return new UUID(msb, lsb);
	//}

	public static UUID uuidFromName(String name) {
		return UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
	}

	public static class Zipper {
		public final Inflater inflater = new Inflater();
		public final Deflater deflater = new Deflater();
		@Getter
		private ByteBuffer buffer;

		private Zipper() {
		}

		public void pack(ByteBuffer content, int contentStart, int length) {
			buffer.clear().put(content.array(), contentStart, length).flip();
			deflater.setInput(buffer);
			deflater.finish();
			deflater.deflate(content.position(contentStart));
			deflater.reset();
		}

		public void unpack(byte[] srcArr, int srcPos, int srcLength) throws DataFormatException {
			buffer.clear();
			inflater.setInput(srcArr, srcPos, srcLength);
			inflater.inflate(buffer);
			inflater.reset();
			buffer.flip();
		}
	}

	@NoArgsConstructor
	@AllArgsConstructor
	public static class PairIF {
		public int i;
		public float f;

		@Override
		public String toString() {
			return String.format("[%s <-> %s]", i, f);
		}
	}

	public static class Pair<A, B> {
		public A a;
		public B b;

		public Pair() {
		}

		public Pair(A a, B b) {
			this.a = a;
			this.b = b;
		}

		@Override
		public String toString() {
			return String.format("[%s <-> %s]", a, b);
		}
	}

	public static class Numered<T> {
		public T value;
		public int index;

		public Numered(T value, int index) {
			this.value = value;
			this.index = index;
		}

		@Override
		public String toString() {
			return String.format("[%s <-> %s]", value, index);
		}
	}

	public static class NumeredConstant<T> {
		public final T value;
		public final int index;

		public NumeredConstant(T value, int index) {
			this.value = value;
			this.index = index;
		}

		@Override
		public String toString() {
			return String.format("[%s <-> %s]", value, index);
		}
	}

	public static interface Indexed {
		public int getIndex();
	}

	public static interface NamedWithId {
		public String getName();

		public String getId();
	}

	public static class R3ICorded<T> {
		public T object;
		public int x;
		public int y;
		public int z;

		public R3ICorded(T o, int x, int y, int z) {
			this.object = o;
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public R3ICorded() {
			this.object = null;
			this.x = 0;
			this.y = 0;
			this.z = 0;
		}

		@Override
		public String toString() {
			return String.format("[(%s, %s, %s) <-> %s]", x, y, z, object);
		}
	}

	public static class R3ICordedC<T> {
		public final T object;
		public final int x;
		public final int y;
		public final int z;

		public R3ICordedC(T o, int x, int y, int z) {
			this.object = o;
			this.x = x;
			this.y = y;
			this.z = z;
		}

		@Override
		public String toString() {
			return String.format("[(%s, %s, %s) <-> %s]", x, y, z, object);
		}
	}

	public static String cutStringBefore(String str, char split) {
		int i = str.indexOf(split);
		if (i == -1) {
			return str;
		}
		return str.substring(0, i);
	}

	public static String cutStringAfter(String str, char split) {
		int i = str.indexOf(split);
		if (i == -1) {
			return str;
		}
		if (i + 1 == str.length()) {
			return "";
		}
		return str.substring(i + 1);
	}

	public static boolean truePredicate(Object o) {
		return true;
	}

	public static void collectFileTree(File root, Collection<File> collection) {
		collectFileTree(root, f -> true, collection);
	}

	public static List<File> collectFileTree(File root) {
		ArrayList<File> files = new ArrayList<>();
		collectFileTree(root, f -> true, files);
		return files;
	}

	public static List<File> collectFileTree(File root, Predicate<File> filter) {
		ArrayList<File> files = new ArrayList<>();
		collectFileTree(root, filter, files);
		return files;
	}

	public static List<File> collectFileTree(File root, Predicate<File> filter, int depth) {
		ArrayList<File> files = new ArrayList<>();
		collectFileTree(root, filter, files, depth);
		return files;
	}

	public static void collectFileTree(File root, Predicate<File> filter, Collection<File> collection) {
		collectFileTree(root, filter, collection, Integer.MAX_VALUE);
	}

	public static void collectFileTree(File root, Predicate<File> filter, Collection<File> collection, int depth) {
		if (root.isDirectory()) {
			if (depth <= 0) {
				return;
			}
			File[] files = root.listFiles();
			for (int i = 0; i < files.length; i++) {
				collectFileTree(files[i], filter, collection, depth - 1);
			}
		} else if (filter.test(root)) {
			collection.add(root);
		}
	}

	public static MessageDigest getSHA1() {
		MessageDigest md = tlSHA1.get();
		md.reset();
		return md;
	}

	public static MessageDigest getSHA256() {
		MessageDigest md = tlSHA256.get();
		md.reset();
		return md;
	}

	public static MessageDigest getMD5() {
		MessageDigest md = tlMD5.get();
		md.reset();
		return md;
	}

	private static String getOSAndArc() {
		String arc = System.getProperty("os.arch");
		//System.out.println(OS_TYPE.nameLC + "-" + arc);
		return OS_TYPE.nameLC + "-" + arc;
	}

	private static OSType getOS() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
			return OSType.WINDOWS;
		} else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
			return OSType.LINUX;
		} else if (os.contains("mac")) {
			return OSType.OSX;
		} else if (os.contains("sunos")) {
			return OSType.SOLARIS;
		}
		return OSType.UNKNOWN;
	}

	public static void dumpHeap(String filePath) {
		File f = new File(filePath);
		if (f.exists()) {
			if (!f.delete()) {
				System.out.println("Unable to dump heap: File \"" + filePath + "\" cannot be overwritten");
				return;
			}
		}
		System.out.println("Dumping heap");
		try {
			MBeanServer server = ManagementFactory.getPlatformMBeanServer();
			HotSpotDiagnosticMXBean mxBean = ManagementFactory.newPlatformMXBeanProxy(
					server,
					"com.sun.management:type=HotSpotDiagnostic",
					HotSpotDiagnosticMXBean.class
			);
			mxBean.dumpHeap(filePath, true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		System.out.println("Heap dump created \"" + filePath + "\"");
	}


	public enum OSType {
		WINDOWS, LINUX, OSX, SOLARIS, UNKNOWN;

		public final String nameLC;

		OSType() {
			this.nameLC = name().toLowerCase();
		}
	}
}
