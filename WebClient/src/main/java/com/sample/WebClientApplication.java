package com.sample;



import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.sample.model.User;
import com.sample.service.UserService;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class WebClientApplication implements CommandLineRunner {

	private final UserService userService;
	public WebClientApplication(UserService userService) {
		this.userService = userService;
	}
	
	public static void main(String[] args) {
		SpringApplication.run(WebClientApplication.class, args);
	}
	
	 @Override
	    public void run(final String... args) {
//	        userService
//	                .getUserByIdAsync("1")
//	                .subscribe(user -> log.info("Get user async: {}", user));
//	        
//	        
//	       User user = userService.getUserByIdSync("1");
//	       log.info(user.getName()+"---------------Sync");
	       
	      log.info(userService.getPost().getName());
	      
	        userService
          .getUserByIdAsync("1")
          .subscribe(user -> log.info("Get user async: {}", user));
	    }
}
