package jm.bot.steamActivityBot.telegramBot.components;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

public class SteamUserInfoButtons {

    private static final InlineKeyboardButton SHOT_INFO = new InlineKeyboardButton("Short");
    private static final InlineKeyboardButton ALL_INFO = new InlineKeyboardButton("All");

    public static InlineKeyboardMarkup inlineMarkup() {
        SHOT_INFO.setCallbackData("short user info");
        ALL_INFO.setCallbackData("all user info");

        List<InlineKeyboardButton> rowInline = List.of(SHOT_INFO, ALL_INFO);
        List<List<InlineKeyboardButton>> rowsInLine = List.of(rowInline);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(rowsInLine);

        return markupInline;
    }
}
