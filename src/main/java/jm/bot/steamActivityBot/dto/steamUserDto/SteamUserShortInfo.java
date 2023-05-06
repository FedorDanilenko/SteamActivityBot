package jm.bot.steamActivityBot.dto.steamUserDto;

import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SteamUserShortInfo {

    private Long id;
    private String userNickName;
    private String avatar;
    private LocalDateTime timeRegister;

    @Override
    public String toString() {
        return "Steam User: " + userNickName + "\n" +
                "Steam Id: "  + id + "\n" +
                "Registration time: " + timeRegister.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) + "\n" +
                "Avatar: "  + avatar + "\n";
    }
}
