package io.javabrains.MovieCatalogService;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable String userId){

        // get all rated movie ids

        //for each movie id call movie info service and get details


        //put them all together
        return Collections.singletonList(
                new CatalogItem("Transformers", "test", 4)
        );
    }
}
