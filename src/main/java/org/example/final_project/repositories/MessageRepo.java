package org.example.final_project.repositories;

import org.example.final_project.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRepo extends JpaRepository<Message, Long> {

    @Query("select m from Message m join m.chat c where c.id=:chatId")
     Optional<List<Message>> findByChatId(@Param("chatId") Long chatId);

}
