package org.example.final_project.repositories;


import org.example.final_project.model.User;
import org.example.final_project.payloads.UserDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String userName);

    Optional<UserDto> findByEmail(String email);

    List<User> findByUsernameOrUsernameStartsWith(String username, String usernameStartWith);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
