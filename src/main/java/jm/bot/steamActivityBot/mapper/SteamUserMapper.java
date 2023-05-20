package jm.bot.steamActivityBot.mapper;


import jm.bot.steamActivityBot.dto.steamUserDto.SteamUserAllInfo;
import jm.bot.steamActivityBot.dto.steamUserDto.SteamUserShortInfo;
import jm.bot.steamActivityBot.entity.SteamUser;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

import java.time.LocalDate;

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

    @Transactional
    public SteamUserAllInfo toAllInfo(SteamUser steamUser) {
        return SteamUserAllInfo.builder()
                .id(steamUser.getId())
                .userNickName(steamUser.getUserNickName())
                .avatar(steamUser.getAvatarUrl())
                .timeRegister(steamUser.getTimeRegister())
                .steamAppNames(steamUser.getSteamAppNames())
                .TotalAchievements(steamUser.getAchievements().stream().filter(t -> t.getTimeRec().isAfter(LocalDate.of(1970, 1, 1))).toList().size())
                .build();
    }

}
