package org.example.final_project.repositories;

import org.example.final_project.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // Null timestamps from older data sort last, with the same ID tie-breaker.
    String CURSOR_FILTER = "(:firstPage = true OR "
            + "(:nullTime = false AND (p.createdAt < :beforeTime OR "
            + "(p.createdAt = :beforeTime AND p.id < :beforeId))) OR "
            + "(p.createdAt IS NULL AND (:nullTime = false OR p.id < :beforeId)))";

    @Query("SELECT p FROM Post p WHERE " + CURSOR_FILTER + " ORDER BY p.createdAt DESC NULLS LAST, p.id DESC")
    List<Post> findFeedPage(@Param("firstPage") boolean firstPage, @Param("beforeTime") LocalDateTime beforeTime,
                            @Param("beforeId") long beforeId, @Param("nullTime") boolean nullTime, Pageable limit);

    @Query("SELECT p FROM Post p WHERE p.user.id IN :users AND " + CURSOR_FILTER
            + " ORDER BY p.createdAt DESC NULLS LAST, p.id DESC")
    List<Post> findAuthorsPage(@Param("users") List<Long> users, @Param("firstPage") boolean firstPage,
                               @Param("beforeTime") LocalDateTime beforeTime, @Param("beforeId") long beforeId,
                               @Param("nullTime") boolean nullTime, Pageable limit);
	
	@Query("select p from Post p where p.user.id=?1")
	public List<Post> findByUserId (Long userId);
	
    @Query("SELECT p FROM Post p WHERE p.user.id IN :users ORDER BY p.createdAt DESC")
    public List<Post> findAllPostByUserIds(@Param("users") List<Long> userIds);

    @Query("SELECT p FROM Post p WHERE p.user.id IN :users ORDER BY p.createdAt DESC")
    public List<Post> findAllPostByUserIdsSortedByDateDesc(@Param("users") List<Long> userIds);

}
