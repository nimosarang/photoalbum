package com.squarecross.photoalbum.dto;

import com.squarecross.photoalbum.domain.Photo;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
public class AlbumDto {

    private Long albumId;

    private String albumName;

    private Date createdAt;

    private int count; //앨범에 속한 사진의 수를 나타내는 역할

    private List<String> thumbUrls; //앨범 목록 위함

    public AlbumDto() {
    }
}
