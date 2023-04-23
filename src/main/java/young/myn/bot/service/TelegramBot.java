package young.myn.bot.service;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import young.myn.bot.config.BotConfig;
import young.myn.bot.config.ImagesConfig;
import young.myn.bot.emailSender.EmailSenderService;
import young.myn.bot.entities.Reservation;
import young.myn.bot.entities.Room;
import young.myn.bot.entities.User;
import young.myn.bot.enums.RegistrationStage;
import young.myn.bot.enums.RoomType;
import young.myn.bot.languages.*;
import young.myn.bot.service.widgets.Keyboards;
import young.myn.bot.service.widgets.calendar.Calendar;
import young.myn.bot.sms.Smsc;
import young.myn.bot.util.HibernateUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final EmailSenderService senderService;

    private final Keyboards keyboards;
    final BotConfig config;

    private final UserData userData;
    @Autowired
    public TelegramBot(BotConfig botConfig,UserData userData,Keyboards keyboards, EmailSenderService ess){
        this.config = botConfig;
        this.userData = userData;
        this.keyboards=keyboards;
        this.senderService = ess;
        this.userData.setLanguage(new EN());
        this.userData.setStage(RegistrationStage.ANONYMOUS);
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
            long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();
            switch (messageText) {
                case "/start":
                    chooseLanguage(chatId);
                    log.info("User " + update.getMessage().getChat().getUserName() + " started");
                    break;
                case "/rooms":
                    setRoomsPage(chatId);
                    break;
                case "/price":
                    showPrises(chatId);
                    break;
                case "/reservation":
                    showBooking(chatId);
                    break;
                default:
                    if (messageText.trim().length() == 4 && messageText.trim().matches("[0-9]{4}")
                            && userData.getVerificationCode().equals(messageText.trim())
                            && userData.getStage().equals(RegistrationStage.EMAIL_ENTERED)) {
                        userData.setStage(RegistrationStage.EMAIL_CONFIRMED);
                        try(Session session = HibernateUtil.getSession()){
                            session.beginTransaction();

                            User user = new User();
                            List<User> allUsers = session.createQuery("from User").list();
                            boolean userExist = false;
                            for(User i : allUsers){
                                if(i.getEmail().equals(userData.getEmail())){
                                    userExist = true;
                                    user = i;
                                }
                            }
                            if(!userExist){
                                user = new User();
                                user.setTelegramId(userData.getTelegramId());
                                user.setName(userData.getName());
                                user.setPhoneNumber(userData.getPhone());
                                user.setEmail(userData.getEmail());
                                session.persist(user);
                            }

                            Reservation reservation = new Reservation();
                            reservation.setUser(user);

                            List<Room> rooms = session.createQuery("from Room").list();
                            for(Room i:rooms){
                                if(i.getId()==Calendar.getFreeRoomsOfEachType().get(userData.getRoomType())){
                                    reservation.setRoom(i);
                                }
                            }

                            GregorianCalendar from = new GregorianCalendar();
                            from.setTime(Calendar.booking.get("FROM"));
                            reservation.setStartDate(from);

                            System.out.println(Calendar.booking.get("FROM").toString() + Calendar.booking.get("TO"));

                            GregorianCalendar to= new GregorianCalendar();
                            to.setTime(Calendar.booking.get("TO"));
                            reservation.setEndDate(to);

                            session.persist(reservation);

                            session.getTransaction().commit();
                        }

                        sendMessage(chatId, "THANK YOU!!! FUCK YOU");
                    } else if (messageText.matches("^\\+?[0-9\\-\\s]*$") && userData.getStage().equals(RegistrationStage.BOOKING_SELECTED)) {
                        userData.setStage(RegistrationStage.PHONE_ENTERED);
                        userData.setPhone(messageText);
                        confirmPhone(chatId, messageText);
                    } else if (userData.getStage().equals(RegistrationStage.BOOKING_SELECTED)){
                        userData.setName(messageText);
                        sendMessage(chatId,userData.getLanguage().getShareContactString(),null);
                    } else if (messageText.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                            "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$") && userData.getStage().equals(RegistrationStage.PHONE_ENTERED)) {
                        userData.setStage(RegistrationStage.EMAIL_ENTERED);
                        userData.setEmail(messageText.toLowerCase());
                        confirmEmail(chatId, messageText);
                    } else {
                        log.info("Command from user " + update.getMessage().getChat().getUserName() + " was not recognised");
                        sendMessage(chatId, userData.getLanguage().getNotRecognisedCommandString());
                    }
            }
        }
        else if(update.hasCallbackQuery()){
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            int messageId = update.getCallbackQuery().getMessage().getMessageId();
            switch (update.getCallbackQuery().getData()){
                case "ru":
                    userData.setLanguage(new RU());
                    createMenu(userData.getLanguage());
                    showMainMenu(chatId,update.getCallbackQuery().getMessage().getChat().getFirstName());
                    break;
                case "en":
                    userData.setLanguage(new EN());
                    createMenu(userData.getLanguage());
                    showMainMenu(chatId,update.getCallbackQuery().getMessage().getChat().getFirstName());
                    break;
                //sets the room selection menu
                case "rooms":
                    setRoomsPage(chatId,
                            messageId);
                    break;
                case "prices":
                    showPrises(chatId,messageId);
                    break;
                case "reserve":
                    showBooking(chatId,messageId);
                    break;
                case "contacts":
                    System.out.println(4);
                    break;
                case "help":
                    System.out.println(5);
                    break;
                case "single_room":
                    setRoom(chatId,messageId, ImagesConfig.singleRoomImage,userData.getLanguage().getSingleRoomDescription());
                    break;
                case "double_room":
                    setRoom(chatId,messageId, ImagesConfig.doubleRoomImage,userData.getLanguage().getDoubleEconomyRoomDescription());
                    break;
                case "double_room+":
                    setRoom(chatId,messageId, ImagesConfig.doublePlusRoomImage,userData.getLanguage().getDoubleComfortRoomDescription());
                    break;
                case "lux":
                    setRoom(chatId,messageId, ImagesConfig.luxRoomImage,userData.getLanguage().getLuxRoomDescription());
                    break;
                case "previous_month":
                    editMessage(chatId,messageId,null,Calendar.switchMonth(-1,userData.getLanguage()));
                    break;
                case "next_month":
                    editMessage(chatId,messageId,null,Calendar.switchMonth(1,userData.getLanguage()));
                    break;
                case "submit_booking":
                    userData.setTelegramId(update.getCallbackQuery().getMessage().getChat().getUserName());
                    editMessage(chatId,messageId,"in process....",null);
                    showAvailableRooms(chatId,messageId);
                    break;
                case "clear_booking":
                    Calendar.clearCalendarKeyboard(userData.getLanguage());
                    editMessage(chatId,messageId,null,Calendar.getCalendarKeyboard(userData.getLanguage()));
                    break;
                case "single_room_clicked":
                    showBookingConfirmationPage(chatId,messageId,RoomType.SINGLE);
                    break;
                case "double_room_clicked":
                    showBookingConfirmationPage(chatId,messageId,RoomType.DOUBLE);
                    break;
                case "double_plus_room_clicked":
                    showBookingConfirmationPage(chatId,messageId,RoomType.DOUBLE_PLUS);
                    break;
                case "lux_room_clicked":
                    showBookingConfirmationPage(chatId,messageId,RoomType.LUX);
                    break;
                case "phone_submitted":
                    emailRequest(chatId,messageId);
                    break;
                case "back_to_phone_input":
                    userData.setStage(RegistrationStage.BOOKING_SELECTED);
                    showBookingConfirmationPage(chatId,messageId,null);
                    break;
                case "back_to_main_menu":
                    backToMainMenu(chatId,messageId,update.getCallbackQuery()
                            .getMessage().getChat().getFirstName());
                    break;
                default:
                    if(update.getCallbackQuery().getData().matches("[0-9]+[0-9]+.+[0-9]+[0-9]+.+[0-9]+[0-9]+[0-9]+[0-9]")){
                        String date =update.getCallbackQuery().getData();
                        System.out.println(date);
                        if(Calendar.isBookingFull()){
                            Calendar.clearCalendarKeyboard(userData.getLanguage());
                            editMessage(chatId,messageId,null,Calendar.getCalendarKeyboard(userData.getLanguage()));
                        }
                        else if(Calendar.checkValidValue(date)){
                            InlineKeyboardMarkup keyboard = Calendar.getCalendarKeyboard(userData.getLanguage());
                            String day = (date.split("\\.")[0].equals("10")||date.split("\\.")[0].equals("20")
                                    ||date.split("\\.")[0].equals("30")) ? date.split("\\.")[0] :
                                    date.split("\\.")[0].replaceFirst("0","");
                            for(List<InlineKeyboardButton> i : keyboard.getKeyboard()){
                                for(InlineKeyboardButton j:i){
                                    if(j.getText().equals(day)){
                                        j.setText("$");
                                        editMessage(chatId,messageId,null,keyboard);
                                        break;
                                    }
                                }
                            }
                        }
                        if(Calendar.isBookingFull()){
                            InlineKeyboardMarkup keyboard = Calendar.getCalendarKeyboard(userData.getLanguage());

                            List<InlineKeyboardButton> submitRow = new ArrayList<>();
                            InlineKeyboardButton submit = new InlineKeyboardButton(userData.getLanguage().getSubmitBookingString());
                            submit.setCallbackData("submit_booking");
                            submitRow.add(submit);

                            List<List<InlineKeyboardButton>> newKeyboard = keyboard.getKeyboard();
                            newKeyboard.add(keyboard.getKeyboard().size()-2,submitRow);
                            editMessage(chatId,messageId,null,new InlineKeyboardMarkup(newKeyboard));
                        }//???
                        System.out.println(Calendar.booking.get("FROM"));
                        System.out.println(Calendar.booking.get("TO"));
                    }
            }
        }
    }
    private void confirmPhone(long chatId,String phone){
        //Smsc sms = new Smsc("Shyliko315on@gmail.com","ndsAkb_25YdxN39");
        //sms.send_sms("89050663812","Клубника бомба честно говоря",1, "", "", 0, "", "");
        sendMessage(chatId, String.format(userData.getLanguage().getConfirmPhoneString(), phone), keyboards.getConfirmPhoneKeyboard(userData.getLanguage()));
    }
    private void showAvailableRooms(Long chatId, Integer messageId){
        editMessage(chatId,messageId,userData.getLanguage().getAvailableRoomsString()+Calendar.getBookingDates(userData.getLanguage())
                ,Calendar.getAvailableRoomsKeyboard(userData.getLanguage()));
    }
    private void emailRequest(Long chatId, Integer messageId){
        editMessage(chatId,messageId,userData.getLanguage().getEmailRequestString(),null);
    }
    private void confirmEmail(Long chatId, String email){
        Random r = new Random();
        String randomNumber = String.format("%04d", r.nextInt(10000));
        userData.setVerificationCode(randomNumber);
        System.out.println(randomNumber);
        sendMessage(chatId,"Введите код подтверждения, высланный на "+ email);
        senderService.sendEmail(email.trim(),"Booking confirmation", randomNumber);
    }
    private void showMainMenu(Long chatId, String name){

        sendMessage(chatId,
                String.format(userData.getLanguage().getStartString(),name),keyboards.getMainMenuKeyboard(userData.getLanguage()));

    }
    private void backToMainMenu(Long chatId, Integer messageId, String name){
        showMainMenu(chatId,name);
        deleteMessage(chatId,messageId);
    }
    private void showBookingConfirmationPage(Long chatId, Integer messageId,RoomType roomType){

        userData.setStage(RegistrationStage.BOOKING_SELECTED);
        userData.setRoomType(roomType);
        try(Session session = HibernateUtil.getSession()){
            session.beginTransaction();
            List<User> users = session.createQuery("From User").list();
            boolean userExist = false;
            for(User user:users){
                if(user.getTelegramId().equals(userData.getTelegramId())){
                    userExist = true;
                }
            }
            if(userExist){
                editMessage(chatId,messageId,userData.getLanguage().getShareContactString(),null);
            }
            else{
                editMessage(chatId,messageId,userData.getLanguage().getNameRequest(),null);
            }
        }
    }
    //private void

    private void setRoomsPage(Long chatId, Integer messageId) {
        editMessage(chatId,messageId,userData.getLanguage().getRoomsMenuDescription(),keyboards.getRoomsKeyboard(userData.getLanguage()));
    }
    private void setRoomsPage(Long chatId){
        sendMessage(chatId,userData.getLanguage().getRoomsMenuDescription(),keyboards.getRoomsKeyboard(userData.getLanguage()));
    }
    private void setRoom(Long chatId, Integer messageId,String imgURL, String description){
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(String.valueOf(chatId));
        sendPhoto.setPhoto(new InputFile(new File(imgURL)));
        sendPhoto.setCaption(description);
        sendPhoto.setReplyMarkup(keyboards.getBackToRoomsKeyboard(userData.getLanguage()));
        try {
            execute(sendPhoto);
            deleteMessage(chatId,messageId);
        } catch (TelegramApiException e) {
            log.error("Error sending photo and text :"+e.getMessage());
        }

    }
    private void showBooking(long chatId,int messageId){
        Calendar.clearCalendarKeyboard(userData.getLanguage());
        editMessage(chatId, messageId,userData.getLanguage().getBookingStartString(),Calendar.getCalendarKeyboard(userData.getLanguage()));
    }
    private void showBooking(long chatId){
        Calendar.clearCalendarKeyboard(userData.getLanguage());
        sendMessage(chatId,userData.getLanguage().getBookingStartString(),Calendar.getCalendarKeyboard(userData.getLanguage()));
    }
    private void showPrises(long chatId,int messageId){
        editMessage(chatId,messageId,userData.getLanguage().getPrises(),keyboards.getBackToMainMenuKeyboard(userData.getLanguage()));
    }
    private void showPrises(long chatId){
        sendMessage(chatId,userData.getLanguage().getPrises(),keyboards.getBackToMainMenuKeyboard(userData.getLanguage()));
    }
    private void chooseLanguage(Long chatId) {
        sendMessage(chatId,"Please, select a language",keyboards.getLanguageKeyboard());
    }

    private void createMenu(Language language){
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/rooms", userData.getLanguage().getRoomsDescription()));
        listOfCommands.add(new BotCommand("/price", userData.getLanguage().getPriceDescription()));
        listOfCommands.add(new BotCommand("/reservation", userData.getLanguage().getReservationDescription()));
        listOfCommands.add(new BotCommand("/contacts", userData.getLanguage().getContactsDescription()));
        listOfCommands.add(new BotCommand("/help", userData.getLanguage().getHelpDescription()));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }
    }
    private void editMessage(long chatId,int messageId,String messageText, InlineKeyboardMarkup keyboard){
        if(messageText==null){
            EditMessageReplyMarkup editMessage = new EditMessageReplyMarkup();
            editMessage.setChatId(String.valueOf(chatId));
            editMessage.setMessageId(messageId);
            editMessage.setReplyMarkup(keyboard);
            try {
                execute(editMessage);
            } catch (TelegramApiException e) {
                log.error("Error editing message ReplyMarkup:" + e.getMessage());
            }
        }
        else {
            EditMessageText editMessage = new EditMessageText();
            editMessage.setChatId(String.valueOf(chatId));
            editMessage.setMessageId(messageId);
            editMessage.setText(messageText);
            editMessage.setReplyMarkup(keyboard);
            try {
                execute(editMessage);
            } catch (TelegramApiException e) {
                if (e.getMessage().equals("Error editing message text: [400] Bad Request: there is no text in the message to edit")) {
                    sendMessage(chatId, messageText, keyboard);
                    deleteMessage(chatId, messageId);
                } else {
                    log.error("Error editing message:" + e.getMessage());
                }
            }
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
        if(messageText==null)return;
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
    private void deleteMessage(Long chatId,int messageId){
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(String.valueOf(chatId));
        deleteMessage.setMessageId(messageId);
        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            log.error("Error deleting message:"+e.getMessage());
        }
    }
}
