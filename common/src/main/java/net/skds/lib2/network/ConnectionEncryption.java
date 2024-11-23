package net.skds.lib2.network;

import lombok.Getter;
import net.skds.lib2.mat.FastMath;
import net.skds.lib2.utils.SKDSByteBuf;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

public class ConnectionEncryption {

	private static final KeyPairGenerator generator;
	private static final KeyFactory kf;

	@Getter
	boolean enabled = false;
	@Getter
	boolean ready = false;

	@Getter
	private byte[] publicKey;
	@Getter
	private byte[] token;

	@Getter
	private Cipher encryptor;
	@Getter
	private Cipher decryptor;

	public ConnectionEncryption(boolean createRSA) {
		if (createRSA) {
			KeyPair kp = generator.generateKeyPair();
			try {
				decryptor = Cipher.getInstance("RSA");
				decryptor.init(Cipher.DECRYPT_MODE, kp.getPrivate());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			this.publicKey = kp.getPublic().getEncoded();
			this.token = new byte[64];
			FastMath.RANDOM.nextBytes(this.token);
		}
	}

	public byte[] createSecret(byte[] publicKey) {
		try {
			byte[] secret = new byte[16];
			FastMath.RANDOM.nextBytes(secret);

			SecretKey key = new SecretKeySpec(secret, 0, secret.length, "AES");
			IvParameterSpec iv = new IvParameterSpec(secret);
			this.encryptor = Cipher.getInstance("AES/CFB8/NoPadding");
			encryptor.init(Cipher.ENCRYPT_MODE, key, iv);
			this.decryptor = Cipher.getInstance("AES/CFB8/NoPadding");
			decryptor.init(Cipher.DECRYPT_MODE, key, iv);

			var enc = Cipher.getInstance("RSA");
			enc.init(Cipher.ENCRYPT_MODE, kf.generatePublic(new X509EncodedKeySpec(publicKey)));
			return enc.doFinal(secret);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public boolean applySecret(byte[] secret, byte[] tkn) {
		if (!Arrays.equals(this.token, tkn)) {
			return false;
		}

		try {
			secret = decryptor.doFinal(secret);
			SecretKey key = new SecretKeySpec(secret, 0, secret.length, "AES");
			IvParameterSpec iv = new IvParameterSpec(secret);
			this.encryptor = Cipher.getInstance("AES/CFB8/NoPadding");
			encryptor.init(Cipher.ENCRYPT_MODE, key, iv);
			this.decryptor = Cipher.getInstance("AES/CFB8/NoPadding");
			decryptor.init(Cipher.DECRYPT_MODE, key, iv);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		token = null;
		publicKey = null;
		enabled = true;
		return true;
	}

	public void encrypt(SKDSByteBuf buffer, int from, int to) {
		if (encryptor == null) return;
		byte[] data = encryptor.update(buffer.array(), from, to - from);
		buffer.getBuffer().put(from, data);
	}

	public void decrypt(SKDSByteBuf buffer, int from, int to) {
		if (decryptor == null) return;
		byte[] data = decryptor.update(buffer.array(), from, to - from);
		buffer.getBuffer().put(from, data);
	}

	static {
		try {
			generator = KeyPairGenerator.getInstance("RSA");
			kf = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		generator.initialize(2048);
	}
}
