package com.sample.service;



import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sample.model.User;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
@Slf4j
public class UserService {
	private static final String USERS_URL_TEMPLATE = "/users/{id}";
	private static final String BROKEN_URL_TEMPLATE = "/broken-url/{id}";
	private static final String POST_URL_TEMPLATE = "/posts";
	public static final int DELAY_MILLIS = 100;
	public static final int MAX_RETRY_ATTEMPTS = 3;
	private final WebClient webClient;
	private final WebClient webClient2;

	public UserService(@Qualifier("BASE")WebClient webClient, 
			@Qualifier("LOCAL")WebClient webClient2) {
		this.webClient = webClient;
		this.webClient2 = webClient2;
	}
	
	   public Mono<User> getUserByIdAsync(final String id) {
	        return webClient
	                .get()
	                .uri(String.join("", "/users/", id))
	                .retrieve()
	                .bodyToMono(User.class);
	    }
	   public User getUserByIdSync(final String id) {
	        return webClient
	                .get()
	                .uri(USERS_URL_TEMPLATE, id)
	                .retrieve()
	                .bodyToMono(User.class)
	                .block();
	    }

	    public User getUserWithRetry(final String id) {
	        return webClient
	                .get()
	                .uri(BROKEN_URL_TEMPLATE, id)
	                .retrieve()
	                .bodyToMono(User.class)
	                .retryWhen(Retry.fixedDelay(MAX_RETRY_ATTEMPTS, Duration.ofMillis(DELAY_MILLIS)))
	                .block();
	    }

	    public User getUserWithFallback(final String id) {
	        return webClient
	                .get()
	                .uri(BROKEN_URL_TEMPLATE, id)
	                .retrieve()
	                .bodyToMono(User.class)
	                .doOnError(error -> log.error("An error has occurred {}", error.getMessage()))
	                .onErrorResume(error -> Mono.just(new User()))
	                .block();
	    }

	    public User getUserWithErrorHandling(final String id) {
	        return webClient
	                .get()
	                .uri(BROKEN_URL_TEMPLATE, id)
	                .retrieve()
	                .onStatus(HttpStatus::is4xxClientError,
	                        error -> Mono.error(new RuntimeException("API not found")))
	                .onStatus(HttpStatus::is5xxServerError,
	                        error -> Mono.error(new RuntimeException("Server is not responding")))
	                .bodyToMono(User.class)
	                .block();
	    }
	    public User getPost() {
	    	   User userInfo = new User();
	    	   userInfo.setId(1);
	    	   userInfo.setName("dlehden1");
	    	   userInfo.setEmail("dlehden1@nav.com");

	           return webClient2.post()         // POST method
	               .uri("/api/users")    // baseUrl 이후 uri
	               .bodyValue(userInfo)     // set body value
	               .retrieve()                 // client message 전송
	               .bodyToMono(User.class)  // body type : EmpInfo
	               .block();                   // await
	    }
}
