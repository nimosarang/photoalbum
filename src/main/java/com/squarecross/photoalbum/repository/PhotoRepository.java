package com.squarecross.photoalbum.repository;

import com.squarecross.photoalbum.domain.Photo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<Photo,Long> {

    //특정 앨범에 속해있는 사진의 개수를 가져옴
    int countByAlbum_AlbumId(Long albumId);

    //각 앨범마다 앨범아이디별로 최신 4장 이미지를 가져오는 Repository 메서드를 구현
    List<Photo> findTop4ByAlbum_AlbumIdOrderByUploadedAtDesc(Long albumId);

    //파일명 존재 유무 체크
    //1. 같은 파일명이 존재하는지 체크하는 Repository 메서드 추가
    //- 입력된 앨범 안에 같은 파일명이 있는지 확인.
    //- 다른 앨범에는 같은 파일명이 있어도 된다.
    Optional<Photo> findByFileNameAndAlbum_AlbumId(String photoName, Long albumId);

}

