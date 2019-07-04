package io.javabrains.MovieCatalogService;

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
    public List<CatalogItem> getCatalog(@PathVariable String userId){

        WebClient.Builder builder = WebClient.builder();
        UserRating userRating = restTemplate.getForObject("http://localhost:8083/ratingdata/users/" + userId, UserRating.class);
        // get all rated movie ids

        return userRating.getUserRatings().stream().map(rat -> {
            //a sync call  using RestTemplate an old way of doing
            Movie movie = restTemplate.getForObject("http://localhost:8081/movies/" + rat.getMovieId(), Movie.class);

//            Rating rating = restTemplate.getForObject("http://localhost:8083/ratingdata/" + rat.getMovieId(), Rating.class);
            //an async call using WebClient new way of doing
            Rating rating = webClientBuilder.build()
                    .get()//use get method (post, update...)
                    .uri("http://localhost:8083/ratingdata/" + rat.getMovieId())
                    .retrieve()//get the data
                    .bodyToMono(Rating.class)//its an async call
                    .block();//waite until data will be fulfiled an async call, WO it it was a sync call
                    restTemplate.getForObject("http://localhost:8083/ratingdata/" + rat.getMovieId(), Rating.class);


            return new CatalogItem(movie.getName(), "test description: " + movie.getName() + " " + rating.getRating(), rating.getRating());
        }).collect(Collectors.toList());
        //for each movie id call movie info service and get details


        //put them all together
//        return Collections.singletonList(
//                new CatalogItem("Transformers", "test", 4)
//        );
    }
}
