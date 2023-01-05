package young.myn.bot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import young.myn.bot.config.BotConfig;
import young.myn.bot.languages.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    private Language language;
    private Keyboards keyboards;
    final BotConfig config;

    public TelegramBot(BotConfig botConfig){
        this.config = botConfig;
        language = new EN();
        keyboards = new Keyboards();

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
                    chooseLanguage(update.getMessage().getChatId());
                    log.info("User "+ update.getMessage().getChat().getUserName()+" started");
                    break;
                default:
                    log.info("Command from user "+update.getMessage().getChat().getUserName()+" was not recognised");
                    sendMessage(update.getMessage().getChatId(),language.getNotRecognisedCommandString());
                    break;
            }
        }
        else if(update.hasCallbackQuery()){
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            int messageId = update.getCallbackQuery().getMessage().getMessageId();
            switch (update.getCallbackQuery().getData()){
                case "ru":
                    language = new RU();
                    createMenu(language);
                    sendMessage(chatId,
                            String.format(language.getStartString(),update.getCallbackQuery()
                                    .getMessage().getChat().getFirstName()),keyboards.getMainMenuKeyboard(language));
                    break;
                case "en":
                    language = new EN();
                    createMenu(language);
                    sendMessage(chatId,
                            String.format(language.getStartString(),update.getCallbackQuery()
                                    .getMessage().getChat().getFirstName()),keyboards.getMainMenuKeyboard(language));
                    break;
                case "rooms":
                    System.out.println(1);
                    setRoomsPage(chatId,
                            messageId);
                    break;
                case "prices":
                    System.out.println(2);
                    break;
                case "reserve":
                    System.out.println(3);
                    break;
                case "contacts":
                    System.out.println(4);
                    break;
                case "help":
                    System.out.println(5);
                    break;
                case "single_room":
                    setRoom(chatId,messageId,"src\\main\\resources\\images\\ph.jpg",language.getSingleRoomDescription());
                    break;
                case "double_room":
                    break;
                case "double_room+":
                    break;
                case "lux":
                    break;
                case "back_to_main_menu":
                    break;
            }
        }
    }

    private void setRoomsPage(Long chatId, Integer messageId) {
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(String.valueOf(chatId));
        editMessage.setMessageId(messageId);
        editMessage.setText(language.getRoomsMenuDescription());
        editMessage.setReplyMarkup(keyboards.getRoomsKeyboard(language));
        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void setRoom(Long chatId, Integer messageId,String imgURL, String description){
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(String.valueOf(chatId));
        deleteMessage.setMessageId(messageId);

        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(String.valueOf(chatId));
        sendPhoto.setPhoto(new InputFile(new File(imgURL)));
        sendPhoto.setCaption(description);
        sendPhoto.setReplyMarkup(keyboards.getBackToRoomsKeyboard(language));
        try {
            execute(sendPhoto);
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            log.error("Error sending photo and text :"+e.getMessage());
        }

    }

    private void chooseLanguage(Long chatId) {
        if(chatId==null) return;
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("Please, select a language");
        sendMessage.setReplyMarkup(keyboards.getLanguageKeyboard());
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error choosing language:"+e.getMessage());
        }
    }


    private void createMenu(Language language){
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/rooms", language.getRoomsDescription()));
        listOfCommands.add(new BotCommand("/price", language.getPriceDescription()));
        listOfCommands.add(new BotCommand("/reservation", language.getReservationDescription()));
        listOfCommands.add(new BotCommand("/contacts", language.getContactsDescription()));
        listOfCommands.add(new BotCommand("/help", language.getHelpDescription()));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }
    }

    private void sendMessage(Long chatId, String messageText){
        if(chatId==null||messageText==null){
            return;
        }
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(messageText);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error sending message: "+e.getMessage());
        }
    }
    private void sendMessage(Long chatId, String messageText,InlineKeyboardMarkup keyboard){
        if(chatId==null||messageText==null){
            return;
        }
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(messageText);
        sendMessage.setReplyMarkup(keyboard);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error sending message:"+e.getMessage());
        }
    }
}
