package com.lql.movie_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AddReviewRequest {
    private String movieId;
    private String username;
    private String userId;
    private String reviewContent;
}
