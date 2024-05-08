package com.sb.filenet.genericapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Properties;

@SpringBootApplication
@EnableWebMvc
@EnableAutoConfiguration
public class FilenetGenericAPIApplication {

	public static void main(String[] args) {
		SpringApplication.run(FilenetGenericAPIApplication.class, args);
		System.out.println("FILENET GENERIC API STARTED");
	}

	@Bean("systemProperties")

	@Autowired
	public Properties getSystemProperties(@Value("${ce.url}") String url, @Value("${ce.stanza}") String stanza,
			@Value("${encryption.required}") String enc,
			@Value("${secret.key}") String secretkey/*
													 * ,
													 * 
													 * @Value("${ras.enc.pub.key}") String
													 * pubKey, @Value("${ras.enc.private.key}") String privateKey
													 */) {
		Properties ceConnectionProps = new Properties();
		ceConnectionProps.put("URI", url);
		ceConnectionProps.put("Stanza", stanza);
		ceConnectionProps.put("ENC", enc);
		ceConnectionProps.put("SECRETKEY", secretkey);
		//ceConnectionProps.put("publickey", pubKey);
		//ceConnectionProps.put("privatekey", privateKey);
		return ceConnectionProps;
	}

}
