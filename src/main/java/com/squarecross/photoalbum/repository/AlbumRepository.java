package com.squarecross.photoalbum.repository;

import com.squarecross.photoalbum.domain.Album;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    Optional<Album> findByAlbumName(String name);
}
