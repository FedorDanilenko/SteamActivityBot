package jm.bot.steamActivityBot.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Data
@Entity(name = "steamUsers")
public class SteamUser {
    @Id
    @Column(name = "steamId")
    private Long id;

    @Column(name = "nickName")
    private String userNickName;

    @Column(name = "avatar")
    private String avatarUrl;

    @Column(name = "refister")
    private LocalDateTime timeRegister;

    @OneToMany(mappedBy = "steamUser", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SteamApp> steamAppNames;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Steam User: ").append(userNickName);
        sb.append("\nSteam Id: ").append(id);
        sb.append("\nRegistration time: ").append(timeRegister.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        sb.append("\nGames: ");
        for (SteamApp app : steamAppNames) {
            sb.append("\n - ").append(app.getName());
        }
        sb.append("\nAvatar: ").append(avatarUrl);
        return sb.toString();
    }
}
