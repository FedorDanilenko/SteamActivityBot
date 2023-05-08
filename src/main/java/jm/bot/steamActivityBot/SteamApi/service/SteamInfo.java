package jm.bot.steamActivityBot.SteamApi.service;

import com.lukaspradel.steamapi.core.exception.SteamApiException;
import com.lukaspradel.steamapi.data.json.ownedgames.Game;
import com.lukaspradel.steamapi.data.json.ownedgames.GetOwnedGames;
import com.lukaspradel.steamapi.data.json.playersummaries.GetPlayerSummaries;
import com.lukaspradel.steamapi.webapi.client.SteamWebApiClient;
import com.lukaspradel.steamapi.webapi.request.GetOwnedGamesRequest;
import com.lukaspradel.steamapi.webapi.request.GetPlayerSummariesRequest;
import com.lukaspradel.steamapi.webapi.request.builders.SteamWebApiRequestFactory;
import jm.bot.steamActivityBot.dto.steamUserDto.SteamUserAllInfo;
import jm.bot.steamActivityBot.dto.steamUserDto.SteamUserShortInfo;
import jm.bot.steamActivityBot.entity.SteamApp;
import jm.bot.steamActivityBot.entity.SteamUser;
import jm.bot.steamActivityBot.mapper.SteamAppMapper;
import jm.bot.steamActivityBot.mapper.SteamUserMapper;
import jm.bot.steamActivityBot.repository.SteamAppRepo;
import jm.bot.steamActivityBot.repository.SteamUserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SteamInfo {

    private final SteamUserRepo steamUserRepo;
    private final SteamUserMapper steamUserMapper;
    private final SteamAppRepo steamAppRepo;
    private final SteamAppMapper steamAppMapper;

    private final SteamWebApiClient client;

    @Autowired
    public SteamInfo(SteamUserRepo steamUserRepo, SteamUserMapper steamUserMapper, SteamAppRepo steamAppRepo, SteamAppMapper steamAppMapper,
                     @Value("${steam.key}") String steamKey) {
        this.steamUserRepo=steamUserRepo;
        this.steamUserMapper=steamUserMapper;
        this.steamAppRepo = steamAppRepo;
        this.steamAppMapper = steamAppMapper;
        client = new SteamWebApiClient.SteamWebApiClientBuilder(steamKey).build();
    }


    @Transactional
    public SteamUserShortInfo getShortUserInfo(String userId) throws SteamApiException {

        SteamUser steamUser;

        // register the user if he is not in the database
        if (steamUserRepo.findById(Long.valueOf(userId)).isEmpty()) {
             steamUser = registerUser(userId);
        } else steamUser = steamUserRepo.findById(Long.valueOf(userId)).orElseThrow(() ->
                new EntityNotFoundException("Steam user not found"));
        System.out.println(steamUser.getSteamAppNames().size());

        return steamUserMapper.toShorInfo(steamUser);
    }


    @Transactional
    public SteamUserAllInfo getAllUserInfo(String userId) throws SteamApiException {

        SteamUser steamUser;
        // register the user if he is not in the database
        if (steamUserRepo.findById(Long.valueOf(userId)).isEmpty()) {
            steamUser = registerUser(userId);
        } else steamUser = steamUserRepo.findById(Long.valueOf(userId)).orElseThrow(() ->
                new EntityNotFoundException("Steam user not found"));
        steamUser.getSteamAppNames().size(); // initialize "steamAppNames"
        return steamUserMapper.toAllInfo(steamUser);
    }


    private SteamUser registerUser(String userId) throws SteamApiException {

        // request for user info
        GetPlayerSummariesRequest request = SteamWebApiRequestFactory.createGetPlayerSummariesRequest(Collections.singletonList(userId));
        GetPlayerSummaries userInfo = client.processRequest(request);

        String userNickName = userInfo.getResponse().getPlayers().get(0).getPersonaname();
        String avatarUrl = userInfo.getResponse().getPlayers().get(0).getAvatarfull();
        Integer time = userInfo.getResponse().getPlayers().get(0).getTimecreated();

        SteamUser steamUser = new SteamUser();

        steamUser.setId(Long.valueOf(userId));
        steamUser.setUserNickName(userNickName);
        steamUser.setAvatarUrl(avatarUrl);
        steamUser.setTimeRegister(LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.UTC));
        steamUser.setSteamAppNames(registerApps(steamUser));
        steamUserRepo.save(steamUser);

        log.info("Register steam user in database: " + steamUserMapper.toShorInfo(steamUser));

        return steamUser;

    }


    private Set<SteamApp> registerApps(SteamUser user) throws SteamApiException {

        GetOwnedGamesRequest ownedGamesRequest = SteamWebApiRequestFactory.createGetOwnedGamesRequest(String.valueOf(user.getId()),true,true, new ArrayList<>());
        GetOwnedGames ownedGames = client.processRequest(ownedGamesRequest);
        List<Game> games = ownedGames.getResponse().getGames();
        System.out.println(games.size());

        Set<SteamApp> steamAppSet = new HashSet<>();
        SteamApp steamApp;
        for (Game game: games) {
            // check game in database
            registerGame(game);
            steamApp = steamAppRepo.findById(Long.valueOf(game.getAppid())).orElseThrow(() ->
                    new EntityNotFoundException("Steam app not found"));
            steamAppSet.add(steamApp);
        }
//        steamAppRepo.saveAll(steamAppSet);

        log.info("register games in database:" + steamAppSet.stream().map(steamAppMapper::toShortInfo).collect(Collectors.toSet()));
        return steamAppSet;

    }

    private void registerGame(Game game) {
        // register game if it not on DB
        if (steamAppRepo.findById(Long.valueOf(game.getAppid())).isEmpty()) {
            SteamApp steamApp = new SteamApp();

            steamApp.setId(Long.valueOf(game.getAppid()));
            steamApp.setName(game.getName());
            steamApp.setSteamUsers(new HashSet<>());

            steamAppRepo.save(steamApp);
        }
    }

}
