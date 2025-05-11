package org.example.final_project.repositories;


import org.example.final_project.model.Chat;
import org.example.final_project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepo extends JpaRepository<Chat,Long> {

//    @Query("select c from  Chat c join c.users u where u.id=:userId")
//    List<Chat> findByCreateBy(@Param("userId") Long userId);

    List<Chat> findByUsersId(@Param("userId") Long userId);


    @Query("select c from Chat c where c.isGroup=false and :user member of c.users and :reqUser member  of c.users")
    Chat findSingleChatByUserId(@Param("user") User user, @Param("reqUser") User reqUser);
//Chat findFirstByIsGroupFalseAndUsersInAndUsersIn(User user, User reqUser);

}
