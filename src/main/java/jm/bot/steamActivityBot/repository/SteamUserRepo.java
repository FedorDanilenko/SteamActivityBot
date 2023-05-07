package jm.bot.steamActivityBot.repository;

import jm.bot.steamActivityBot.entity.SteamUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SteamUserRepo extends CrudRepository<SteamUser, Long>  {

}
