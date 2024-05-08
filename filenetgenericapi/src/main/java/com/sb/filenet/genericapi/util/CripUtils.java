package com.sb.filenet.genericapi.util;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.sb.filenet.genericapi.exception.FNException;

public class CripUtils 
{

	public static void main(String args[])
	{
		try {

			String encryUsername = encryptStr("kge13368","HU58YZ3CR9");
			System.out.println("Encrypted username is: "+encryUsername);


			String encryPasswd = encryptStr("Pp@70961","HU58YZ3CR9");
			System.out.println("Encrypted password is: "+encryPasswd);

			System.out.println("-----------------------------------------------------");
			
			//System.out.println(decryptStr("U/CduEQI4koEZx9uD9qF+w==", "HU58YZ3CR9"));
			System.out.print("Decrypted username is: ");
			System.out.println(decryptStr("DW9E3s5HCMGNgPi7ALfTLA==", "HU58YZ3CR9"));
		
			
			System.out.print("Decrypted password is: ");
			System.out.println(decryptStr("FcibQjcbJ4T1zralumA+0g==", "HU58YZ3CR9"));
			
			//System.out.println(decryptStr("VB65IeqLrbpwTM8ppIv7SQ==", "HU58YZ3CR9"));
			
			
					
			/*
			 * GetDocumentRequest request = new GetDocumentRequest();
			 * request.setDocumentClass("CRMNEXT_DOCS"); request.setObjectStore("KMBL");
			 * request.setPassword("EdVVYV1vwUCFKR8Syda4Mg==");
			 * request.setUsername("uRTURmsRlvKrG/Dyw7CmUA=="); request.setProperties(
			 * "[{'name':'UNIQUE_NUMBER','value':'345','dataType':'STRING'}]");
			 * FilenetGenericAPIController controller = new FilenetGenericAPIController();
			 * controller.getDocument(request);
			 */
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
		// final String encodedCipherText = new sun.misc.BASE64Encoder()
		// .encode(cipherText);

		return cipherText;
	}

//	public static String encryptStr(String message,String sc) throws Exception {
//		byte[] encryptedByte = CripUtils.encrypt(message,sc);
//		return new sun.misc.BASE64Encoder().encode(encryptedByte);
//	}
	
	

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


	public static String decryptStr(String message,String sc) {
		try {
//			byte[] decBtye = new sun.misc.BASE64Decoder().decodeBuffer(message);
//			return decrypt(decBtye,sc);
			
			byte[] decodedBytes = Base64.getDecoder().decode(message);
		    return decrypt(decodedBytes, sc);
		}
		catch (Exception e) {
			throw new FNException("Invalid username/password. Please check.");
		}
	}

	/*
	 * public static int toNearestWholeMinute() { Calendar c = new
	 * GregorianCalendar(); //Date d = c.getTime(); Date d = new Date();
	 * c.setTime(d);
	 * 
	 * if (c.get(Calendar.SECOND) >= 30) c.add(Calendar.MINUTE, 1);
	 * 
	 * c.set(Calendar.SECOND, 0);
	 * 
	 * return c.get(Calendar.MINUTE); }
	 * 
	 * static Date toNearestWholeHour(Date d) { Calendar c = new
	 * GregorianCalendar(); c.setTime(d);
	 * 
	 * if (c.get(Calendar.MINUTE) >= 30) c.add(Calendar.HOUR, 1);
	 * 
	 * c.set(Calendar.MINUTE, 0); c.set(Calendar.SECOND, 0);
	 * 
	 * return c.getTime(); }
	 */
	
	
}
