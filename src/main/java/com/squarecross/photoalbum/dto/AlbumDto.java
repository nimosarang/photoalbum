package com.squarecross.photoalbum.dto;

import com.squarecross.photoalbum.domain.Photo;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class AlbumDto {

    private Long albumId;

    private String albumName;

    private Date createdAt;

    private int count;

}
