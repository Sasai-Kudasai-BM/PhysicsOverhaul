package net.skds.lib2.utils;

import lombok.Getter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntConsumer;

@SuppressWarnings("unused")
public class HttpUtils { // TODO

	private static final HttpClient.Builder builder = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(10));

	public static Map<String, String> queryToMap(String query) {
		if (query == null || query.isEmpty()) {
			return Map.of();
		}
		Map<String, String> map = new HashMap<>();
		String[] arr = query.split("&");
		for (int i = 0; i < arr.length; i++) {
			String val = arr[i];
			int pos = val.indexOf('=');
			if (pos != -1 && pos < val.length() - 1) {
				map.put(val.substring(0, pos), val.substring(pos + 1));
			}
		}
		return map;
	}


	public static DownloadProcess downloadFromNet(String url) {
		try {
			HttpClient client = builder.build();
			HttpRequest request = HttpRequest.newBuilder(URI.create(url)).build();
			//System.out.println(request.headers().map());
			var response = client.send(request, ri -> HttpResponse.BodySubscribers.ofInputStream());
			if (response.statusCode() != 200) {
				throw new RuntimeException("non-ok response " + response.statusCode() + " on " + url);
			}
			List<String> cl = response.headers().map().get("content-length");
			if (cl == null || cl.isEmpty()) {
				return new DownloadProcess(response.statusCode(), 0, response.body());
				//throw new RuntimeException("content-length not provided");
			}
			int len = Integer.parseInt(cl.get(0));
			return new DownloadProcess(response.statusCode(), len, response.body());

		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new RuntimeException("Unable to download " + url);
	}

	public record Response(int code, byte[] data, HttpResponse<byte[]> response) {
	}

	public static Response doRequest(HttpRequest request) {
		try {
			HttpClient client = builder.build();
			var response = client.send(request, ri -> HttpResponse.BodySubscribers.ofByteArray());
			return new Response(response.statusCode(), response.body(), response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new RuntimeException("Unable to download " + request.uri());
	}

	public static Response doRequest(String url, byte[] requestBody) {
		try {
			HttpClient client = builder.build();
			HttpRequest request = HttpRequest.newBuilder(URI.create(url))
					.POST(HttpRequest.BodyPublishers.ofByteArray(requestBody))
					.build();
			var response = client.send(request, ri -> HttpResponse.BodySubscribers.ofByteArray());
			return new Response(response.statusCode(), response.body(), response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new RuntimeException("Unable to download " + url);
	}

	public static class DownloadProcess {

		private final BufferedInputStream inputStream;
		@Getter
		private final byte[] content;
		@Getter
		private volatile int progress = 0;
		@Getter
		private final int responseCode;

		protected DownloadProcess(int code, int size, InputStream is) {
			this.responseCode = code;
			this.content = new byte[size];
			this.inputStream = new BufferedInputStream(is);
		}

		public void readAll() throws IOException {
			do {
				this.progress += inputStream.read(content, progress, content.length - progress);
			} while (!isReady());
			inputStream.close();
		}

		public boolean read() throws IOException {
			if (isReady()) {
				return false;
			}
			this.progress += inputStream.read(content, progress, content.length - progress);
			boolean ready = isReady();
			if (ready) {
				inputStream.close();
			}
			return !ready;
		}

		public void readAll(IntConsumer action) throws IOException {
			do {
				int r = inputStream.read(content, progress, content.length - progress);
				this.progress += r;
				action.accept(r);
			} while (!isReady());
			inputStream.close();
		}

		public boolean checkSHA1(String sha1) {
			try {
				byte[] sha = SKDSUtils.HEX_FORMAT_LC.parseHex(sha1);
				MessageDigest md = MessageDigest.getInstance("SHA1");
				return Arrays.equals(md.digest(content), sha);
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException(e);
			}
		}

		public boolean checkSHA1(byte[] sha1) {
			try {
				MessageDigest md = MessageDigest.getInstance("SHA1");
				return Arrays.equals(md.digest(content), sha1);
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException(e);
			}
		}

		public int getSize() {
			return content.length;
		}

		public boolean isReady() {
			return progress == content.length;
		}
	}
}
