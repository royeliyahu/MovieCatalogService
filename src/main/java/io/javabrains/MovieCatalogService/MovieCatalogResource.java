package io.javabrains.MovieCatalogService;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.ribbon.proxy.annotation.Hystrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientCodecCustomizer;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    @Autowired
    // allowa sync call old way.   a thread save object
    private RestTemplate restTemplate;
    @Autowired
    // allowas an async calls  new way
    private WebClient.Builder webClientBuilder;

    @RequestMapping("/{userId}")
    @HystrixCommand(fallbackMethod = "getFallbackCatalog",
    commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000")
    })
    public List<CatalogItem> getCatalog(@PathVariable String userId){

        WebClient.Builder builder = WebClient.builder();
//        UserRating userRating = restTemplate.getForObject("http://localhost:8083/ratingdata/users/" + userId, UserRating.class);
        //using Eureka
        UserRating userRating = getUserRating(userId);
        // get all rated movie ids

        return userRating.getUserRatings().stream().map(rat -> {
            //a sync call  using RestTemplate an old way of doing
//            Movie movie = restTemplate.getForObject("http://localhost:8081/movies/" + rat.getMovieId(), Movie.class);
            //using Eureka
            return getCatalogItem(rat);
        }).collect(Collectors.toList());
        //for each movie id call movie info service and get details


        //put them all together
//        return Collections.singletonList(
//                new CatalogItem("Transformers", "test", 4)
//        );
    }

    @HystrixCommand(fallbackMethod = "getFallbackCatalogItem",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000")
            },threadPoolKey = "CatalogItem",
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "20"),
                    @HystrixProperty(name = "maxQueueSize", value = "10")
            })
    private CatalogItem getCatalogItem(Rating rat) {
        Movie movie = restTemplate.getForObject("http://info/movies/" + rat.getMovieId(), Movie.class);

//            Rating rating = restTemplate.getForObject("http://localhost:8083/ratingdata/" + rat.getMovieId(), Rating.class);
        //an async call using WebClient new way of doing
        Rating rating = webClientBuilder.build()
                .get()//use get method (post, update...)
                .uri("http://localhost:8083/ratingdata/" + rat.getMovieId())//not using Eureka
                .retrieve()//get the data
                .bodyToMono(Rating.class)//its an async call
                .block();//waite until data will be fulfiled an async call, WO it it was a sync call
//            restTemplate.getForObject("http://localhost:8083/ratingdata/" + rat.getMovieId(), Rating.class);
        restTemplate.getForObject("http://rating/ratingdata/" + rat.getMovieId(), Rating.class);


        return new CatalogItem(movie.getName(), "test description: " + movie.getName() + " " + rating.getRating(), rating.getRating());
    }

    private CatalogItem getFallbackCatalogItem(Rating rat) {
        return new CatalogItem("Movie not found", "test description: " + rat.getMovieId() + " " + rat.getRating(), rat.getRating());
    }

    @HystrixCommand(fallbackMethod = "getFallbackUserRating",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000")
            },threadPoolKey = "UserRating",
    threadPoolProperties = {
            @HystrixProperty(name = "coreSize", value = "20"),
            @HystrixProperty(name = "maxQueueSize", value = "10")
    })
    private UserRating getUserRating(@PathVariable String userId) {
        return restTemplate.getForObject("http://rating/ratingdata/users/" + userId, UserRating.class);
    }

    private UserRating getFallbackUserRating(@PathVariable String userId) {
        UserRating userRating = new UserRating();
        userRating.setUserRatings(Arrays.asList( new Rating("0", 0)));
        return userRating;
    }


        public List<CatalogItem> getFallbackCatalog(@PathVariable String userId){
        return Arrays.asList(new CatalogItem("no Movie","",0));
    }

}
