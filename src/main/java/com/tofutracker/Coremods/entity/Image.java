package com.tofutracker.Coremods.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "images")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_mod_id", nullable = false)
    @ToString.Exclude
    @NotNull(message = "GameMod is required")
    private GameMod gameMod;
    
    @Column(name = "name", nullable = false)
    @NotBlank(message = "Image name is required")
    private String name;
    
    @Column(name = "ext", nullable = false)
    @NotBlank(message = "File extension is required")
    private String ext;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "image_type", nullable = false)
    @NotNull(message = "Image type is required")
    private ImageType imageType;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "display_order")
    private Integer displayOrder;
    
    @CreationTimestamp
    @Column(name = "created_time", updatable = false)
    private LocalDateTime createdTime;
    
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Image image = (Image) o;
        return getId() != null && Objects.equals(getId(), image.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
    
    public enum ImageType {
        HEADER,
        MOD_IMAGE
    }
} 