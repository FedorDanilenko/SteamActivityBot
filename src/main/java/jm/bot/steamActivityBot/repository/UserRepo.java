package jm.bot.steamActivityBot.repository;

import jm.bot.steamActivityBot.entity.BotUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<BotUser, Long> {
}
