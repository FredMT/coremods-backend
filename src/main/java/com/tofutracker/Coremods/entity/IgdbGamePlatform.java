package com.tofutracker.Coremods.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity
@Table(name = "igdb_game_platforms")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(IgdbGamePlatformId.class)
public class IgdbGamePlatform {

    @Id
    @Column(name = "game_id")
    private Long gameId;

    @Id
    @Column(name = "platform_id")
    private Long platformId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", insertable = false, updatable = false)
    @ToString.Exclude
    private IgdbGame game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "platform_id", insertable = false, updatable = false)
    @ToString.Exclude
    private IgdbPlatform platform;

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
        IgdbGamePlatform that = (IgdbGamePlatform) o;
        return getGameId() != null && Objects.equals(getGameId(), that.getGameId()) &&
                getPlatformId() != null && Objects.equals(getPlatformId(), that.getPlatformId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(gameId, platformId);
    }
}