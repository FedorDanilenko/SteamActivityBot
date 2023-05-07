package jm.bot.steamActivityBot.repository;

import jm.bot.steamActivityBot.entity.BotUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends CrudRepository<BotUser, Long> {
}
