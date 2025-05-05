package org.example.final_project.repositories;

import org.example.final_project.entities.VideoCount;
import org.example.final_project.entities.VideoViewHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoCountRepository extends JpaRepository<VideoCount, Long> {
    List<VideoViewHistory> findByUserUserId(Long userId);

}
