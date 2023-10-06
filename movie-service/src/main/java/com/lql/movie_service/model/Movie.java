package com.lql.movie_service.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lql.movie_service.dto.MovieDTO;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@With
public class Movie {
    @Id
    private String id;
    private String createdBy;
    private String name;
    private Binary images;
    private Set<String> likes;
    private Set<String> dislikes;
    private MovieDTO videoInfo;

    @JsonIgnore
    public Binary getImages() {
        return images;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "movie")
    private List<Review> reviews;
}
