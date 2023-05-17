package com.duluthtechnologies.ocpi.persistence.helper;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import jakarta.persistence.AttributeConverter;

@Component
public class TokenEncryptor implements AttributeConverter<String, String> {

	private static final Logger LOG = LoggerFactory.getLogger(TokenEncryptor.class);

	private static final String ALGORITHM = "AES/GCM/NoPadding";
	private static final int TAG_LENGTH_BIT = 128;
	private static final int IV_LENGTH_BYTE = 128;
	private static final int SALT_LENGTH_BYTE = 128;

	private final String passwordToGenerateSecretKey;

	public TokenEncryptor(@Qualifier("encryption-password") String passwordToGenerateSecretKey) throws Exception {
		this.passwordToGenerateSecretKey = passwordToGenerateSecretKey;
	}

	@Override
	public String convertToDatabaseColumn(String attribute) {
		if (attribute == null) {
			return null;
		}
		try {
			byte[] salt = getRandomNonce(SALT_LENGTH_BYTE);
			byte[] iv = getRandomNonce(IV_LENGTH_BYTE);
			SecretKey secretKey = getAESKeyFromPassword(passwordToGenerateSecretKey.toCharArray(), salt);
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
			byte[] cipherText = cipher.doFinal(attribute.getBytes());
			byte[] cipherTextWIthIvSalt = ByteBuffer.allocate(iv.length + salt.length + cipherText.length).put(iv)
					.put(salt).put(cipherText).array();
			return Base64.getEncoder().encodeToString(cipherTextWIthIvSalt);
		} catch (Exception e) {
			String message = "Exception caught while encrypting token";
			LOG.error(message);
			throw new RuntimeException(message, e);
		}
	}

	@Override
	public String convertToEntityAttribute(String entityAttribute) {
		if (entityAttribute == null) {
			return null;
		}
		try {
			byte[] decodedEntityAttribute = Base64.getDecoder().decode(entityAttribute);
			ByteBuffer byteBuffer = ByteBuffer.wrap(decodedEntityAttribute);
			byte[] iv = new byte[IV_LENGTH_BYTE];
			byteBuffer.get(iv);
			byte[] salt = new byte[SALT_LENGTH_BYTE];
			byteBuffer.get(salt);
			byte[] cipherText = new byte[byteBuffer.remaining()];
			byteBuffer.get(cipherText);
			SecretKey secretKey = getAESKeyFromPassword(passwordToGenerateSecretKey.toCharArray(), salt);
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
			return new String(cipher.doFinal(cipherText));
		} catch (Exception e) {
			String message = "Exception caught while decrypting token";
			LOG.error(message);
			throw new RuntimeException(message, e);
		}
	}

	private static byte[] getRandomNonce(int numBytes) {
		byte[] nonce = new byte[numBytes];
		new SecureRandom().nextBytes(nonce);
		return nonce;
	}

	private static SecretKey getAESKeyFromPassword(char[] password, byte[] salt)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		KeySpec spec = new PBEKeySpec(password, salt, 65536, 256);
		SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
		return secret;
	}
}