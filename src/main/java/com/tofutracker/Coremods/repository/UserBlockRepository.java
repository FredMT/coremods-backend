package com.tofutracker.Coremods.repository;

import com.tofutracker.Coremods.config.enums.BlockScopeType;
import com.tofutracker.Coremods.entity.UserBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBlockRepository extends JpaRepository<UserBlock, Long> {

    // Check if a user is blocked by another user in a specific scope
    Optional<UserBlock> findByBlockerIdAndBlockedIdAndScopeType(Long blockerId, Long blockedId,
            BlockScopeType scopeType);

    // Check if a user is blocked from a specific mod
    Optional<UserBlock> findByBlockerIdAndBlockedIdAndScopeTypeAndModId(Long blockerId, Long blockedId,
            BlockScopeType scopeType, Long modId);

    // Check if a user is blocked from all mods by a specific author
    Optional<UserBlock> findByBlockerIdAndBlockedIdAndScopeTypeAndBlockedAuthorId(Long blockerId, Long blockedId,
            BlockScopeType scopeType, Long blockedAuthorId);

    // Get all blocks created by a user
    List<UserBlock> findByBlockerIdOrderByCreatedAtDesc(Long blockerId);

    // Get all blocks affecting a user (where they are blocked)
    List<UserBlock> findByBlockedIdOrderByCreatedAtDesc(Long blockedId);

    // Get all blocks in a specific scope
    List<UserBlock> findByScopeTypeOrderByCreatedAtDesc(BlockScopeType scopeType);

    // Check if user is blocked from interacting with a specific mod (either direct
    // mod block or author global block)
    @Query("SELECT ub FROM UserBlock ub WHERE ub.blocker.id = :blockerId AND ub.blocked.id = :blockedId AND " +
            "((ub.scopeType = 'MOD' AND ub.mod.id = :modId) OR " +
            "(ub.scopeType = 'AUTHOR_GLOBAL' AND ub.blockedAuthor.id = :authorId))")
    List<UserBlock> findModInteractionBlocks(@Param("blockerId") Long blockerId,
            @Param("blockedId") Long blockedId,
            @Param("modId") Long modId,
            @Param("authorId") Long authorId);

    // Check if user is blocked from any interaction (direct messages or general
    // interaction)
    @Query("SELECT ub FROM UserBlock ub WHERE ub.blocker.id = :blockerId AND ub.blocked.id = :blockedId AND " +
            "ub.scopeType IN ('DIRECT_MESSAGES', 'INTERACTION')")
    List<UserBlock> findGeneralInteractionBlocks(@Param("blockerId") Long blockerId,
            @Param("blockedId") Long blockedId);
}