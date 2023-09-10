package com.squarecross.photoalbum.controller;

import com.squarecross.photoalbum.dto.PhotoDto;
import com.squarecross.photoalbum.service.PhotoService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

//입출력 관련 : 1. 입출력 정보 확인  2.Controller 틀 만들기
//서비스로직 관련 :
//1. 의사코드 작성
//2. Service 외 Dto, Mapper 등에 추가로 구현해야하는 사항들이 있는지 체크 및 구현
//3. Service 구현
//4. 테스트케이스 작성
//5. 테스팅 진행
//6. Controller 마무리

@RestController
@RequestMapping("/albums/{albumId}/photos")
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    @GetMapping("/photos")
    public ResponseEntity<List<PhotoDto>> getAllPhotos() {
        List<PhotoDto> photoDtos = photoService.getAllPhotos();
        return new ResponseEntity<>(photoDtos, HttpStatus.OK);
    }

    //사진 상세정보 API
    @GetMapping("/{photoId}")
    public ResponseEntity<PhotoDto> getPhotoInfo(@PathVariable("photoId") final Long photoId) {
        PhotoDto photoDto = photoService.getPhoto(photoId);
        return new ResponseEntity<>(photoDto, HttpStatus.OK);
    }

    //사진 업로드 API
    @PostMapping
    public ResponseEntity<List<PhotoDto>> uploadPhotos( //업로드된 사진이 포함된 앨범 내 사진 리스트를 반환합니다
        @PathVariable("albumId") final Long albumId, @RequestParam("photos") MultipartFile[] files
    ) { //MultiPartFile 은 API 호출을 통해서 들어온 파일을 받을 수 있는 클래스입니다. photos 입력 필드로 업로드된 파일이 files 배열 객체 안에 저장되게 됩니다.

        List<PhotoDto> photos = new ArrayList<>();
        for (MultipartFile file : files){
            PhotoDto photoDto = photoService.savePhoto(file, albumId);
            photos.add(photoDto);
        }
        return new ResponseEntity<>(photos,HttpStatus.OK);
    }

    //사진 다운로드 API
    @GetMapping("/download")
    public void downloadPhotos(@RequestParam("photoIds") Long[] photoIds, HttpServletResponse response){
        //photoIds Query Parameter 로 다운받을 사진 Id를 받습니다
        //HttpServletResponse 는 Client 에서 API 호출 시 생성되는 Response Servlet 이다.
        //평상시에는 ResponseEntity 를 사용하지만 파일을 전달하는 통로를 열려면 ResponseServlet 을 직접 사용하여 파일을 전달하는 것이 용이하다.
        try {
            if (photoIds.length == 1){ //이미지가 한장있을 경우 압축하지 않고 이미지파일 내보내기
                File file = photoService.getImageFile(photoIds[0]);
                //Servlet Response 의 Outputstream 을 연다 (파일 Byte 를 흘려보낼 수 있는 통로).
                OutputStream outputStream = response.getOutputStream();
                //출력된 이미지 파일의 InputStream 을 열어서 OutputStream 에 흘려보내준다
                IOUtils.copy(new FileInputStream(file), outputStream);
                //닫아준다
                outputStream.close();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
