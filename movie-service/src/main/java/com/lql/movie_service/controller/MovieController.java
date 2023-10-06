package com.lql.movie_service.controller;


import com.lql.movie_service.model.Movie;
import com.lql.movie_service.model.Review;
import com.lql.movie_service.serivce.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
@Slf4j
public class MovieController {
    private final MovieService movieService;

    @GetMapping("/demo")
    @PreAuthorize("hasAuthority('WRITE')")
    public Mono<Authentication> demo(Authentication authentication) {
        return Mono.just(authentication);
    }


    @PostMapping
    Mono<Movie> createMovie(@RequestPart FilePart movieImage,
                            @RequestPart FilePart movieVideo,
                            @RequestPart String movieName,
                            Authentication authentication) {
        return movieService.createMovie(movieImage, movieVideo, movieName, authentication.getCredentials().toString());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('READ')")
    Flux<Movie> getMovies(@RequestParam(required = false, defaultValue = "0") int pageNo,
                          @RequestParam(required = false, defaultValue = "10") int pageSize) {
        return movieService.getAll(pageNo, pageSize);
    }
    @GetMapping("/search/{movieName}")
    Flux<Movie> getMoviesByName(@PathVariable String movieName) {
        return movieService.getByMovieName(movieName);
    }

    @DeleteMapping("/{movieId}")
    public Mono<String> deleteMovieById(@PathVariable String movieId, Authentication authentication) {

        return movieService.deleteById(movieId, authentication);
    }

    @GetMapping("/image")
    Mono<ResponseEntity<byte[]>> getImageById2(@RequestParam String id) {
        return movieService
                .findById(id)
                .map(movie ->
                        ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(movie.getImages().getData()));
    }

    @PostMapping("/comment/{movieId}")
    Flux<Review> addReview2(@PathVariable String movieId, @RequestBody Mono<Review> review) {
        return movieService.addReview(movieId, review);
    }

    @GetMapping("/likes/{movieId}")
    public Mono<Set<String>> addLikes(Authentication authentication, @PathVariable String movieId) {
        return movieService.findById(movieId).flatMap(movie -> {
            movie.getLikes().add(authentication.getCredentials().toString());
            return movieService.save(movie).map(Movie::getLikes);
        });
    }

}
