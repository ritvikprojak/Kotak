package com.abc.kotak.web.rest.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.abc.kotak.model.InputObj;


@Component
public class Script {
	
	public String Encryt(String plainText) throws FileNotFoundException, ScriptException, NoSuchMethodException {
		
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("nashorn");
		ClassLoader classLoader = getClass().getClassLoader();
		String path = classLoader.getResource("SimpleAES.js").getPath();
		engine.eval(new FileReader(path));
		Invocable inv = (Invocable) engine;
		InputObj obj = new InputObj();
		obj.setKey("Sumit");
		obj.setnBits(256);
		obj.setText(plainText);
		Object temp = inv.invokeFunction("AESEncryptCtr", obj);
		String cipherText = (String) temp;
		System.out.println(cipherText);
		return cipherText;
		
	}
	
	public String EncrytResponse(String plainText, String nkey , int nBits) throws FileNotFoundException, ScriptException, NoSuchMethodException {
		
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("nashorn");
		ClassLoader classLoader = getClass().getClassLoader();
		String path = classLoader.getResource("SimpleAES.js").getPath();
		engine.eval(new FileReader(path));
		Invocable inv = (Invocable) engine;
		InputObj obj = new InputObj();
		obj.setKey(nkey);
		obj.setnBits(nBits);
		obj.setText(plainText);
		Object temp = inv.invokeFunction("AESEncryptCtr", obj);
		String cipherText = (String) temp;
		System.out.println(cipherText);
		return cipherText;
		
	}
	
	public String Decrypt(String cipherText) throws FileNotFoundException, ScriptException, NoSuchMethodException {
		
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("nashorn");
		ClassLoader classLoader = getClass().getClassLoader();
		String path = classLoader.getResource("SimpleAES.js").getPath();
		engine.eval(new FileReader(path));
		Invocable inv = (Invocable) engine;
		InputObj obj = new InputObj();
		obj.setKey("Sumit");
		obj.setnBits(256);
		obj.setText(cipherText);
		Object temp = inv.invokeFunction("AESDecryptCtr", obj);
		String plainText = (String) temp;
		System.out.println(plainText);
		return plainText;
		
	}
	
	public String DecryptResponse(String cipherText, String nkey, int nBits) throws FileNotFoundException, ScriptException, NoSuchMethodException {
		
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("nashorn");
		ClassLoader classLoader = getClass().getClassLoader();
		String path = classLoader.getResource("SimpleAES.js").getPath();
		engine.eval(new FileReader(path));
		Invocable inv = (Invocable) engine;
		InputObj obj = new InputObj();
		obj.setKey(nkey);
		obj.setnBits(nBits);
		obj.setText(cipherText);
		Object temp = inv.invokeFunction("AESDecryptCtr", obj);
		String plainText = (String) temp;
		System.out.println(plainText);
		return plainText;
		
	}
	
	public static String decoding(String text) {
		try {
			//byte[] decoded = new sun.misc.BASE64Decoder().decodeBuffer(text);
			byte[] decoded = Base64.getDecoder().decode(text.getBytes());
			System.out.println("Decoding" + text + "to >>>>>" + decoded);
			String decode = new String(decoded);
			return decode;
		} catch (Exception ex) {
			System.out.println("Error at Decoding Text" + text);
			System.out.println(ex.getStackTrace());
			return text;
		}
	}

	public static void main(String[] args) throws ScriptException, IOException, NoSuchMethodException {

		// TODO Auto-generated method stub
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("nashorn");
		engine.eval(new FileReader("C:\\Users\\Lenovo\\Desktop\\Kotak\\kotak_WAS\\src\\main\\resources\\SimpleAES.js"));
		Invocable inv = (Invocable) engine;
		InputObj obj = new InputObj();
//		System.out.println("Sumit");
//		obj.setKey("kotak");
//		obj.setnBits(256);
//		obj.setText("NzY3MWE4ZDI4ODAxMDAwMCBmMWU2MTcxMg==");
//		Object temp = inv.invokeFunction("AESEncryptCtr", obj);
		InputObj obj1 = new InputObj();
		obj1.setKey("kotak");
		obj1.setnBits(256);
		obj1.setText("NzY3MWE4ZDI4ODAxMDAwMCBmMWU2MTcxMg==");
		Object temp2 = inv.invokeFunction("AESDecryptCtr", obj1);
//		System.out.println((String)temp);
		System.out.println((String)temp2);
		System.out.println(decoding((String)temp2));

	}

}
