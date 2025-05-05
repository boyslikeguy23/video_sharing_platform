package org.example.final_project.repositories;

import org.example.final_project.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Tìm user theo email
    Optional<User> findByEmail(String email);

    // Tìm user theo full name (không bắt buộc unique)
    List<User> findByFullNameContainingIgnoreCase(String fullName);

    // Check tồn tại email
    boolean existsByEmail(String email);

    // Tìm theo phone number
    Optional<User> findByPhoneNumber(String phoneNumber);
}
