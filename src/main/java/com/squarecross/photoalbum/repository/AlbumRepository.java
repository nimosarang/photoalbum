package com.squarecross.photoalbum.repository;

import com.squarecross.photoalbum.domain.Album;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    Optional<Album> findByAlbumName(String name);

    //앨범명검색 + 앨범명 A-Z 정렬 :
    List<Album> findByAlbumNameContainingOrderByCreatedAtDesc(String keyword);

    //앨범명검색 + 생성날짜 최신순 :
    List<Album> findByAlbumNameContainingOrderByAlbumNameAsc(String keyword);


}
