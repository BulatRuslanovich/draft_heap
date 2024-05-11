package com.bul.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "app_sticker")
@Entity
public class AppSticker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String telegramFileId;

    private String telegramFileUniqueId;

    @ManyToOne
    private AppUser appUser;

    @OneToOne
    private BinaryContent binaryContent;

    @CreationTimestamp
    private LocalDateTime loadDateTime;

    private Integer fileSize;

    private Boolean isAnimated;

    private String emoji;

    private Boolean isVideo;
}
