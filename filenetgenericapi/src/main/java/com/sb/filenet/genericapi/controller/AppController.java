package com.sb.filenet.genericapi.controller;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/rest/api")
public class AppController {
	private final Logger log = Logger.getLogger(AppController.class);

	@PostMapping(value = "/health")
	public String health()  {
		if(log.isInfoEnabled())
			log.info("<< health check  FileNet API ::");
		return "Hello FileNet Generic API is up and running.";
	}


}
