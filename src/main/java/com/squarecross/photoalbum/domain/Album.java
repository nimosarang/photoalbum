package com.squarecross.photoalbum.domain;

import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Data
@Table(name = "album", schema = "photo_album", uniqueConstraints = {
    @UniqueConstraint(columnNames = "album_id")})
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //가장 최근 id에 +1 을 해서 다음 아이디를 생성합니다.
    @Column(name = "album_id", unique = true, nullable = false)
    private Long albumId;

    @Column(name = "album_name", unique = true, nullable = false)
    private String albumName;

    @Column(name = "created_at", unique = true, nullable = false)
    @CreationTimestamp //새로운 앨범을 생성해 DB INSERT 할 때 자동으로 현재 시간을 입력해줍니다.
    private Date createdAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "album", cascade = CascadeType.ALL)
    private List<Photo> photos;

    public Album() {
    }
}
