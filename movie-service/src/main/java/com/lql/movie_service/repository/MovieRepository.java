package com.lql.movie_service.repository;

import com.lql.movie_service.model.Movie;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface MovieRepository extends ReactiveMongoRepository<Movie, String> {

    Flux<Movie> findAllByIdNotNull(Pageable pageable);
    Flux<Movie> findAllByNameContains(String name);
}
