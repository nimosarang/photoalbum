package com.squarecross.photoalbum.domain;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Data
@Table(name = "photo", schema = "photo_album", uniqueConstraints = {
    @UniqueConstraint(columnNames = "photo_id")})
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id",unique = true,nullable = false)
    private Long photoId;

    @Column(name = "file_name",unique = false, nullable = true)
    private String fileName;

    @Column(name = "file_size",unique = false, nullable = true)
    private int fileSize;

    @Column(name = "original_url",unique = false, nullable = true)
    private String originalUrl;

    @Column(name = "thumb_url", unique = false, nullable = true)
    private String thumbUrl;

    @Column(name = "uploaded_at",unique = false, nullable = true)
    @CreationTimestamp
    private Date uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private Album album;

    public Photo() {
    }
}
