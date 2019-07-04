package io.javabrains.MovieCatalogService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable String userId){

        // get all rated movie ids
        List<Rating> raings = Arrays.asList(
                new Rating("1234", 4),
                new Rating("12", 5),
                new Rating("14", 1));

        return raings.stream().map(rat -> {
            Movie movie = restTemplate.getForObject("http://localhost:8081/movies/" + rat.getMovieId(), Movie.class);
            Rating rating = restTemplate.getForObject("http://localhost:8083/ratingdata/" + rat.getMovieId(), Rating.class);
            return new CatalogItem(movie.getName(), "test", rating.getRating());
        }).collect(Collectors.toList());
        //for each movie id call movie info service and get details


        //put them all together
//        return Collections.singletonList(
//                new CatalogItem("Transformers", "test", 4)
//        );
    }
}
