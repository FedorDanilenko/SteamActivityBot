package jm.bot.steamActivityBot.repository;

import jm.bot.steamActivityBot.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AchievementRepo extends JpaRepository<Achievement, Long> {

    List<Achievement> findBySteamUsersIdAndTimeRecAfter(Long userId, LocalDate date);

}
