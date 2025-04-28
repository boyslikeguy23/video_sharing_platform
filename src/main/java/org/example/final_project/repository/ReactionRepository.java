package org.example.final_project.repository;

import org.example.final_project.entity.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Reaction findByUserUserIdAndVideoVideoId(Long userId, Long videoId);
}
