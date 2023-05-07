package jm.bot.steamActivityBot.telegramBot.components;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.List;

public interface BotCommands {

    List<BotCommand> BOT_COMMAND_LIST = List.of(
            new BotCommand("/start", "start bot"),
            new BotCommand("/help", "info how to use this bot"),
            new BotCommand("/getSteamUserInfo", "gives steam user data by his id")
    );

    String HELP_INFO = "This bot is designed to work with SteamAPI and get different statistics of Steam users and applications.\n\n " +
            "Commands:\n\n" + "/start - start bot.\n\n" +
            "/help - get a description of the bot and commands\n\n" +
            "/getSteamUserInfo - get steam user info by his Id";
}
