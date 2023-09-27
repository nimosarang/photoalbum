package com.squarecross.photoalbum.dto;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumDto {

    private Long albumId;
    private String albumName;
    private Date createdAt;
    private int count;                  // 앨범에 속한 사진의 수를 나타내는 역할
    private List<String> thumbUrls;     // 앨범 목록 위함

}
