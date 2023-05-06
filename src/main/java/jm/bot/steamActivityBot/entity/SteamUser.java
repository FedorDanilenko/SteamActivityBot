package jm.bot.steamActivityBot.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

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
}
