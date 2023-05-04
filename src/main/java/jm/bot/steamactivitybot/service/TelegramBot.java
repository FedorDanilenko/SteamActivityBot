package jm.bot.steamactivitybot.service;

import jm.bot.steamactivitybot.config.BotConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
@Service
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig botConfig;

    public TelegramBot(BotConfig botConfig) {
        this.botConfig = botConfig;
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
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                        startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                        break;
                default:
                        sendMassage(chatId, "Whatever happens, happens.");
            }

        }

    }

    private void startCommandReceived(long chatId, String name) throws InterruptedException {

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
