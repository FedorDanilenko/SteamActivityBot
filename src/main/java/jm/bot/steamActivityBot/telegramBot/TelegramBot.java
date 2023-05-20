package jm.bot.steamActivityBot.telegramBot;

import jm.bot.steamActivityBot.SteamApi.service.SteamInfo;
import jm.bot.steamActivityBot.config.BotConfig;
import jm.bot.steamActivityBot.dto.steamUserDto.SteamUserAllInfo;
import jm.bot.steamActivityBot.dto.steamUserDto.SteamUserShortInfo;
import jm.bot.steamActivityBot.entity.BotUser;
import jm.bot.steamActivityBot.repository.SteamUserRepo;
import jm.bot.steamActivityBot.repository.UserRepo;
import jm.bot.steamActivityBot.telegramBot.components.BotCommands;
import jm.bot.steamActivityBot.telegramBot.components.SteamUserInfoButtons;
import jm.bot.steamActivityBot.telegramBot.service.PlotCreator;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import jakarta.persistence.EntityNotFoundException;
import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class TelegramBot extends TelegramLongPollingBot implements BotCommands {

    private final UserRepo userRepo;
    private final SteamInfo steamInfo;
    private final SteamUserRepo steamUserRepo;
    private static final Map<Long, String> waitingCommands = new HashMap<>();
    private static final Map<Long, String> waitingId = new HashMap<>();

    final BotConfig botConfig;


    @Autowired
    public TelegramBot(BotConfig botConfig, UserRepo userRepo, SteamInfo steamInfo, SteamUserRepo steamUserRepo) {
        this.botConfig = botConfig;
        this.userRepo = userRepo;
        this.steamInfo = steamInfo;
        this.steamUserRepo = steamUserRepo;
        try {
            this.execute(new SetMyCommands(BOT_COMMAND_LIST, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(@NonNull Update update) {
        long chatId;
        long messageId;
        Chat chat;
        String receivedMessage;

        // if text message
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            chat = update.getMessage().getChat();
            if (update.getMessage().hasText()) {
                receivedMessage = update.getMessage().getText();
                botAnswerUtil(receivedMessage, chatId, chat);
            }

            // if click buttons
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            messageId = update.getCallbackQuery().getMessage().getMessageId();
            chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackData.equals("short user info")) {
                SteamUserShortInfo steamUserInfo = steamInfo.getShortUserInfo(waitingId.remove(chatId));
                String answer = String.valueOf(steamUserInfo);
                executeEditMessageText(answer, chatId, messageId);
                log.info("get short info user " + chatId + " about steamuser " + steamUserInfo.getId());
            } else if (callbackData.equals("all user info")) {
                SteamUserAllInfo steamUserInfo = steamInfo.getAllUserInfo(waitingId.remove(chatId)); //76561198045167898
                String answer = String.valueOf(steamUserInfo);
                executeEditMessageText(String.valueOf(answer), chatId, messageId);
                log.info("get All info user " + chatId + " about steamuser " + steamUserInfo.getId());

            }
        }
    }

    @SneakyThrows
    private void botAnswerUtil(String messageText, long chatId, Chat chat) {
        // check waiting command in memory
        if (waitingCommands.containsKey(chatId)) {
            switch (waitingCommands.get(chatId)) {
                case "/getsteamuserinfo":
                    waitingId.put(chatId,messageText);
                    waitingCommands.remove(chatId);
                    // check id is number
                    if (messageText.matches("\\d+")) {
                        getSteamUserInfo(chatId);
                        return;
                    }
                case "/getuseractivity":
                    waitingCommands.remove(chatId);
                    if (messageText.matches("\\d+")) {
                        prepareAndSendMessage(chatId, "Scanning your games");
                        prepareAndSendMessage(chatId, "wait");
                        Map<LocalDate,Integer> ach = steamInfo.getUserActivity(messageText);
                        String steamUserName = steamUserRepo.findById(Long.valueOf(messageText)).orElseThrow(() ->
                                new EntityNotFoundException("Steam user not found")).getUserNickName();
                        prepareAndSendMessage(chatId, "Steam Activity for user " + steamUserName);
                        sendFile(chatId, PlotCreator.createPlotPng(ach));
                        return;
                    }
            }
        }

        // commands for admin
        if (messageText.contains("/sendAll") && botConfig.getOwnerId() == chatId) {
            var textToSend = messageText.substring(messageText.indexOf(" "));
            var users = userRepo.findAll();
            for (BotUser user : users) {
                prepareAndSendMessage(user.getChatId(), textToSend);
                log.info("Send text all users: " + textToSend);
            }
        } else {
            switch (messageText) {
                case "/start" -> {
                    registerUser(chat);
                    startCommandReceived(chatId, chat.getFirstName());
                }
                case "/help" -> {
                    prepareAndSendMessage(chatId, HELP_INFO);
                    log.info("send help info user: " + chatId);
                }
                case "/getsteamuserinfo", "/getuseractivity" -> getSteamUserId(chatId, messageText);
                default -> {
                    prepareAndSendMessage(chatId, "Oh No Bro! I don't know this command.");
                    log.info("Unexpected message: " + messageText);
                }
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


    private void getSteamUserId(long chatId, String messageText) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Enter steam user ID");
        executeMessage(message);
        waitingCommands.put(chatId, messageText);
        log.info("sent a request to get a user steam user id: " + chatId);
    }


    private void getSteamUserInfo(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("How much information to give out?");

        message.setReplyMarkup(SteamUserInfoButtons.inlineMarkup());

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
            log.info("User saved in database:" + user);
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

    private void sendFile(long chatId, File file) throws TelegramApiException {
        SendDocument document = new SendDocument();
        document.setChatId(String.valueOf(chatId));
        document.setDocument(new InputFile(file));
        execute(document);
        file.delete();
    }

}
