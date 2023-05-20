package jm.bot.steamActivityBot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "steamUsers")
public class SteamUser {
    @Id
    @Column(name = "steamId")
    private Long id;

    @Column(name = "nickName")
    private String userNickName;

    @Column(name = "avatar")
    private String avatarUrl;

    @Column(name = "registerTime")
    private LocalDateTime timeRegister;

    @ManyToMany
    @JoinTable(
            name = "steamAppStat",
            joinColumns = @JoinColumn(name = "steam_id"),
            inverseJoinColumns = @JoinColumn(name = "app_id"))
    private Set<SteamApp> steamAppNames = new HashSet<>();

    @OneToMany(mappedBy = "steamUsers")
    private List<Achievement> achievements = new ArrayList<>();

//    @ManyToMany (cascade = CascadeType.ALL)
//    @JoinTable(
//            name = "user_ach",
//            joinColumns = @JoinColumn(name = "steam_id"),
//            inverseJoinColumns = @JoinColumn(name = "ach_id")
//    )
//    private Set<Achievement> userAchievements = new HashSet<>();

//    @Override
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        sb.append("Steam User: ").append(userNickName);
//        sb.append("\nSteam Id: ").append(id);
//        sb.append("\nRegistration time: ").append(timeRegister.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
//        sb.append("\nGames: ");
//        for (SteamApp app : getSteamAppNames()) {
//            sb.append("\n - ").append(app.getName());
//        }
//        sb.append("\nAvatar: ").append(avatarUrl);
//        return sb.toString();
//    }
}
