package com.squarecross.photoalbum.service;

import com.squarecross.photoalbum.common.Constants;
import com.squarecross.photoalbum.domain.Album;
import com.squarecross.photoalbum.domain.Photo;
import com.squarecross.photoalbum.dto.AlbumDto;
import com.squarecross.photoalbum.mapper.AlbumMapper;
import com.squarecross.photoalbum.repository.AlbumRepository;
import com.squarecross.photoalbum.repository.PhotoRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final PhotoRepository photoRepository;

    public AlbumDto getAlbum(Long albumId) {
        Optional<Album> repository = albumRepository.findById(albumId);
        if (repository.isPresent()) {
            AlbumDto albumDto = AlbumMapper.convertToDto(repository.get());
            albumDto.setCount(photoRepository.countByAlbum_AlbumId(albumId));
            return albumDto;
        } else {
            throw new EntityNotFoundException(String.format("앨범 아이디 %d로 조회되지 않습니다.", albumId));
        }
    }

    public AlbumDto getAlbum(String albumName) {
        Optional<Album> repository = albumRepository.findByAlbumName(albumName);
        if (repository.isPresent()) {
            AlbumDto albumDto = AlbumMapper.convertToDto(repository.get());
            albumDto.setCount(photoRepository.countByAlbum_AlbumId(albumDto.getAlbumId()));
            return albumDto;
        } else {
            throw new EntityNotFoundException(String.format("앨범 이름으로 %d로 조회되지 않습니다.", albumName));
        }
    }

    //todo 0. 앨범 생성하기 API
    //todo 1. AlbumController 에서 AlbumDto 를 입력받음
    //todo 2. 디렉토리 생성에 성공했으면 입력받은 AlbumDto 객체를 Album 객체로 반환
    //  : Album Mapper DTO → Domain 변환 로직 구현하기
    //todo 3. 생성된 Album 객체를 데이터베이스에 저장 (albumId 생성)
    //todo 4. photos/original, photos/thumbnail 디렉토리 내 신규 앨범 디렉토리 생성.
    //todo 5. 생성된 앨범 정보 DTO 로 변환하여 Controller 에 출력

    public AlbumDto createAlbum(AlbumDto albumDto) {
        //Mapper 사용해서 DTO - Domain 변환 → save() 로 DB에 레코드 생성
        Album album = AlbumMapper.convertToDomain(albumDto);
        albumRepository.save(album);
        //앨범 디렉토리 생성하기
        try {
            createAlbumDirectories(album);
        } catch (IOException e) {
            throw new RuntimeException("앨범 디렉토리 생성 중 오류가 발생했습니다.");
        }
        //마지막으로 Dto 로 변환해서 출력
        return AlbumMapper.convertToDto(album);
    }

    private void createAlbumDirectories(Album album) throws IOException {
        //앨범 ID를 사용해서 폴더를 생성
        Files.createDirectories(
            Paths.get(Constants.PATH_PREFIX + "/photos/original/" + album.getAlbumId()));
        Files.createDirectories(
            Paths.get(Constants.PATH_PREFIX + "/photos/thumb/" + album.getAlbumId()));
    }

    //todo 0. 앨범 목록 불러오기 API
    //  1. 날짜 기준 정렬, 앨범명 기준 정렬로 나누어서 별도로 repository 호출해서 앨범 도메인 리스트를 반환
    //      a. AlbumRepository 에 사용자 정의 메서드 추가 필요
    //  2. 앨범 도메인 리스트를 DTO 리스트로 매핑한다
    //  3. 각 앨범마다 가장 최신 업로드된 이미지 4장 섬네일 Url DTO 에 필드값 입력한다
    //  4. 앨범 DTO 리스트 반환한다

    public List<AlbumDto> getAlbumList(String keyword, String sort) {
        List<Album> albums;
        if (Objects.equals(sort, "byName")) {
            albums = albumRepository.findByAlbumNameContainingOrderByAlbumNameAsc(keyword);
        } else if (Objects.equals(sort, "byDate")) {
            albums = albumRepository.findByAlbumNameContainingOrderByCreatedAtDesc(keyword);
        } else {
            throw new IllegalArgumentException("알 수 없는 정렬 기준입니다.");
        }
        List<AlbumDto> albumDtos = AlbumMapper.convertToDtoList(albums);// Dto 리스트로 변환

        for (AlbumDto albumDto : albumDtos) {
            List<Photo> top4 = photoRepository.findTop4ByAlbum_AlbumIdOrderByUploadedAtDesc(
                albumDto.getAlbumId());
            albumDto.setThumbUrls(
                top4.stream().map(Photo::getThumbUrl) //map(Photo::getThumbUrl) 썸네일 URL 추출
                    .map(c -> Constants.PATH_PREFIX + c).collect(Collectors.toList()));
            //map(c -> Constants.PATH_PREFIX + c) 프로젝트 폴더 디렉토리까지 합쳐서 Full 이미지 Path 로 만들기
            //map 안에 있는 내용은 람다 함수인데, c는 stream 에서 넘어오는 각 string 이고 → 우측에 있는 구현내용은 c를 사용해서 실행할 내용임
            //collect(Collectors.toList()) 하나씩 수정을 거쳐서 들어오는 String 을 List 로 모은다
        }
        return albumDtos;
    }

    //todo 0. 앨범명 변경 API
    //  1.입력된 albumId가 존재하는지 체크
    //      a. 없으면 NoSuchElementException 던지기
    //  2.입력받은 albumId로 DB 조회 → Domain 객체 받기
    //  3.받은 Domain 객체에서 Setter 로 수정된 앨범명으로 변경해주기
    //  4.업데이트된 앨범 Domain 객체를 저장하기
    //  5.업데이트된 앨범 DTO 출력하기
    //  데이터를 수정할 때 DB에서 조회, 수정한 후, 신규 객체가 아닌 수정된 객체로 저장

    public AlbumDto changeName(Long albumId, AlbumDto albumDto) {
        Optional<Album> album = albumRepository.findById(albumId);
        if (album.isEmpty()) {
            throw new NoSuchElementException(String.format("Album ID '%d'가 존재하지 않습니다", albumId));
        }
        Album updateAlbum = album.get();
        updateAlbum.setAlbumName(albumDto.getAlbumName());
        Album savedAlbum = albumRepository.save(updateAlbum);
        return AlbumMapper.convertToDto(savedAlbum);
    }

    public void deleteAlbum(Long albumId) {
        Album album = albumRepository.findById(albumId).orElseThrow(IllegalArgumentException::new);
        albumRepository.delete(album);
    }

}
