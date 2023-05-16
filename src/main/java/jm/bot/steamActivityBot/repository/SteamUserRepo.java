package jm.bot.steamActivityBot.repository;

import jm.bot.steamActivityBot.entity.SteamApp;
import jm.bot.steamActivityBot.entity.SteamUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface SteamUserRepo extends JpaRepository<SteamUser, Long> {

//    @Query("SELECT su.steamAppNames FROM steamUsers su WHERE su.id = :userId")
//    Set<SteamApp> findGamesByUserId(@Param("userId") Long userId);

}
