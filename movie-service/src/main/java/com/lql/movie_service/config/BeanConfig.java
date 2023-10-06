package com.lql.movie_service.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class BeanConfig {
    @Bean
    public AmazonS3 s3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(
                "AKIA6K6BWIE3JUMAWS4Q",
                "JBK2p7+gzboyUgEJdLbl7rLS+bxn+/b+HhL4U+dk"
        );
        return AmazonS3Client.builder()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.AP_SOUTHEAST_1)
                .build();
    }
//
//    @Bean
//    public CommandLineRunner runner(MovieService movieService) {
//        return args -> {
//            Movie movie = new Movie(null, "1234", "abc", null, null, null, null, null);
//
//            movieService.save(movie).doOnSuccess(mv -> log.info("movie saved successfully {}", mv)).subscribe();
//        };
//    }
}
