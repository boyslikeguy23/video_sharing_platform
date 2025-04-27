package org.example.final_project.repository;

import org.example.final_project.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {
}
