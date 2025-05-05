package org.example.final_project.repositories;

import org.example.final_project.entities.UserAuthentication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAuthenticationRepository extends JpaRepository<UserAuthentication, Long> {
    UserAuthentication findByUsername(String username);
}