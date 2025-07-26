package com.tofutracker.Coremods.repository;

import com.tofutracker.Coremods.config.enums.ModImageType;
import com.tofutracker.Coremods.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    Optional<List<Image>> findByImageableTypeAndImageableIdOrderByDisplayOrderAsc(String imageableType,
            Long imageableId);

    Optional<Image> findFirstByImageableTypeAndImageableIdAndImageType(String imageableType, Long imageableId,
            ModImageType imageType);

    Optional<List<Image>> findByImageableTypeAndImageableIdAndImageType(String imageableType, Long imageableId,
            ModImageType imageType);

    Optional<Long> countByImageableTypeAndImageableIdAndImageType(String imageableType, Long imageableId,
            ModImageType imageType);
            
    void deleteByImageableTypeAndImageableId(String imageableType, Long imageableId);

    @Modifying
    @Query("UPDATE Image i SET i.displayOrder = i.displayOrder - 1 WHERE i.imageableType = :imageableType AND i.imageableId = :imageableId AND i.displayOrder > :deletedDisplayOrder")
    void shiftDisplayOrderDown(@Param("imageableType") String imageableType, 
                              @Param("imageableId") Long imageableId, 
                              @Param("deletedDisplayOrder") Integer deletedDisplayOrder);
}