package org.example.final_project.repository;

import org.example.final_project.entity.VideoViewHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface VideoViewHistoryRepository extends JpaRepository<VideoViewHistory, Long> {
    List<VideoViewHistory> findByUserUserId(Long userId);

}
