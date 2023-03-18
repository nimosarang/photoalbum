package com.squarecross.photoalbum.service;

import static org.junit.jupiter.api.Assertions.*;

import com.squarecross.photoalbum.domain.Album;
import com.squarecross.photoalbum.domain.Photo;
import com.squarecross.photoalbum.dto.AlbumDto;
import com.squarecross.photoalbum.mapper.AlbumMapper;
import com.squarecross.photoalbum.repository.AlbumRepository;
import com.squarecross.photoalbum.repository.PhotoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class AlbumServiceTest {

    @Autowired
    AlbumRepository albumRepository;
    @Autowired
    PhotoRepository photoRepository;
    @Autowired
    AlbumService albumService;

    @Test
    void getAlbum() {
        Album album = new Album(); //Mock 앨범 생성해서 DB 에 저장
        album.setAlbumName("테스트앨범");
        Album saveAlbum = albumRepository.save(album);

        //id
        AlbumDto resultAlbum = albumService.getAlbum(saveAlbum.getAlbumId());
        assertEquals("테스트앨범", resultAlbum.getAlbumName());

        //name
        resultAlbum = albumService.getAlbum(saveAlbum.getAlbumName());
        assertEquals("테스트앨범", resultAlbum.getAlbumName());

    }
    @Test
    void testPhotoCount(){
        //1.앨범 생성
        Album album = new Album(); //Mock 앨범 생성해서 DB 에 저장
        album.setAlbumName("테스트앨범for사진");
        Album saveAlbum = albumRepository.save(album);

        //2.사진을 생성하고, setAlbum 을 통해 앨범을 지정해준 이후, repository 에 사진을 저장한다
        Photo photo1 = new Photo();
        photo1.setFileName("사진1");
        photo1.setAlbum(saveAlbum);
        photoRepository.save(photo1);

        //3.앨범 정보 조회
        AlbumDto album1 = albumService.getAlbum(saveAlbum.getAlbumId());
        assertEquals("테스트앨범for사진",album1.getAlbumName());

        album1 = albumService.getAlbum(saveAlbum.getAlbumName());
        assertEquals("테스트앨범for사진",album1.getAlbumName());

        //4.추가된 사진 갯수만큼 있는지 확인
        //해당 앨범에 추가된 사진의 개수와 테스트에서 조회한 앨범의 사진 개수가 일치하는지를 검증할 수 있습니다.
        int actualPhotoCount = album1.getCount();
        assertEquals(1,actualPhotoCount);

    }




}