package com.lql.movie_service.serivce;

import com.amazonaws.services.s3.AmazonS3;
import com.lql.movie_service.dto.MovieDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    public static final String BUCKET_NAME = "my-s3-demo-app-bucket";
    private final AmazonS3 s3;

    public Mono<MovieDTO> uploadFile(FilePart file) {
        String fileExtension = StringUtils.getFilenameExtension(file.filename());
        String key = UUID.randomUUID().toString().concat(".").concat(fileExtension);
        File file1 = null;
        try{
            file1 = File.createTempFile("uploaded", file.filename());

        } catch (IOException e) {
            e.printStackTrace();
        }
        Date expiresDate = getNewExpiresDate();
        return file.transferTo(file1).thenReturn(file1).map(f -> {
            s3.putObject(BUCKET_NAME, key, f);
            return new MovieDTO(key, s3.generatePresignedUrl(BUCKET_NAME, key, expiresDate).toString(), expiresDate);
        });
    }

    public Mono<MovieDTO> getNewUrl(String key) {
        Date newExpiresDate = getNewExpiresDate();
        return Mono.fromCallable(() ->
                new MovieDTO(key, s3.generatePresignedUrl(BUCKET_NAME, key, newExpiresDate).toString(), newExpiresDate));
    }

    @Async
    public void deleteMovie(String key) {
        s3.deleteObject(BUCKET_NAME, key);
    }


    private Date getNewExpiresDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR,24 * 7);
        return  cal.getTime();
    }
}
