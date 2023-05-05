package jm.bot.steamActivityBot.SteamApi.service;

import com.lukaspradel.steamapi.core.exception.SteamApiException;
import com.lukaspradel.steamapi.data.json.playersummaries.GetPlayerSummaries;
import com.lukaspradel.steamapi.webapi.client.SteamWebApiClient;
import com.lukaspradel.steamapi.webapi.request.GetPlayerSummariesRequest;
import com.lukaspradel.steamapi.webapi.request.builders.SteamWebApiRequestFactory;
import jm.bot.steamActivityBot.entity.SteamUser;
import jm.bot.steamActivityBot.repository.SteamUserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
public class SteamInfo {

    @Autowired
    private SteamUserRepo steamUserRepo;
    @Value("${steam.key}")
    private String steamkey;
    private String userId;

    SteamWebApiClient client = new SteamWebApiClient.SteamWebApiClientBuilder(steamkey).build();

//    private SteamUser getShortUserInfo(String userId) throws SteamApiException {
//
//        // register the user if he is not in the database
//        registerUser(userId);
//        SteamUser steamUser =
//
//        return steamUserRepo.findById(Long.valueOf(userId));
//
//    }

//    private SteamUser getAllUserInfo(String userId) throws SteamApiException {
//
//        // register the user if he is not in the database
//        registerUser(userId);
//        SteamUser steamUser =
//
//        return steamUserRepo.findById(Long.valueOf(userId));
//
//    }

    public String getInfo() {
        return "User info: ....";
    }

    private void registerUser(String userId) throws SteamApiException {
        this.userId=userId;

        if (steamUserRepo.findById(Long.valueOf(userId)).isEmpty()) {

            // request for user info
            GetPlayerSummariesRequest request = SteamWebApiRequestFactory.createGetPlayerSummariesRequest(Collections.singletonList(userId));
            GetPlayerSummaries userInfo = client.processRequest(request);

            String userNickName = userInfo.getResponse().getPlayers().get(0).getPersonaname();
            String avatarUrl = userInfo.getResponse().getPlayers().get(0).getAvatarfull();

            SteamUser steamUser = new SteamUser();

            steamUser.setId(Long.valueOf(userId));
            steamUser.setUserNickName(userNickName);
            steamUser.setAvatarUrl(avatarUrl);
            log.info("Register steam user in database: " + steamUser);
        }
    }

}
