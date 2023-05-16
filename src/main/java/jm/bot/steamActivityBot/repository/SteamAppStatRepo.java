package jm.bot.steamActivityBot.repository;

import jm.bot.steamActivityBot.entity.SteamAppStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SteamAppStatRepo extends JpaRepository<SteamAppStat, Long> {
}
