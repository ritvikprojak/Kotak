package com.sb.filenet.genericapi.util;



import javax.crypto.Cipher;



import org.apache.log4j.Logger;





import java.security.KeyFactory;

import java.security.NoSuchAlgorithmException;

import java.security.PrivateKey;

import java.security.PublicKey;

import java.security.spec.InvalidKeySpecException;

import java.security.spec.PKCS8EncodedKeySpec;

import java.security.spec.X509EncodedKeySpec;

import java.util.Base64;



public class EncryptionUtility {

	private Logger logger = Logger.getLogger(EncryptionUtility.class);



	private static String publicKeys ;//= "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAoPibOxn7ggLHQqGbq8MvbUjsih9Ma2e2yJuXDcb5Q7PF0lrOyLYl62nXzMx4VATCDCQ9t/F7cH9IYVnFybB+9K8g36NVKfttqVOpkY1RqWdkWkSjAnzQIBOHPh+409ySH9876gU/mSUGcCKuYDI7Lz8ZhqQ+iUReyK0lPGcObG7UhSLwcJE8On0NQm3a/pFS91DpfZDtSi59kmA7W41PDbVqPwQUZJ1FzGPz3T/FNSRGswkRoZsyARcUBLnDrQBGI/KwktDS3z+iLKjdI/E8OJKiBkeX1YR8BhArBCo9BuI+xEjpofbQHWQ/W+c+tIFu2aFDXc8ajrFt89V12tPX3ShHjv+lwYydxiSozNgSAzlX2iJoQgp89v2xHl/91vjp6xQ8yFscl/mHB0IRSkvMLWDysyqab4doLCJ/8AGEC6vAZQ2VYY3KmhEU1U7HMtIckuwGm1aDUOcfFZDXf6hlKxq7ehZ8oNHBWbIAVOxGGYXk3ewdyN9MPIUQ3b0tK2pyaB1CzA9QVA28M22SdZgLDOvN3gIefIMFfBQRfWVM+mHBYiDKRh19vDeXaVZOZJeAHhshbebYTG9QO7a6DE4p/RuLgi2FD/k9wl8XSvb4iq9Q21Wkve8pl74npOeSeppDOsL7SKEL02d2XLf/hiij3HHglSnPq0TeqEKm44l//HECAwEAAQ==";

	private static String privateKeys ;//= "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";



	public EncryptionUtility( String pubKey, String pvtKey )

	{



		if ( pubKey == null )

		{

			logger.error("Public key was null");

			throw new IllegalArgumentException( "Public key was null" );



		}





		if ( pvtKey == null )

		{

			logger.error("Private key was null");

			throw new IllegalArgumentException( "Private key was null" );

		}



		publicKeys=pubKey;

		privateKeys=pvtKey;



	}



	public static PublicKey getPublicKey(){

		PublicKey publicKey = null;

		try{

			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeys.getBytes()));

			KeyFactory keyFactory = KeyFactory.getInstance("RSA");

			publicKey = keyFactory.generatePublic(keySpec);

			return publicKey;

		} catch (NoSuchAlgorithmException e) {

			e.printStackTrace();

		} catch (InvalidKeySpecException e) {

			e.printStackTrace();

		}

		return publicKey;

	}



	public static PrivateKey getPrivateKey(){

		PrivateKey privateKey = null;

		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeys.getBytes()));

		KeyFactory keyFactory = null;

		try {

			keyFactory = KeyFactory.getInstance("RSA");

		} catch (NoSuchAlgorithmException e) {

			e.printStackTrace();

		}

		try {

			privateKey = keyFactory.generatePrivate(keySpec);

		} catch (InvalidKeySpecException e) {

			e.printStackTrace();

		}

		return privateKey;

	}



	public static byte[] encrypt(String data) throws EncryptionException {

		if ( data == null || data.trim().length() == 0 )

			throw new IllegalArgumentException("unencrypted string was null or empty" );

		try 

		{

			Cipher cipher = Cipher.getInstance("RSA");

			cipher.init(Cipher.ENCRYPT_MODE, getPublicKey());

			return cipher.doFinal(data.getBytes());

		}

		catch (Exception e)

		{

			throw new EncryptionException( e );

		}

	}



	public  String decrypt(String  encrydata) throws EncryptionException {

		if ( encrydata == null || encrydata.trim().length() == 0 )

			throw new IllegalArgumentException("unencrypted string was null or empty" );



		try {

			Cipher cipher = Cipher.getInstance("RSA");

			cipher.init(Cipher.DECRYPT_MODE, getPrivateKey());

			return new String(cipher.doFinal((Base64.getDecoder().decode(encrydata.getBytes()))));



		}

		catch (Exception e)

		{

			throw new EncryptionException( e );

		}



	}



	/*public  String decrypt(String data) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {

		return decrypt(Base64.getDecoder().decode(data.getBytes()), getPrivateKey());

	}*/



	public static class EncryptionException extends Exception

	{

		public EncryptionException( Throwable t )

		{

			super( t );

		}

	}



	public static void main(String[] args) throws EncryptionException {



		EncryptionUtility Encrypti = new EncryptionUtility("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");

		

		String decryptedString = Encrypti.decrypt("duXLH59pBLHJN7Jka9sgcLFZIRDW21fB8YrlwjZ9Prh50+q+HNZYwrBH9KEb+pkMP5M4pzKEbqBVq7jrGv4uDflPlEih0PoNMB4cZUETYAUDj2V6imhMJEWGtXTjREVojtEEm4VjaOeAdYuG2FJE30rhzNVpk29eP0ZxF1Hjnn1SRAK05BuN1IttNQ9sB3oBVrvXnbIkVmXDSt/Tw2Ek4Mpxfg1sGIQqQAyNrMEw+/nEvNNhdqlIRkvw/1E5zKk9RXSIWZ+dufbQCE0NUy+kMcgQ4Iedz9z7WPVevvst4ajzk5jAmLVE5lNN8nXzxadvQmbtaIR7pncMM5y3IKv8jqvX5HTw6vLeQYCIuwFCENcvkZUI3ZBXlBMhC9ebxuOkQGxpzxt/1CekXM9dXJgLzgFskKu5uQKNUeDRwRIh3OIKC4tSE9YN+88e/XuzAK87IHhc0vo8O+GV9kQBz/2V6DG8aoOeflO9ZN63QIt4Ujcd1z9T2z0epgV6TkbEtOafBAhuPXiBQAw9cR1/YCHurduCjjgw8Hgw3jmu9a8hNAQj1Ll1+16VGpgkVL9BO+M1MIDFnvEZ3xt3U49gqT9FPwf0A+IwElgYtoVF70zCF/5IQiSOFs6rIy05TPpHSBw+jtDlGEiPePOarE9fGt0cn/8DIX3v/2JF4yQGA9Exduk=");

		System.out.println(decryptedString);



	}

}

