package com.ktk.Kotak.util;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CripUtils 
{

	public static void main(String args[])
	{
		try {

			String encryUsername = encryptStr("navreports","HU58YZ3CR9");
			// System.out.println("Encrypted username is: "+encryUsername);


			String encryPasswd = encryptStr("Qdhabolt#429","HU58YZ3CR9");
			// System.out.println("Encrypted password is: "+encryPasswd);

			// System.out.println("-----------------------------------------------------");
			
			System.out.print("Decrypted username is: ");
			// System.out.println(decryptStr("W/K0PQ6FLNJABqqeNb/JjA==", "HU58YZ3CR9"));
		
			
			System.out.print("Decrypted password is: ");
			// System.out.println(decryptStr("e5zq3nJ9xxyHqzeqeCQujQ==", "HU58YZ3CR9"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private static byte[] encrypt(String message,String secretKey) throws Exception {
		//String secretKey = systemProperties.getProperty("SECRETKEY");

		final MessageDigest md = MessageDigest.getInstance("md5");
		final byte[] digestOfPassword = md.digest(secretKey.getBytes("utf-8"));
		final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
		
		for (int j = 0, k = 16; j < 8;) 
		{
			keyBytes[k++] = keyBytes[j++];
		}

		final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
		final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
		final Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key, iv);

		final byte[] plainTextBytes = message.getBytes("utf-8");
		final byte[] cipherText = cipher.doFinal(plainTextBytes);
		
		return cipherText;
	}
	

	public static String encryptStr(String message, String sc) throws Exception {
	    byte[] encryptedBytes = CripUtils.encrypt(message, sc);
	    return Base64.getEncoder().encodeToString(encryptedBytes);
	}

	private static String decrypt(byte[] message,String secretKey ) throws GeneralSecurityException, IOException {
		/* String secretKey = systemProperties.getProperty("SECRETKEY"); */
		final MessageDigest md = MessageDigest.getInstance("md5");
		final byte[] digestOfPassword = md.digest(secretKey.getBytes("utf-8"));
		final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
		for (int j = 0, k = 16; j < 8;) {
			keyBytes[k++] = keyBytes[j++];
		}

		final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
		final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
		final Cipher decipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
		decipher.init(Cipher.DECRYPT_MODE, key, iv);

		final byte[] plainText = decipher.doFinal(message);
		return new String(plainText, "UTF-8");
	}


	public static String decryptStr(String message,String sc) throws Exception {
		try {
			
			byte[] decodedBytes = Base64.getDecoder().decode(message);
		    return decrypt(decodedBytes, sc);
		}
		catch (Exception e) {
			throw new Exception("Invalid username/password. Please check.");
		}
	}

}