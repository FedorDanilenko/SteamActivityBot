package jm.bot.steamActivityBot.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity(name="steamUsers")
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

    @Override
    public String toString() {
        return "Steam User: " + userNickName + "\n" +
                "Steam Id: "  + id + "\n" +
                "Registration time " + timeRegister + "\n" +
                "Avatar: "  + avatarUrl + "\n";
    }
}
