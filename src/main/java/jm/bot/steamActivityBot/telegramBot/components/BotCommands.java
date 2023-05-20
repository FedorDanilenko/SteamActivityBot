package jm.bot.steamActivityBot.telegramBot.components;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.List;

public interface BotCommands {

    List<BotCommand> BOT_COMMAND_LIST = List.of(
            new BotCommand("/start", "start bot"),
            new BotCommand("/help", "info how to use this bot"),
            new BotCommand("/getsteamuserinfo", "gives steam user data by his id"),
            new BotCommand("/getuseractivity", "gives graph of user activity")
    );

    String HELP_INFO = """
            This bot is designed to work with SteamAPI and get different statistics of Steam users and applications.

             Commands:

            /start - start bot.

            /help - get a description of the bot and commands

            /getsteamuserinfo - get steam user info by his Id

            /getuseractivity - get steam user achievements activity""";
}
