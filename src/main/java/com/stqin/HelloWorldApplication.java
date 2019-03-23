package com.stqin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@EnableAutoConfiguration
public class HelloWorldApplication {

	@RequestMapping(value = "/sayHello")
	@ResponseBody
	public String sayHello(String name) {
		return "Hello," + name;
	}

	public static void main(String[] args) {
		SpringApplication.run(HelloWorldApplication.class, args);
	}
}
