package jm.bot.steamActivityBot.repository;

import jm.bot.steamActivityBot.entity.SteamApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SteamAppRepo extends JpaRepository<SteamApp, Long> {
}
