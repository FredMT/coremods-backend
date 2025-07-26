package com.tofutracker.Coremods.entity;

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
@Table(name = "game_mods")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameMod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    @ToString.Exclude
    @NotNull(message = "Game is required")
    private IgdbGame game;

    @Enumerated(EnumType.STRING)
    @Column(name = "mod_type", nullable = false)
    @NotNull(message = "Mod type is required")
    private ModType modType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @ToString.Exclude
    private GameModCategory category;

    @Column(name = "suggested_category")
    private String suggestedCategory;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "Mod name is required")
    private String name;

    @Column(name = "language")
    private String language;

    @Column(name = "version", nullable = false)
    @NotBlank(message = "Version is required")
    private String version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @ToString.Exclude
    @NotNull(message = "Author is required")
    private User author;

    @Column(name = "overview", length = 350)
    private String overview;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Description is required")
    private String description;

    @Column(name = "has_nudity")
    @Builder.Default
    private boolean hasNudity = false;

    @Column(name = "has_skimpy_outfits")
    @Builder.Default
    private boolean hasSkimpyOutfits = false;

    @Column(name = "has_extreme_violence")
    @Builder.Default
    private boolean hasExtremeViolence = false;

    @Column(name = "is_sexualized")
    @Builder.Default
    private boolean isSexualized = false;

    @Column(name = "has_profanity")
    @Builder.Default
    private boolean hasProfanity = false;

    @Column(name = "is_character_preset")
    @Builder.Default
    private boolean isCharacterPreset = false;

    @Column(name = "has_real_world_references")
    @Builder.Default
    private boolean hasRealWorldReferences = false;

    @Column(name = "includes_visual_preset")
    @Builder.Default
    private boolean includesVisualPreset = false;

    @Column(name = "has_save_files")
    @Builder.Default
    private boolean hasSaveFiles = false;

    @Column(name = "has_translation_files")
    @Builder.Default
    private boolean hasTranslationFiles = false;

    @Column(name = "is_published", nullable = false)
    @Builder.Default
    private boolean isPublished = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ModStatus status = ModStatus.DRAFT;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

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
        GameMod gameMod = (GameMod) o;
        return getId() != null && Objects.equals(getId(), gameMod.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }

    public enum ModType {
        MOD,
        TRANSLATION
    }

    public enum ModStatus {
        DRAFT,
        PENDING_REVIEW,
        PUBLISHED,
        REJECTED
    }
}