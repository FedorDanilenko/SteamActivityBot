package jm.bot.steamActivityBot.SteamApi.service;

import com.lukaspradel.steamapi.core.exception.SteamApiException;
import com.lukaspradel.steamapi.data.json.playersummaries.GetPlayerSummaries;
import com.lukaspradel.steamapi.webapi.client.SteamWebApiClient;
import com.lukaspradel.steamapi.webapi.request.GetPlayerSummariesRequest;
import com.lukaspradel.steamapi.webapi.request.builders.SteamWebApiRequestFactory;
import jm.bot.steamActivityBot.entity.SteamUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

//@SpringBootT est
class SteamInfoTest {

    @Value("${steam.key}")
    private String steamkey;
    SteamWebApiClient client = new SteamWebApiClient.SteamWebApiClientBuilder(steamkey).build();

    @Test
    public void SteamApiTest() throws SteamApiException {
        System.out.println(steamkey);

        GetPlayerSummariesRequest request = SteamWebApiRequestFactory.createGetPlayerSummariesRequest(Collections.singletonList("76561198045167898"));
        GetPlayerSummaries userInfo = client.processRequest(request);

        String userNickName = userInfo.getResponse().getPlayers().get(0).getPersonaname();
        String avatarUrl = userInfo.getResponse().getPlayers().get(0).getAvatarfull();

        SteamUser steamUser = new SteamUser();

        steamUser.setId(Long.valueOf("76561198045167898"));
        steamUser.setUserNickName(userNickName);
        steamUser.setAvatarUrl(avatarUrl);

        System.out.println(steamUser);

        assertEquals(Long.valueOf("76561198045167898"), steamUser.getId());


    }

}