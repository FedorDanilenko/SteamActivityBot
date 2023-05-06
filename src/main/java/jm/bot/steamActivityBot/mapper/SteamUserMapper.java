package jm.bot.steamActivityBot.mapper;


import jm.bot.steamActivityBot.dto.steamUserDto.SteamUserShortInfo;
import jm.bot.steamActivityBot.entity.SteamUser;
import org.springframework.stereotype.Component;

@Component
public class SteamUserMapper {

    public SteamUserShortInfo toShorInfo(SteamUser steamUser) {
        return SteamUserShortInfo.builder()
                .id(steamUser.getId())
                .userNickName(steamUser.getUserNickName())
                .avatar(steamUser.getAvatarUrl())
                .timeRegister(steamUser.getTimeRegister())
                .build();
    }

}
