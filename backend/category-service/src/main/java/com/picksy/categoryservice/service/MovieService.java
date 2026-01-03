package com.picksy.categoryservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.picksy.categoryservice.model.Movie;
import com.picksy.categoryservice.model.MovieGenre;
import com.picksy.categoryservice.request.CategoryBody;
import com.picksy.categoryservice.request.OptionBody;
import com.picksy.categoryservice.response.CategoryDTO;
import com.picksy.categoryservice.response.OptionDTO;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MovieService {

  private final ObjectMapper objectMapper;
  private final CategoryService categoryService;
  private final OptionService optionService;
  private final String TMDB_POSTER_BASE_URL = "https://image.tmdb.org/t/p/original/";

  public void saveMoviesAsCategories() throws IOException {
    System.out.println("Saving movies as categories");
    // If there are builtin categories
    if (!categoryService
        .findBuiltInCategories(PageRequest.of(0, 1, Sort.by("id").ascending()))
        .isEmpty()) {
      return;
    }

    List<MovieGenre> genres = getGenres();

    for (MovieGenre genre : genres) {
      List<Movie> movies = getMoviesForGenre(genre.id());

      // Add category
      String title = String.format("Filmy %s", genre.name());
      String description =
          String.format(
              "Zestaw %d filmów %s sortowanych od najpopularniejszych",
              movies.size(), genre.name());

      CategoryBody categoryBody = new CategoryBody(title, "SWIPE", description, true, null);
      CategoryDTO categoryDTO = categoryService.create((long)-1, categoryBody);

      // Add category image as first movie poster
      categoryService.addImageUrl(
          categoryDTO.id(), TMDB_POSTER_BASE_URL + movies.getFirst().poster());

      // Save movies as options
      for (Movie movie : movies) {
        String movieReleaseDate =
            movie.releaseDate().indexOf('-') != -1
                ? movie.releaseDate().substring(0, movie.releaseDate().indexOf('-'))
                : "";
        String movieTitle = String.format("%s(%s)", movie.title(), movieReleaseDate);

        OptionBody optionBody = new OptionBody(categoryDTO.id(), movieTitle);
        OptionDTO optionDTO = optionService.add((long) -1, "ADMIN", optionBody);

        optionService.addImageUrl(optionDTO.id(), TMDB_POSTER_BASE_URL + movie.poster());
      }
    }
  }

  private List<Movie> getMoviesForGenre(int genre) throws IOException {
    OkHttpClient client = new OkHttpClient();

    List<Movie> movies = new ArrayList<>();

    Integer maxPages = 25;

    for (int page = 1; page <= 25; page++) {
      Request request =
          new Request.Builder()
              .url(
                  String.format(
                      "https://api.themoviedb.org/3/discover/movie?include_adult=false&include_video=false&language=pl-PL&page=%d&sort_by=popularity.desc&with_genres=%d",
                      page, genre))
              .get()
              .addHeader("accept", "application/json")
              .addHeader(
                  "Authorization",
                  "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJhZmQ0N2NiMWM4ODA4MWJhMDc3ZmY4MjBmYjAzYWFjNyIsIm5iZiI6MTc0MjQxMzc5MC42NjYsInN1YiI6IjY3ZGIxZmRlOWYzNThmNGExMzdmN2U3OSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.cl_U1DUSnVpSCRSC1hBP7qL9uIkgYYgbP1i95yZMDW8")
              .build();

      Response response = client.newCall(request).execute();

      JsonNode jsonNode = objectMapper.readTree(response.body().string());

      movies.addAll(
          objectMapper.readValue(
              jsonNode.get("results").toString(), new TypeReference<List<Movie>>() {}));

      if (maxPages > jsonNode.get("total_pages").asInt()) break;
    }

    return movies;
  }

  private List<MovieGenre> getGenres() throws IOException {
    List<Integer> genres = new ArrayList<>();

    OkHttpClient client = new OkHttpClient();

    Request request =
        new Request.Builder()
            .url("https://api.themoviedb.org/3/genre/movie/list?language=pl")
            .get()
            .addHeader("accept", "application/json")
            .addHeader(
                "Authorization",
                "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJhZmQ0N2NiMWM4ODA4MWJhMDc3ZmY4MjBmYjAzYWFjNyIsIm5iZiI6MTc0MjQxMzc5MC42NjYsInN1YiI6IjY3ZGIxZmRlOWYzNThmNGExMzdmN2U3OSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.cl_U1DUSnVpSCRSC1hBP7qL9uIkgYYgbP1i95yZMDW8")
            .build();

    Response response = client.newCall(request).execute();

    JsonNode jsonNode = objectMapper.readTree(response.body().string());

    return objectMapper.readValue(
        jsonNode.get("genres").toString(), new TypeReference<List<MovieGenre>>() {});
  }
}
