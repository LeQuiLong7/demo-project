package com.lql.movie_service.serivce;


import com.lql.movie_service.dto.MovieDTO;
import com.lql.movie_service.model.Movie;
import com.lql.movie_service.model.Review;
import com.lql.movie_service.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.Binary;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {

    private final MovieRepository movieRepository;
    private final S3Service s3Service;

    public Flux<Movie> getAll(int pageNo, int pageSize) {
        return movieRepository.findAllByIdNotNull( PageRequest.of(pageNo, pageSize));
    }


    public Mono<Movie> createMovie( FilePart movieImage, FilePart movieVideo, String movieName, String createdBy) {
        return s3Service.uploadFile(movieVideo).flatMap(videoInfo ->
                movieImage.content().map(DataBuffer::asInputStream)
                        .collectList()
                        .flatMap(inputStreams -> {
                            byte[] res = null;
                            try {
                                res = inputStreams.get(0).readAllBytes();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return movieRepository
                                    .save(new Movie(null, createdBy, movieName, res != null ? new Binary(res) : null,
                                            new HashSet<>(), new HashSet<>(), videoInfo, new ArrayList<>()));
                        })
        );
    }

    public Flux<Review> addReview( String movieId, Mono<Review> review) {

        return movieRepository.findById(movieId).flatMap(movie -> review.flatMap(rv -> {
             movie.getReviews().add(rv);
             return movieRepository.save(movie);
         })).flatMapIterable(Movie::getReviews);
    }


    public Flux<Movie> getByMovieName(String movieName) {
        return movieRepository.findAllByNameContains(movieName);
    }



    public Mono<Movie> save(Movie movie) {
        return movieRepository.save(movie);
    }
    public Mono<Movie> findById(String id) {
        System.out.println(id);
        return movieRepository.findById(id).doOnNext(System.err::println);
    }

    public Mono<String> deleteById(String movieId, Authentication authentication) {
            return movieRepository.findById(movieId)
                    .flatMap(movie -> {
                        if(authentication.getName().equals("admin") ||
                                movie.getCreatedBy().equals(authentication.getCredentials().toString())) {
                            s3Service.deleteMovie(movie.getVideoInfo().movieKey());
                            return movieRepository.deleteById(movieId).thenReturn("delete movie " + movieId + " successfully");
                        } else {
                            return Mono.error(RuntimeException::new);
                        }
                    }).onErrorReturn("delete movie " + movieId + " unsuccessfully");
    }


    @Scheduled(timeUnit = TimeUnit.DAYS, fixedDelay = 3)
    public void getNewUrlForAllMovies() {
        movieRepository.findAll()
                .flatMap(mv -> {
                    Mono<MovieDTO> newUrl = s3Service.getNewUrl(mv.getVideoInfo().movieKey());
                    return newUrl.flatMap(newInfo -> {
                        mv.setVideoInfo(newInfo);
                        return movieRepository.save(mv);
                    });
                }).doOnNext(movie -> log.info("Update new url for movie {}", movie.getId()))
                    .subscribe();
    }
}
