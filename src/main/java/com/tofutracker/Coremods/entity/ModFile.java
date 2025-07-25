package com.tofutracker.Coremods.entity;

import com.tofutracker.Coremods.config.enums.FileCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "mod_files")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mod_id", nullable = false)
    @ToString.Exclude
    @NotNull(message = "Game mod is required")
    private GameMod mod;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "File name is required")
    private String name;

    @Column(name = "original_filename", nullable = false)
    @NotBlank(message = "Original filename is required")
    private String originalFilename;

    @Column(name = "storage_key", nullable = false)
    @NotBlank(message = "Storage key is required")
    private String storageKey;

    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "ext", nullable = false)
    @NotBlank(message = "File extension is required")
    private String ext;

    @Column(name = "description")
    private String description;

    @Column(name = "version")
    private String version;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    @NotNull(message = "File category is required")
    private FileCategory category;

    @Column(name = "download_count")
    @Builder.Default
    private Long downloadCount = 0L;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy
                ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass)
            return false;
        ModFile modFile = (ModFile) o;
        return getId() != null && Objects.equals(getId(), modFile.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
} 