package jm.bot.steamActivityBot.telegramBot;

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
        listOfCommands.add(new BotCommand("/mydata", "get your data"));
        listOfCommands.add(new BotCommand("/deletedata", "delete all your data"));
        listOfCommands.add(new BotCommand("/help", "info how to use this bot"));
        listOfCommands.add(new BotCommand("/settings", "set your preferences"));
    }

    public List<BotCommand> getListOfCommands() {
        return listOfCommands;
    }
}
