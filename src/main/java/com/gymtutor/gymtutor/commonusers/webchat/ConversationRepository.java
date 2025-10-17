package com.gymtutor.gymtutor.commonusers.webchat;

import com.gymtutor.gymtutor.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {


    Optional<Conversation> findByUser1AndUser2(User user1, User user2);
    Optional<Conversation> findByUser2AndUser1(User user2, User user1);

    default Optional<Conversation> findBetweenUsers(User user1, User user2) {
        return findByUser1AndUser2(user1, user2).or(() -> findByUser2AndUser1(user1, user2));
    }

    // métodos para pegar conversas do usuário
    List<Conversation> findByUser1(User user);
    List<Conversation> findByUser2(User user);
}