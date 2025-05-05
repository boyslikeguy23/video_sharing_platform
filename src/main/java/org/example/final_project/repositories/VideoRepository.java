package org.example.final_project.repositories;

import org.example.final_project.entities.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findByCaptionContaining(String keyword);
    List<Video> findByUserUserId(Long userId);
}
