package com.squarecross.photoalbum.service;

import com.squarecross.photoalbum.Constants;
import com.squarecross.photoalbum.domain.Album;
import com.squarecross.photoalbum.domain.Photo;
import com.squarecross.photoalbum.dto.PhotoDto;
import com.squarecross.photoalbum.mapper.PhotoMapper;
import com.squarecross.photoalbum.repository.AlbumRepository;
import com.squarecross.photoalbum.repository.PhotoRepository;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import javax.imageio.ImageIO;
import javax.persistence.EntityNotFoundException;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PhotoService {

    @Autowired
    private PhotoRepository photoRepository;
    @Autowired
    private AlbumRepository albumRepository;

    //todo 이미지 저장 로직 추가
    private final String original_path = Constants.PATH_PREFIX + "/photos/original";
    private final String thumb_path = Constants.PATH_PREFIX + "/photos/thumb";


    //todo 사진 상세정보 가져오기 API
    public PhotoDto getPhoto(Long photoId) {
        Optional<Photo> repository = photoRepository.findById(photoId);
        if (repository.isPresent()) {
            PhotoDto photoDto = PhotoMapper.convertToDto(repository.get());
            return photoDto;
        } else {
            throw new EntityNotFoundException(String.format("사진 아이디 %d로 조회되지 않습니다.", photoId));
        }
    }

    //todo 사진 업로드 API
    //  서비스로직은 Controller 에서 for loo 를 돌려서 각 사진 파일 하나씩 처리하게 됩니다.
    //  1. 사진 이미지파일 기본정보 추출: 파일명, 파일 용량.
    //  2. 파일명이 이미 존재하는지 확인해서 존재하면 파일명에 숫자 추가하기.
    //    a. 예) photo.jpg 가 이미 있으면 photo1.jpg, photo2.jpg.. 등등 숫자가 계속 올라간다.
    //    b. DB에 입력된 앨범 내에 파일명이 이미 존재하는지 조회한다
    //        i. 있으면 숫자 + 1
    //        ii. 없을때까지 계속 진행
    //  3. 이미지파일 저장.
    //    a. Thumbnail 이미지파일 Original 에서 max 300x300으로 사이즈 축소.
    //    b. Original 이미지파일 `/photos/original/{albumId}/filename` 디렉토리 저장.
    //    c. Thumbnail 이미지파일 `/photos/thumb/{albumId}/filename` 디렉토리 저장.
    //  4. 파일 저장 성공 시 Database 에 사진 레코드 Insert.

    public PhotoDto savePhoto(MultipartFile file, Long albumId) {
        //todo 앨범아이디 체크 및 파일 기본정보 추출
        //1.앨범아이디가 존재하는지 확인
        Optional<Album> res = albumRepository.findById(albumId);
        if (res.isEmpty()) {
            throw new EntityNotFoundException("앨범이 존재하지 않습니다");
        }
        //2.파일명, 파일 용량 추출
        String fileName = file.getOriginalFilename();
        int fileSize = (int) file.getSize(); ////long 은 64바이트 int 는 32바이트다. int 로 나타낼 수 있는 최대는 대략 2GB 인데 그렇게 커질 일 없으니 int 로 변환
        fileName = getNextFileName(fileName, albumId); //체크 메서드 추가
        saveFile(file,albumId,fileName); //saveFile 메서드를 추가해서 사진을 저장합니다

        //DB에 사진 레코드 생성 & 생성된 앨범 DTO 반환
        Photo photo = new Photo();
        photo.setOriginalUrl("/photos/original/" + albumId + "/" + fileName);
        photo.setThumbUrl("/photos/thumb/" + albumId + "/" + fileName);
        photo.setFileName(fileName);
        photo.setFileSize(fileSize);
        photo.setAlbum(res.get());
        Photo createdPhoto = photoRepository.save(photo);
        return PhotoMapper.convertToDto(createdPhoto);

    }

    //파일 체크와 결과를 받을 수 있는 내용은 checkFileName 메서드에서 별도로 구현한다.
    public String getNextFileName(String fileName, Long albumId) {
        String fileNameNoExt = StringUtils.stripFilenameExtension(fileName);
        String ext = StringUtils.getFilenameExtension(fileName);

        Optional<Photo> res = photoRepository.findByFileNameAndAlbum_AlbumId(fileName, albumId);

        int count = 2;
        while (res.isPresent()) {
            fileName = String.format("%s (%d).%s", fileNameNoExt, count, ext);
            res = photoRepository.findByFileNameAndAlbum_AlbumId(fileName, albumId);
            count++;
        }

        return fileName;
    }

    //이미지 저장 기능, 원본 이미지를 original 사진 경로에 저장합니다.

    private void saveFile(MultipartFile file, Long AlbumId, String fileName) {
        try {
            //Scalr 라이브러리를 사용해서 오리지날 이미지를 최대 300x300으로 Resize 해줍니다
            //정사각형이 아닌 경우 가장 긴 면은 300으로 줄이고 다른 면은 비례해서 Resize 됩니다
            String filePath = AlbumId + "/" + fileName;
            Files.copy(file.getInputStream(), Paths.get(original_path + "/" + filePath));
            BufferedImage thumbImg = Scalr.resize(ImageIO.read(file.getInputStream()), Constants.THUMB_SIZE, Constants.THUMB_SIZE);

            //Resize 된 썸네일 이미지를 저장합니다.
            File thumbFile = new File(thumb_path + "/" + filePath);
            String ext = StringUtils.getFilenameExtension(fileName);
            if (ext == null) {
                throw new IllegalArgumentException("No Extention");
            }
            ImageIO.write(thumbImg, ext, thumbFile);
        } catch (IOException e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    //todo 사진 다운로드 API
    //  1. 입력된 이미지 아이디로 DB를 조회합니다.
    //    a. 없으면 Exception 을 던집니다
    //  2. 프로젝트 경로를 앞에 붙여서 디렉토리 내에서 사진을 불러옵니다.
    //  3. 파일을 Controller 로 출력합니다.
    //  서비스 메서드는 개별 이미지 아이디를 받아 이미지 파일을 반환합니다.
    //  서비스에서 파일을 집파일로 묶어서 Client 에 전달하는건 Controller 에서 진행합니다.

    public File getImageFile(Long photoId){
        Optional<Photo> res = photoRepository.findById(photoId);
        if (res.isEmpty()){
            throw new EntityNotFoundException(String.format("사진을 ID %d를 찾을 수 없습니다", photoId));
        }
        return new File(Constants.PATH_PREFIX + res.get().getOriginalUrl());
    }

}
