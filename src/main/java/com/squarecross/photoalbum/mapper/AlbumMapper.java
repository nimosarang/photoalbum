package com.squarecross.photoalbum.mapper;

import com.squarecross.photoalbum.domain.Album;
import com.squarecross.photoalbum.dto.AlbumDto;
import java.util.List;
import java.util.stream.Collectors;

public class AlbumMapper {

    public static AlbumDto convertToDto(Album album) {
        AlbumDto albumDto = new AlbumDto();
        albumDto.setAlbumId(album.getAlbumId());
        albumDto.setAlbumName(album.getAlbumName());
        albumDto.setCreatedAt(album.getCreatedAt());
        return albumDto;
    }

    public static List<AlbumDto> convertToDtoList(List<Album> albums) {
        //리스트안에 있는 것들을 스트리밍하듯이 하나씩 하나씩 흘려보내듯이 처리합니다.
        //albums 에 있는 각 앨범을 하나씩 하나씩 AlbumMapper.convertToDto 로 변화시킨 이후 리스트형태로 다시 모읍니다 collect(Collectors.toList()).
        return albums.stream().map(AlbumMapper::convertToDto).collect(Collectors.toList());
    }


    public static Album convertToDomain(AlbumDto albumDto) {
        Album album = new Album();
        album.setAlbumId(albumDto.getAlbumId());
        album.setAlbumName(albumDto.getAlbumName());
        album.setCreatedAt(albumDto.getCreatedAt());
        return album;
    }


}
