package com.squarecross.photoalbum.controller;

import com.squarecross.photoalbum.dto.AlbumDto;
import com.squarecross.photoalbum.service.AlbumService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/albums")
public class AlbumController {

    @Autowired
    AlbumService albumService;

    @GetMapping("/{albumId}")
    //특정 앨범을 타게팅 해서 정보를 조회하기 때문에 REST API 규칙에 따라 Path Variable 로 앨범 아이디를 입력합니다.
    public ResponseEntity<AlbumDto> getAlbum(@PathVariable("albumId") final long albumId) {
        AlbumDto album = albumService.getAlbum(albumId);
        return new ResponseEntity<>(album, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<AlbumDto> createAlbum(@RequestBody final AlbumDto albumDto) {
        AlbumDto savedAlbumDto = albumService.createAlbum(albumDto);
        return new ResponseEntity<>(savedAlbumDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<AlbumDto>> getAlbumList(
        @RequestParam(value = "keyword", defaultValue = "") String keyword,
        @RequestParam(value = "sort", defaultValue = "byDate") String sort) {
        //required=false 는 필수 값이 아니라는 의미. // defaultValue= 는 Client 에서 입력값이 없을 때 설정되는 값을 의미합니다.
        List<AlbumDto> albumDtos = albumService.getAlbumList(keyword, sort);
        return new ResponseEntity<>(albumDtos, HttpStatus.OK);
    }

    @PutMapping("/{albumId}") //앨범명 변경 API
    public ResponseEntity<AlbumDto> updateAlbum(@PathVariable("albumId") final long albumId,
        @RequestBody final AlbumDto albumDto) { //업데이트된 앨범정보가 담긴 AlbumDto 오브젝트를 반환합니다
        AlbumDto res = albumService.changeName(albumId, albumDto);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("/{albumId}") //앨범 삭제
    public void deleteAlbum(@PathVariable("albumId") final long albumId) {
        albumService.deleteAlbum(albumId);
    }


}

//    @GetMapping("/query")
//    public ResponseEntity<AlbumDto> getAlbumByQuery(long albumId){
//        AlbumDto album = albumService.getAlbum(albumId);
//        return new ResponseEntity<>(album, HttpStatus.OK);
//    }
//
//    @PostMapping("/search")
//    public ResponseEntity<AlbumDto> getAlbumByJson(@RequestBody final AlbumDto albumDto){
//        AlbumDto album = albumService.getAlbum(albumDto.getAlbumId());
//        return new ResponseEntity<>(album, HttpStatus.OK);
//    }
//    @GetMapping("/name/{albumName}")
//    public ResponseEntity<AlbumDto> getAlbumByName(@PathVariable("albumName") final String albumName) {
//        AlbumDto album = albumService.getAlbumByName(albumName);
//        return new ResponseEntity<>(album, HttpStatus.OK);
//    }
