package com.squarecross.photoalbum.service;

import com.squarecross.photoalbum.domain.Album;
import com.squarecross.photoalbum.dto.AlbumDto;
import com.squarecross.photoalbum.mapper.AlbumMapper;
import com.squarecross.photoalbum.repository.AlbumRepository;
import com.squarecross.photoalbum.repository.PhotoRepository;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlbumService {

    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private PhotoRepository photoRepository;

    //Album 정보 조회하기 메서드 만들기
//    public AlbumDto getAlbum(Long albumId) {
//        Optional<Album> repository = albumRepository.findById(albumId);
//        if (repository.isPresent()) {
//            return repository.get();
//        } else {
//            throw new EntityNotFoundException(String.format("앨범 아이디 %d로 조회되지 않습니다.", albumId));
//        }
//    }

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

    public AlbumDto getAlbum(String albumName){
        Optional<Album> repository = albumRepository.findByAlbumName(albumName);
        if (repository.isPresent()){
            AlbumDto albumDto = AlbumMapper.convertToDto(repository.get());
            albumDto.setCount(photoRepository.countByAlbum_AlbumId(albumDto.getAlbumId()));
            return albumDto;
        }else {
            throw new EntityNotFoundException(String.format("앨범 이름으로 %d로 조회되지 않습니다.", albumName));
        }
    }



}
