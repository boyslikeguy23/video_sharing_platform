package org.example.final_project.repository;

import org.example.final_project.entity.Subcription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface SubcriptionRepository extends JpaRepository<Subcription, Long> {
}
