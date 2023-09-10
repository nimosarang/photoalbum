package com.squarecross.photoalbum.dto;

import java.util.Date;
import lombok.Data;

@Data
public class PhotoDto {

    private Long photoId;

    private String fileName;
    private int fileSize;

    private String originalUrl;
    private String thumbUrl;

    private Date uploadedAt;

    private Long albumId;

    public PhotoDto() {
    }
}