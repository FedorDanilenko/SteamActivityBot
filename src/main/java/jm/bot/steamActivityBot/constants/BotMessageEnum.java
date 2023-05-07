package jm.bot.steamActivityBot.constants;

/**
 * Text messages sent by bot
 */

public enum BotMessageEnum {

    // commands description
    HELP_INFO ("This bot is designed to work with SteamAPI and get different statistics of Steam users and applications.\n\n " +
            "Commands:\n\n" + "/start - start bot.\n\n" +
            "/help - get a description of the bot and commands\n\n" +
            "/getSteamInfo - get steam user info by his Id");

    private String message;

    BotMessageEnum(String message) {
        this.message=message;
    }

    public String getMessage() {
        return message;
    }

}
