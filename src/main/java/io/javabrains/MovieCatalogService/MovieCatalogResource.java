package io.javabrains.MovieCatalogService;

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

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable String userId){

        RestTemplate restTemplate = new RestTemplate();
        // get all rated movie ids
        List<Rating> raings = Arrays.asList(
                new Rating("1234", 4),
                new Rating("12", 5),
                new Rating("14", 1));

        return raings.stream().map(rating -> {
            Movie movie = restTemplate.getForObject("http://localhost:8081/movies/56" + rating.getMovieId(), Movie.class);
            return new CatalogItem(movie.getName(), "test", 4);
        }).collect(Collectors.toList());
        //for each movie id call movie info service and get details


        //put them all together
//        return Collections.singletonList(
//                new CatalogItem("Transformers", "test", 4)
//        );
    }
}
