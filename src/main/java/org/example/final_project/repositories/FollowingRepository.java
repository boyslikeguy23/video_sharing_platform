package org.example.final_project.repositories;


import org.example.final_project.entities.Following;
import org.example.final_project.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowingRepository extends JpaRepository<Following, Long> {
    List<Following> findByFollower(User follower);
    List<Following> findByFollowing(User following);

    Optional<Following> findByFollowerAndFollowing(User follower, User following);

    Long countByFollower(User follower);
    Long countByFollowing(User following);
}

