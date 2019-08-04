package io.javabrains.MovieCatalogService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
@EnableCircuitBreaker
public class MovieCatalogServiceApplication {

	@Bean
    @LoadBalanced //the URL I send you is a hint, look it up in Eureka. you cannot use the url now
	public RestTemplate getRestTemplate(){
		return new RestTemplate();
	}

    @Bean
    public WebClient.Builder getWebClientBuilder(){
        return WebClient.builder();
    }
    public static void main(String[] args) {
		SpringApplication.run(MovieCatalogServiceApplication.class, args);
	}

}
