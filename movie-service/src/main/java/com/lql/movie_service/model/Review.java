package com.lql.movie_service.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Review {

    private String userId;
    private String username;
    private String reviewContent;

}
