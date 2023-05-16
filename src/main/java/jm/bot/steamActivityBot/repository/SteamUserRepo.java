package jm.bot.steamActivityBot.repository;

import jm.bot.steamActivityBot.entity.SteamApp;
import jm.bot.steamActivityBot.entity.SteamUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface SteamUserRepo extends JpaRepository<SteamUser, Long> {

    @Query("SELECT su.steamAppNames FROM steamUsers su WHERE su.id = :userId")
    Set<SteamApp> findGamesByUserId(@Param("userId") Long userId);

    @Query("SELECT sa FROM steamUsers su JOIN su.steamAppNames sa LEFT JOIN sa.steamUsers su2 WHERE su.id = :userId AND sa.hasAsh = true AND EXISTS (SELECT 1 FROM steamAppStat sas WHERE sas.steamUser = su AND sas.steamApp = sa AND sas.allTimeSpent > 0)")
    Set<SteamApp> findGamesWithAchievementsByUserId(@Param("userId") Long userId);


}
