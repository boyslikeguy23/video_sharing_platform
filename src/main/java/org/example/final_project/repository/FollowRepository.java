package org.example.final_project.repository;


import org.example.final_project.entity.Follow;
import org.example.final_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    List<Follow> findByFollower(User follower);
    List<Follow> findByFollowing(User following);

    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    Long countByFollower(User follower);
    Long countByFollowing(User following);
}

