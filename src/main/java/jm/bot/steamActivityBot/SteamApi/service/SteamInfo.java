package jm.bot.steamActivityBot.SteamApi.service;

import com.lukaspradel.steamapi.core.exception.SteamApiException;
import com.lukaspradel.steamapi.data.json.ownedgames.Game;
import com.lukaspradel.steamapi.data.json.ownedgames.GetOwnedGames;
import com.lukaspradel.steamapi.data.json.playerachievements.Achievement;
import com.lukaspradel.steamapi.data.json.playerachievements.GetPlayerAchievements;
import com.lukaspradel.steamapi.data.json.playersummaries.GetPlayerSummaries;
import com.lukaspradel.steamapi.webapi.client.SteamWebApiClient;
import com.lukaspradel.steamapi.webapi.request.GetOwnedGamesRequest;
import com.lukaspradel.steamapi.webapi.request.GetPlayerAchievementsRequest;
import com.lukaspradel.steamapi.webapi.request.GetPlayerSummariesRequest;
import com.lukaspradel.steamapi.webapi.request.builders.SteamWebApiRequestFactory;
import jm.bot.steamActivityBot.dto.steamUserDto.SteamUserAllInfo;
import jm.bot.steamActivityBot.dto.steamUserDto.SteamUserShortInfo;
import jm.bot.steamActivityBot.entity.SteamApp;
import jm.bot.steamActivityBot.entity.SteamAppStat;
import jm.bot.steamActivityBot.entity.SteamUser;
import jm.bot.steamActivityBot.mapper.SteamAppMapper;
import jm.bot.steamActivityBot.mapper.SteamUserMapper;
import jm.bot.steamActivityBot.repository.AchievementRepo;
import jm.bot.steamActivityBot.repository.SteamAppRepo;
import jm.bot.steamActivityBot.repository.SteamAppStatRepo;
import jm.bot.steamActivityBot.repository.SteamUserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
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
    private final SteamAppStatRepo steamAppStatRepo;
    private final AchievementRepo achievementRepo;

    private final SteamWebApiClient client;

    @Autowired
    public SteamInfo(SteamUserRepo steamUserRepo, SteamUserMapper steamUserMapper, SteamAppRepo steamAppRepo, SteamAppMapper steamAppMapper,
                     SteamAppStatRepo steamAppStatRepo, AchievementRepo achievementRepo, @Value("${steam.key}") String steamKey) {
        this.steamUserRepo=steamUserRepo;
        this.steamUserMapper=steamUserMapper;
        this.steamAppRepo = steamAppRepo;
        this.steamAppMapper = steamAppMapper;
        this.steamAppStatRepo = steamAppStatRepo;
        this.achievementRepo = achievementRepo;
        client = new SteamWebApiClient.SteamWebApiClientBuilder(steamKey).build();
    }


    @Transactional
    public SteamUserShortInfo getShortUserInfo(String userId) throws SteamApiException {

        SteamUser steamUser;

        // register the steam user if he is not in the database
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
        // register the steam user if he is not in the database
        if (steamUserRepo.findById(Long.valueOf(userId)).isEmpty()) {
            steamUser = registerUser(userId);
        } else steamUser = steamUserRepo.findById(Long.valueOf(userId)).orElseThrow(() ->
                new EntityNotFoundException("Steam user not found"));
        steamUser.getSteamAppNames().size(); // initialize "steamAppNames"
        return steamUserMapper.toAllInfo(steamUser);
    }

    public Map<LocalDate, Integer> getUserActivity(String userId) throws SteamApiException {

        // register the steam user if he is not in the database
        if (steamUserRepo.findById(Long.valueOf(userId)).isEmpty()) {
            registerUser(userId);
        }

        // get a list of unlocktime of all user achievements
        List<LocalDate> allUnLockTimeStepList = new ArrayList<>(achievementRepo.findBySteamUsersIdAndTimeRecAfter(Long.valueOf(userId), LocalDate.of(1970, 1, 1))
                .stream().map(jm.bot.steamActivityBot.entity.Achievement::getTimeRec).toList());
        System.out.println(allUnLockTimeStepList.size());

        System.out.println(allUnLockTimeStepList);

        // Count days
        Map<LocalDate, Integer> timeStampCount = new HashMap<>();
        allUnLockTimeStepList.forEach(date -> {
            timeStampCount.put(date, timeStampCount.getOrDefault(date, 0) + 1);
        });
        System.out.println(timeStampCount);

        // sort by date
        List<Map.Entry<LocalDate, Integer>> list = new ArrayList<>(timeStampCount.entrySet());

        list.sort(Map.Entry.comparingByKey());

        Map<LocalDate, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<LocalDate, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        System.out.println(sortedMap);
        return sortedMap;

    }


    private SteamUser registerUser(String userId) throws SteamApiException {

        // request for user info
        GetPlayerSummariesRequest request = SteamWebApiRequestFactory.createGetPlayerSummariesRequest(Collections.singletonList(userId));
        GetPlayerSummaries userInfo = client.processRequest(request);

        String userNickName = userInfo.getResponse().getPlayers().get(0).getPersonaname();
        String avatarUrl = userInfo.getResponse().getPlayers().get(0).getAvatarfull();
        Integer time = userInfo.getResponse().getPlayers().get(0).getTimecreated();

        SteamUser steamUser = SteamUser.builder()
                .id(Long.valueOf(userId))
                .userNickName(userNickName)
                .avatarUrl(avatarUrl)
                .timeRegister(LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.UTC))
                .build();

        steamUserRepo.save(steamUser);
        steamUser.setSteamAppNames(registerApps(userId, steamUser));
        registerAchievements(steamUser);

        log.info("Register steam user in database: " + steamUserMapper.toShorInfo(steamUser));

        return steamUser;

    }


    private Set<SteamApp> registerApps(String userId, SteamUser steamUser) throws SteamApiException {

        GetOwnedGamesRequest ownedGamesRequest = SteamWebApiRequestFactory.createGetOwnedGamesRequest(userId,true,true, new ArrayList<>());
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
            registerStat(steamApp, steamUser, game);
            steamAppSet.add(steamApp);
        }

        log.info("register games in database:" + steamAppSet.stream().map(steamAppMapper::toShortInfo).collect(Collectors.toSet()));
        return steamAppSet;

    }

    private void registerGame(Game game) {
        // register game if it not on DB
        if (steamAppRepo.findById(Long.valueOf(game.getAppid())).isEmpty()) {
            SteamApp steamApp = SteamApp.builder()
                    .id(Long.valueOf(game.getAppid()))
                    .name(game.getName())
                    .hasAsh(game.getHasCommunityVisibleStats() != null)
                    .build();

            steamAppRepo.save(steamApp);
        }
    }

    private void registerStat(SteamApp steamApp, SteamUser steamUser, Game game) {
        SteamAppStat steamAppStat = SteamAppStat.builder()
                .steamUser(steamUser)
                .steamApp(steamApp)
                .allTimeSpent(game.getPlaytimeForever())
                .build();

        steamAppStatRepo.save(steamAppStat);
    }

    private void registerAchievements(SteamUser steamUser) throws SteamApiException {
        // get list id of games with achievements
        List<Long> userGamesWithAchievements = steamUserRepo.findGamesWithAchievementsByUserId(steamUser.getId()).stream()
                .map(SteamApp::getId).toList();

        // get achievements by all user game
        for (Long appId : userGamesWithAchievements) {
            System.out.println(appId);
            GetPlayerAchievementsRequest requestAch = SteamWebApiRequestFactory.createGetPlayerAchievementsRequest(Math.toIntExact(appId), String.valueOf(steamUser.getId()));
            GetPlayerAchievements playerAchievements = client.processRequest(requestAch);
            for (Achievement ach : playerAchievements.getPlayerstats().getAchievements()) {
                jm.bot.steamActivityBot.entity.Achievement achievement = jm.bot.steamActivityBot.entity.Achievement.builder()
                        .steamApp(steamAppRepo.findById(appId).orElseThrow(() ->
                                new EntityNotFoundException("Steam app not found")))
                        .steamUsers(steamUser)
                        .achTitle(ach.getApiname())
                        .timeRec(LocalDate.from(LocalDateTime.ofEpochSecond((Integer) ach.getAdditionalProperties().get("unlocktime"), 0, ZoneOffset.UTC)))
                        .build();
                achievementRepo.save(achievement);
            }
        }
    }

}
