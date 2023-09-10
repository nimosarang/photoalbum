package com.squarecross.photoalbum.service;

import static org.junit.jupiter.api.Assertions.*;

import com.squarecross.photoalbum.domain.Album;
import com.squarecross.photoalbum.domain.Photo;
import com.squarecross.photoalbum.dto.AlbumDto;
import com.squarecross.photoalbum.mapper.AlbumMapper;
import com.squarecross.photoalbum.repository.AlbumRepository;
import com.squarecross.photoalbum.repository.PhotoRepository;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
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

    @Test
    void testAlbumRepository() throws InterruptedException {
        Album album1 = new Album();
        Album album2 = new Album();
        album1.setAlbumName("aaaa");
        album2.setAlbumName("aaab");

        albumRepository.save(album1);
        TimeUnit.SECONDS.sleep(1); //시간차를 벌리기위해 두번째 앨범 생성 1초 딜레이
        albumRepository.save(album2);

        //최신순 정렬, 두번째로 생성한 앨범이 먼저 나와야합니다
        List<Album> resDate = albumRepository.findByAlbumNameContainingOrderByCreatedAtDesc("aaa");
        assertEquals("aaab", resDate.get(0).getAlbumName()); // 0번째 Index가 두번째 앨범명 aaab 인지 체크
        assertEquals("aaaa", resDate.get(1).getAlbumName()); // 1번째 Index가 첫번째 앨범명 aaaa 인지 체크
        assertEquals(2, resDate.size()); // aaa 이름을 가진 다른 앨범이 없다는 가정하에, 검색 키워드에 해당하는 앨범 필터링 체크

        //앨범명 정렬, aaaa -> aaab 기준으로 나와야합니다
        List<Album> resName = albumRepository.findByAlbumNameContainingOrderByAlbumNameAsc("aaa");
        assertEquals("aaaa", resName.get(0).getAlbumName()); // 0번째 Index가 두번째 앨범명 aaaa 인지 체크
        assertEquals("aaab", resName.get(1).getAlbumName()); // 1번째 Index가 두번째 앨범명 aaab 인지 체크
        assertEquals(2, resName.size()); // aaa 이름을 가진 다른 앨범이 없다는 가정하에, 검색 키워드에 해당하는 앨범 필터링 체크
    }

    @Test
    void testChangeAlbumName() {
        //앨범 생성
        AlbumDto albumDto = new AlbumDto();
        albumDto.setAlbumName("변경 전");
        AlbumDto res = albumService.createAlbum(albumDto);

        Long albumId = res.getAlbumId();  //생성된 앨범 아이디 추출
        AlbumDto updateDto = new AlbumDto();
        updateDto.setAlbumName("변경 후"); //업데이트용 DTO 생성
        albumService.changeName(albumId,updateDto);

        AlbumDto updateCompleteDto = albumService.getAlbum(albumId);

        //앨범명이 변경되었는지 확인
        assertEquals("변경 후",updateCompleteDto.getAlbumName());
    }

    @Test
    void testDeleteAlbum() {
        //앨범 생성
        AlbumDto albumDto = new AlbumDto();
        albumDto.setAlbumName("삭제 테스트 앨범");
        AlbumDto res = albumService.createAlbum(albumDto);

        Long albumId = res.getAlbumId();  //생성된 앨범 아이디 추출
        albumService.deleteAlbum(albumId);

        //앨범이 삭제되었는지 확인
        Optional<Album> deletedAlbum = albumRepository.findById(albumId);
        assertFalse(deletedAlbum.isPresent());
        //만약, deletedAlbum.isPresent()가 true 를 반환한다면,
        //해당 앨범이 삭제되지 않았다는 것을 의미하므로 테스트가 실패합니다.

    }








}