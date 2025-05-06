package org.example.final_project.repositories;

import org.example.final_project.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Tìm user theo email
    Optional<User> findByEmail(String email);

    // Check tồn tại email
    boolean existsByEmail(String email);

    List<User> findByNickNameContainingIgnoreCase(String nickName);

    List<User> findByFullNameContainingIgnoreCase(String fullName);
}

