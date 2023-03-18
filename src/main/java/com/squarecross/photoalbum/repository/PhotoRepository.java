package com.squarecross.photoalbum.repository;

import com.squarecross.photoalbum.domain.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<Photo,Long> {
    int countByAlbum_AlbumId(Long albumId);//특정 앨범에 속해있는 사진의 개수를 가져옵니다.
}



//두 메서드는 둘 다 같은 결과를 반환하지만, 다른 방식으로 동작합니다.
//
//countByAlbum_AlbumId(Long albumId) 메서드는 Spring Data JPA의 Query Creation 기능을 사용하여 Album 엔티티의 id 속성을 참조하는 Album 객체의 id 값을 이용하여 Photo 엔티티를 찾아 개수를 반환합니다. 이 메서드는 Album 엔티티를 참조하므로 Album 엔티티가 Photo 엔티티와 연관된 엔티티인 경우에만 작동합니다.
//
//countByAlbumId(Long albumId) 메서드는 countByAlbumId 쿼리 메서드를 정의합니다. 이는 "AlbumId" 필드를 기반으로 Photo 엔티티의 개수를 반환합니다. 이는 Album 엔티티를 참조하지 않으므로 Album 엔티티가 없거나 연관된 엔티티가 아닌 경우에도 작동합니다.
//
//따라서, 만약 Album 엔티티가 Photo 엔티티와 연관된 엔티티이고 Album 엔티티의 id를 사용하여 Photo 개수를 찾는다면 1번 방식을, Album 엔티티와 연관이 없다면 2번 방식을 사용할 수 있습니다.

//간단히 말씀드리면,
//
//countByAlbum_AlbumId: Album과 Photo간의 관계를 참조하여 Album의 ID에 해당하는 Photo 개수를 반환합니다.
//countByAlbumId: Album과 Photo간의 관계를 고려하지 않고, Photo 테이블에서 albumId가 매개변수 값과 일치하는 Photo 개수를 반환합니다.
//즉, countByAlbum_AlbumId는 Album 엔티티에 대한 연관 관계를 활용하여 Photo 테이블에서 Album에 속한 Photo 개수를 세는 것이고, countByAlbumId는 Album과 관련없이 단순히 Photo 테이블에서 albumId에 일치하는 Photo 개수를 세는 것입니다. 따라서 상황에 따라 어떤 메소드를 사용할지 결정하면 됩니다.
