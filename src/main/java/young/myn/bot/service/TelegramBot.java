package young.myn.bot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import young.myn.bot.config.BotConfig;
import young.myn.bot.languages.RU;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    final BotConfig config;

    public TelegramBot(BotConfig botConfig){
        this.config = botConfig;
    }
    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            switch (messageText){
                case "/start":
                    log.info("User "+ update.getMessage().getChat().getUserName()+" started");
                    sendMessage(update.getMessage().getChatId(),
                            String.format(RU.START_STRING,update.getMessage().getChat().getFirstName()));
                    break;
                default:
                    log.info("Command from user "+update.getMessage().getChat().getUserName()+" was not recognised");
                    sendMessage(update.getMessage().getChatId(),RU.NOT_RECOGNISED_COMMAND);
            }
        }
    }
    private void sendMessage(Long chatId, String message){
        if(chatId==null||message==null){
            return;
        }
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
