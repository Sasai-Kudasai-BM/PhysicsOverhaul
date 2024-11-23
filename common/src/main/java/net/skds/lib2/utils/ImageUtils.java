package net.skds.lib2.utils;

import net.skds.lib2.mat.FastMath;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.spi.ImageReaderSpi;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;

@SuppressWarnings("unused")
public class ImageUtils {

	private static final ImageReaderSpi PNG_READER;
	private static final ImageReaderSpi JPG_READER;
	private static final ImageReaderSpi GIF_READER;
	private static final ImageReaderSpi TIFF_READER;
	private static final ImageInputStreamSpi INPUT_STREAM_SPI;


	public static BufferedImage readPNG(final InputStream is) {
		return readImage(is, PNG_READER);
	}

	public static BufferedImage readJPG(final InputStream is) {
		return readImage(is, JPG_READER);
	}

	public static BufferedImage readGIF(final InputStream is) {
		return readImage(is, GIF_READER);
	}

	public static BufferedImage readTIFF(final InputStream is) {
		return readImage(is, TIFF_READER);
	}

	private static BufferedImage readImage(final InputStream is, final ImageReaderSpi readerSpi) {
		try {
			final ImageReader reader = readerSpi.createReaderInstance();
			final ImageReadParam param = reader.getDefaultReadParam();
			reader.setInput(INPUT_STREAM_SPI.createInputStreamInstance(is, false, null), true, true);
			BufferedImage bi = reader.read(0, param);
			reader.dispose();
			return bi;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static int packARGB(int r, int g, int b, int a) {
		return (((0xff & a) << 8 | (0xff & r)) << 8 | (0xff & g)) << 8 | (0xff & b);
	}

	public static int packRGB(int r, int g, int b) {
		return ((0xff & r) << 8 | (0xff & g)) << 8 | (0xff & b);
	}

	public static int packARGB(byte r, byte g, byte b, byte a) {
		return ((a << 8 | r) << 8 | g) << 8 | b;
	}

	public static int packRGB(byte r, byte g, byte b) {
		return (r << 8 | g) << 8 | b;
	}

	public static int packARGB(float r, float g, float b, float a) {
		return (((0xff & FastMath.floor(a * 255)) << 8 |
				(0xff & FastMath.floor(r * 255))) << 8 |
				(0xff & FastMath.floor(g * 255))) << 8 |
				(0xff & FastMath.floor(b * 255));
	}

	public static int packRGB(float r, float g, float b) {
		return ((0xff & FastMath.floor(r * 255)) << 8 |
				(0xff & FastMath.floor(g * 255))) << 8 |
				(0xff & FastMath.floor(b * 255));
	}

	public static byte unpackR(int argb) {
		return (byte) (argb >> 16 & 0xff);
	}

	public static byte unpackG(int argb) {
		return (byte) (argb >> 8 & 0xff);
	}

	public static byte unpackB(int argb) {
		return (byte) (argb & 0xff);
	}

	public static byte unpackA(int argb) {
		return (byte) (argb >> 24);
	}


	public static byte[] writeImageToArrayPng(final BufferedImage image) {
		return writeImageToArray(image, "png");
	}

	public static byte[] writeImageToArrayJpg(final BufferedImage image) {
		return writeImageToArray(image, "jpg");
	}

	private static byte[] writeImageToArray(final BufferedImage image, String format) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(image, format, os); // TODO speedup
			return os.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	static {
		ImageReaderSpi gif = null;
		ImageReaderSpi tiff = null;
		ImageReaderSpi jpg = null;
		ImageReaderSpi png = null;
		ImageInputStreamSpi tmpIn = null;
		for (Iterator<ImageReaderSpi> it = IIORegistry.getDefaultInstance().getServiceProviders(ImageReaderSpi.class, false); it.hasNext(); ) {
			var p = it.next();
			Set<String> formats = Set.of(p.getFormatNames());
			if (formats.contains("png")) {
				png = p;
			} else if (formats.contains("jpg")) {
				jpg = p;
			} else if (formats.contains("gif")) {
				gif = p;
			} else if (formats.contains("tiff")) {
				tiff = p;
			}
		}
		for (Iterator<ImageInputStreamSpi> it = IIORegistry.getDefaultInstance().getServiceProviders(ImageInputStreamSpi.class, false); it.hasNext(); ) {
			var p = it.next();
			if (InputStream.class == p.getInputClass()) {
				tmpIn = p;
				break;
			}
		}
		PNG_READER = png;
		JPG_READER = jpg;
		GIF_READER = gif;
		TIFF_READER = tiff;
		INPUT_STREAM_SPI = tmpIn;
	}
}
