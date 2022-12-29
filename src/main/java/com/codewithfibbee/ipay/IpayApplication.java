package com.codewithfibbee.ipay;

import com.codewithfibbee.ipay.config.WebClientHandler;
import com.google.gson.Gson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.net.http.HttpClient;

@SpringBootApplication
public class IpayApplication {
	@Bean
	public HttpClient httpClient(){
		return HttpClient.newHttpClient();
	}

	@Bean
	public Gson gson(){
		return new Gson();
	}

	@Bean
	public WebClientHandler webClientHandler(){
		return new WebClientHandler(httpClient(), gson());
	}

	public static void main(String[] args) {
		SpringApplication.run(IpayApplication.class, args);
	}

}
