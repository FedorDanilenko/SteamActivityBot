package jm.bot.steamActivityBot.constants;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class MenuList {

    private List<BotCommand> listOfCommands;

    public MenuList() {
        listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "start bot and get welcome massage"));
        listOfCommands.add(new BotCommand("/help", "info how to use this bot"));
        listOfCommands.add(new BotCommand("/getSteamInfo", "gives steam user data by his id"));
    }

    public List<BotCommand> getListOfCommands() {
        return listOfCommands;
    }
}