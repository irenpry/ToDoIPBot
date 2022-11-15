package ru.pryadkina.bot.todoipbot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pryadkina.bot.todoipbot.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    public User findByChatId(long chatId);

}
