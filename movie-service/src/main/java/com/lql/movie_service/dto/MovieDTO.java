package com.lql.movie_service.dto;

import java.util.Date;

public record MovieDTO(String movieKey, String movieUrl, Date expires) {
}
