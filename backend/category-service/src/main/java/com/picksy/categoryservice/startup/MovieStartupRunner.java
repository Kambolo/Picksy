package com.picksy.categoryservice.startup;

import com.picksy.categoryservice.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MovieStartupRunner implements ApplicationRunner {

    private final MovieService movieService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        movieService.saveMoviesAsCategories();
    }
}
