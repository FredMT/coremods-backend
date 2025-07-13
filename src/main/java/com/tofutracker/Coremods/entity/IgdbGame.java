package com.tofutracker.Coremods.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "igdb_games")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class IgdbGame {
    
    @Id
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String summary;
    
    @Column(name = "cover_url")
    private String coverUrl;
    
    @Column(name = "release_date")
    private Long releaseDate;
    
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "igdb_games_platforms",
        joinColumns = @JoinColumn(name = "game_id"),
        inverseJoinColumns = @JoinColumn(name = "platform_id")
    )
    @ToString.Exclude
    private Set<IgdbPlatform> platforms = new HashSet<>();
    
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<GameModCategory> modCategories = new HashSet<>();
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public void addPlatform(IgdbPlatform platform) {
        this.platforms.add(platform);
        platform.getGames().add(this);
    }
    
    public void removePlatform(IgdbPlatform platform) {
        this.platforms.remove(platform);
        platform.getGames().remove(this);
    }
    
    public void addModCategory(GameModCategory category) {
        this.modCategories.add(category);
        category.setGame(this);
    }
    
    public void removeModCategory(GameModCategory category) {
        this.modCategories.remove(category);
        category.setGame(null);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        IgdbGame igdbGame = (IgdbGame) o;
        return getId() != null && Objects.equals(getId(), igdbGame.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}