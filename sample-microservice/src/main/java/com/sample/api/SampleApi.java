package com.sample.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sample")
public class SampleApi {
	
	@GetMapping("/api/v1")
	public String sayHello() {
		return "Hello World!";
	}

}
