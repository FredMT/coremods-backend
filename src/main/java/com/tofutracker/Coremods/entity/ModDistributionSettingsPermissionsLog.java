package com.tofutracker.Coremods.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "mod_distribution_settings_permissions_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModDistributionSettingsPermissionsLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mod_id", nullable = false)
    private GameMod mod;

    @Column(name = "changed_fields", columnDefinition = "JSONB")
    private String changedFields;

    @CreationTimestamp
    @Column(name = "changed_at", nullable = false, updatable = false)
    private LocalDateTime changedAt;
}