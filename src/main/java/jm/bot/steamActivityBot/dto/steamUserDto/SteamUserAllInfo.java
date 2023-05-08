package jm.bot.steamActivityBot.dto.steamUserDto;

import com.lukaspradel.steamapi.data.json.ownedgames.Game;
import jm.bot.steamActivityBot.entity.SteamApp;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SteamUserAllInfo {

    private Long id;
    private String userNickName;
    private String avatar;
    private LocalDateTime timeRegister;
    private Set<SteamApp> steamAppNames;
//    private int TotalAchievements;


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Steam User: ").append(userNickName);
        sb.append("\nSteam Id: ").append(id);
        sb.append("\nRegistration time: ").append(timeRegister.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        sb.append("\nGames count:").append(getSteamAppNames().size());
        for (SteamApp app : getSteamAppNames()) {
            sb.append("\n - ").append(app.getName());
        }
        sb.append("\nAvatar: ").append(avatar);
        return sb.toString();
    }
}
