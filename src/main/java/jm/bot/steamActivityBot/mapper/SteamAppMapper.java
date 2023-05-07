package jm.bot.steamActivityBot.mapper;


import jm.bot.steamActivityBot.dto.steamUserDto.SteamAppShortInfo;
import jm.bot.steamActivityBot.entity.SteamApp;
import org.springframework.stereotype.Component;

@Component
public class SteamAppMapper {

    public SteamAppShortInfo toShortInfo(SteamApp steamApp) {
        return SteamAppShortInfo.builder()
                .id(steamApp.getId())
                .name(steamApp.getName())
                .build();
    }

}
