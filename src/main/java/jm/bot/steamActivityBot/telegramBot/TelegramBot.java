package jm.bot.steamActivityBot.telegramBot;

import jm.bot.steamActivityBot.SteamApi.service.SteamInfo;
import jm.bot.steamActivityBot.config.BotConfig;
import jm.bot.steamActivityBot.constants.BotMessageEnum;
import jm.bot.steamActivityBot.constants.MenuList;
import jm.bot.steamActivityBot.dto.steamUserDto.SteamUserShortInfo;
import jm.bot.steamActivityBot.entity.BotUser;
import jm.bot.steamActivityBot.entity.SteamUser;
import jm.bot.steamActivityBot.repository.UserRepo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TelegramBot extends TelegramLongPollingBot {

    private final UserRepo userRepo;
    private final SteamInfo steamInfo;
    private final MenuList menuList;
    final BotConfig botConfig;

    static final String SHOT_INFO="SHOT_INFO_USER";
    static final String ALL_INFO = "All_INFO_USER";


    @Autowired
    public TelegramBot(BotConfig botConfig, UserRepo userRepo, SteamInfo steamInfo, MenuList menuList) {
        this.botConfig = botConfig;
        this.userRepo=userRepo;
        this.steamInfo=steamInfo;
        this.menuList=menuList;
        try {
            this.execute(new SetMyCommands(menuList.getListOfCommands(), new BotCommandScopeDefault(), null));
//            register(new StartCommand("start", "start"));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

//    @Override
//    public void processNonCommandUpdate(Update update) {
//        Long chatId = update.getMessage().getChatId();
//
//        sendMassage(chatId, "Whatever happens, happens.");
//
//    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.contains("/send") && botConfig.getOwnerId() == chatId) {
                var textToSend = messageText.substring(messageText.indexOf(" "));
                var users = userRepo.findAll();
                for (BotUser user : users) {
                    prepareAndSendMessage(user.getChatId(), textToSend);
                    log.info("Send text all users: " + textToSend);
                }
            }
            else {
                switch (messageText) {
                    case "/start":

                        registerUser(update.getMessage().getChat());

                        startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                        break;
                    case "/help":
                        prepareAndSendMessage(chatId, BotMessageEnum.HELP_INFO.getMessage());
                        log.info("send help info user: " + chatId);
                        break;

                    case "/getSteamInfo":

                        getSteamUserInfo(chatId, update);

                        break;
                    default:
                        prepareAndSendMessage(chatId, "Oh No Bro!");
                }
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();

            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackData.equals(SHOT_INFO)) {
                SteamUserShortInfo steamUserInfo = steamInfo.getShortUserInfo("76561198045167898");
                String answer = String.valueOf(steamUserInfo);
                executeEditMessageText(answer, chatId, messageId);
                log.info("get short info user " + chatId + " about steamuser " + steamUserInfo.getId());
            } else if (callbackData.equals(ALL_INFO)) {
                SteamUser steamUserInfo = steamInfo.getAllUserInfo("76561198045167898");
                String answer = String.valueOf(steamUserInfo);
                executeEditMessageText(String.valueOf(answer),chatId,messageId);
                log.info("get All info user " + chatId + " about steamuser " + steamUserInfo.getId());

            }
        }
    }

    private void executeEditMessageText(String answer, Long chatId, long messageId) {
        EditMessageText messageText = new EditMessageText();
        messageText.setChatId(String.valueOf(chatId));
        messageText.setText(answer);
        messageText.setMessageId((int) messageId);
        try {
            execute(messageText);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }


    private void getSteamUserInfo(Long chatId, Update update) {



        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("How much information to give out?");

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        List<InlineKeyboardButton> rowInLine = new ArrayList<>();

        var shortInfoButton = new InlineKeyboardButton();
        shortInfoButton.setText("shot");
        shortInfoButton.setCallbackData(SHOT_INFO);

        var allInfoButton = new InlineKeyboardButton();
        allInfoButton.setText("all");
        allInfoButton.setCallbackData(ALL_INFO);

        rowInLine.add(shortInfoButton);
        rowInLine.add(allInfoButton);

        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);

        message.setReplyMarkup(markupInLine);

        executeMessage(message);
        log.info("send command getSteamInfo user: " + chatId);

    }


    private void registerUser(Chat chat) {
        if (userRepo.findById(chat.getId()).isEmpty()) {
            var chatId = chat.getId();

            BotUser user = new BotUser();

            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setStartTime(new Timestamp(System.currentTimeMillis()));

            userRepo.save(user);
            log.info("User saved:" + user);
        }
    }

    private void startCommandReceived(Long chatId, String name) throws InterruptedException, TelegramApiException {

        String answer = "Hi, " + name + ", you know who else want to get some steam statistics?";

        prepareAndSendMessage(chatId, answer);
        Thread.sleep(2500);
        sendGif(chatId, "https://media.tenor.com/dti1tvshXAoAAAAd/muscle-man.gif"); // "CgACAgQAAxkBAAEgu61kVorN56RKTm4FyMaPG0CItmPQIgAC7gIAAnSurVHvaRDgfnDS7C8E"
        prepareAndSendMessage(chatId, "MY MOM!!!");
        log.info("Replied to user " + name);

    }


    private void sendMassage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("/getSteamInfo");

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);

        message.setReplyMarkup(keyboardMarkup);

        executeMessage(message);

    }

    private void sendGif(long chatId, String fileId) throws TelegramApiException {
        SendAnimation animation = new SendAnimation();
        animation.setChatId(String.valueOf(chatId));
        animation.setAnimation(new InputFile(fileId));
        execute(animation);
    }

    private void executeMessage(SendMessage msg) {
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }

    }

    private void prepareAndSendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);

    }

}
