package jm.bot.steamActivityBot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "steam_app_stat")
public class SteamAppStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "steam_id")
    private SteamUser steamUser;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private SteamApp steamApp;

    @Column(name = "time_spent")
    private int allTimeSpent;
}
