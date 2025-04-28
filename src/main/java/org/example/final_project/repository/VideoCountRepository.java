package org.example.final_project.repository;

import org.example.final_project.entity.VideoCount;
import org.example.final_project.entity.VideoViewHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoCountRepository extends JpaRepository<VideoCount, Long> {
    List<VideoViewHistory> findByUserUserId(Long userId);

}
