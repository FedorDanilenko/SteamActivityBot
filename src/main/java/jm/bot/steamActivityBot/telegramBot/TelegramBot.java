package jm.bot.steamActivityBot.telegramBot;

import jm.bot.steamActivityBot.config.BotConfig;
import jm.bot.steamActivityBot.entity.BotUser;
import jm.bot.steamActivityBot.repository.UserRepo;
import jm.bot.steamActivityBot.telegramBot.service.StartCommand;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepo userRepo;
    final BotConfig botConfig;

    static final String HELP_INFO = "This bot is designed to work with SteamAPI and get different statistics of Steam users and applications.\n\n " +
            "Commands:\n" + "/start - start bot.";


    public TelegramBot(BotConfig botConfig) {
        this.botConfig = botConfig;
        try {
            List<BotCommand> listOfCommands = new ArrayList<>();
            listOfCommands.add(new BotCommand("/start", "start bot and get welcome massage"));
            listOfCommands.add(new BotCommand("/mydata", "get your data"));
            listOfCommands.add(new BotCommand("/deletedata", "delete all your data"));
            listOfCommands.add(new BotCommand("/help", "info how to use this bot"));
            listOfCommands.add(new BotCommand("/settings", "set your preferences"));
            listOfCommands.add(new BotCommand("/getAchActivity", "get user Achievements Activity"));
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
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

            switch (messageText) {
                case "/start":

                    registerUser(update.getMessage().getChat());

                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/help":
                    sendMassage(chatId, HELP_INFO);
                    break;
                default:
                    sendMassage(chatId, "Whatever happens, happens.");
            }

        }

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
            log.info("user saved:" + user);
        }
    }

    private void startCommandReceived(Long chatId, String name) throws InterruptedException {

        String answer = "Hi, " + name + ", you know who else like Telegram Bot?";

        sendMassage(chatId, answer);
        sendGif(chatId, "https://media.tenor.com/dti1tvshXAoAAAAd/muscle-man.gif");
        sendMassage(chatId, "MY MOM!!!");
        log.info("Replied to user " + name);

    }


    private void sendMassage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }

    private void sendGif(long chatId, String gifUrl) {
        SendAnimation animation = new SendAnimation();
        animation.setChatId(String.valueOf(chatId));

        try {
            // Download gif
            URL url = new URL(gifUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            InputStream inputStream = httpURLConnection.getInputStream();
            File file = new File("muscle-man.gif");
            FileUtils.copyInputStreamToFile(inputStream, file);

            // Create InputFile
            InputFile inputFile = new InputFile(file);

            animation.setAnimation(inputFile);

            execute(animation);

            // delete temporary file
            file.delete();
        } catch (IOException | TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }

}
