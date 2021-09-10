package com.example.newrelictesta01;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.annotation.Timed;

@RestController
@Timed
public class TestController {
	
	@RequestMapping("/test")
	public String test() {
		return "hello, world. this is /test";
	}
}
