package jm.bot.steamActivityBot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Achievements")
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "achId")
    private Long id;

    @Column(name = "title")
    private String achTitle;

    @Column(name = "receivingTime")
    private LocalDate timeRec;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private SteamApp games;

    @ManyToOne
    @JoinColumn(name = "steam_user_id", nullable = false)
    private SteamUser steamUsers;

}
