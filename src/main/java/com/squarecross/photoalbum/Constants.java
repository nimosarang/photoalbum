package com.squarecross.photoalbum;

public class Constants { //글로벌 Constants 활용하기
    public static final String PATH_PREFIX = "/Users/eddy/Desktop/Spring/photoalbum";

    //DB에 저장되는 Original Url 과 Thumb Url 은 프로젝트 경로를 제외한 경로입니다.
    // 프로젝트 경로는 환경에 따라 다르기 때문 입니다. 따라서 프로젝트 경로는 Constants 로 빼서 관리합니다
    //실제 배포가 되면 AWS S3같은 클라우드 스토리지에 배포가 될텐데, 거기서도 해당 경로를 사용할 수 있어서 향후 옮기기도 편합니다.

    //썸네일 저장 로직 추가
    //변경 가능한 수치는 코드에 직접 넣는거보다 Constants 로 별도로 빼두는 것이 나중에 수정하기에 용이해요.
    public static final int THUMB_SIZE = 300;

}
