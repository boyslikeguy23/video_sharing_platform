package org.example.final_project.repositories;

import org.example.final_project.models.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StoryRepository extends JpaRepository<Story, Long>{

	@Query("SELECT s FROM Story s WHERE s.userDto.id = :userId")
    List<Story> findAllStoriesByUserId(@Param("userId") Long userId);

}
