package jm.bot.steamActivityBot.telegramBot.service;

import jm.bot.steamActivityBot.entity.BotUser;
import jm.bot.steamActivityBot.repository.UserRepo;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;

@Slf4j
@Component
public class StartCommand extends BotCommand {

    @Autowired
    private UserRepo userRepo;

    public StartCommand(@Value("start") String commandIdentifier,@Value("start") String description) {
        super(commandIdentifier, description);
    }

    @SneakyThrows
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {

        registerUser(chat);
        startCommandReceived(absSender, chat.getId(), chat.getFirstName());

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

    private void startCommandReceived(AbsSender absSender,Long chatId, String name) throws InterruptedException {

        String answer = "Hi, " + name + ", you know who else like Telegram Bot?";

        sendMassage(absSender, chatId, answer);
        sendGif(absSender, chatId, "https://media.tenor.com/dti1tvshXAoAAAAd/muscle-man.gif");
        sendMassage(absSender, chatId, "MY MOM!!!");
        log.info("Replied to user " + name);

    }

    private void sendMassage(AbsSender absSender, Long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId))   ;
        message.setText(textToSend);

        try {
            absSender.execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }

    private void sendGif(AbsSender absSender, long chatId, String gifUrl) {
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

            absSender.execute(animation);

            // delete temporary file
            file.delete();
        } catch (IOException | TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }

}
