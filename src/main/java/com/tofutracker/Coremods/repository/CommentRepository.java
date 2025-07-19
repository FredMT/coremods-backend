package com.tofutracker.Coremods.repository;

import com.tofutracker.Coremods.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByCommentableTypeAndCommentableIdOrderByCreatedAtAsc(String commentableType, Long commentableId);

    Optional<Comment> findByCommentableTypeAndCommentableIdAndParentId(String commentableType, Long commentableId,
            Long parentId);
}