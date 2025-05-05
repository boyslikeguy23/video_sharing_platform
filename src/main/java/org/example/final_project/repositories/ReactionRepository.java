package org.example.final_project.repositories;

import org.example.final_project.entities.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Reaction findByUserUserIdAndVideoVideoId(Long userId, Long videoId);
}
