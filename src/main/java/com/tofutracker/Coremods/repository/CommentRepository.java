package com.tofutracker.Coremods.repository;

import com.tofutracker.Coremods.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Find all comments (including soft deleted) for a commentable entity
     */
    List<Comment> findByCommentableTypeAndCommentableIdOrderByCreatedAtAsc(String commentableType, Long commentableId);
}