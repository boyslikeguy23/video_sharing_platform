package org.example.final_project.repositories;

import org.example.final_project.entities.VideoViewHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoViewHistoryRepository extends JpaRepository<VideoViewHistory, Long> {
    List<VideoViewHistory> findByUserUserId(Long userId);

}
