package jm.bot.steamActivityBot.SteamApi.service;

import jakarta.ws.rs.core.Application;
import jm.bot.steamActivityBot.entity.SteamApp;
import jm.bot.steamActivityBot.entity.SteamUser;
import jm.bot.steamActivityBot.repository.SteamAppRepo;
import jm.bot.steamActivityBot.repository.SteamUserRepo;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = Application.class)
@TestPropertySource(
        locations = "classpath:testApplication.properties")
public class SteamInfoTest {

    private SteamUserRepo steamUserRepo;
    private SteamAppRepo steamAppRepo;

    {
        steamUserRepo.save(SteamUser.builder()
                .build());
    }

    @Test
    public void testDataBase() {


    }

}