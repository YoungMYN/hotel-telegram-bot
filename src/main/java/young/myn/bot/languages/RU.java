package young.myn.bot.languages;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class RU extends Language{
    private final String startString = "Здравствуй, %s! Приветствуем тебя в нашем отеле! !\n" +
            "Этот бот поможет тебе забронировать комнату, покажет доступные даты, а также наши цены и много чего еще.";
    private final String notRecognisedCommandString = "К сожалению, я не понимаю такую команду...\n"+
            " Пожалуйста, выберите элемент из меню.";

    private final String priceDescription = "цены на наши номера";
    private final String roomsDescription ="описание и фото наших номеров";
    private final String reservationDescription ="свободные даты/бронь";
    private final String contactsDescription = "связаться с нами";
    private final String helpDescription ="руководство к пользованию ботом";
    private final List<String> mainMenuButtons = Arrays.asList("Виды номеров","Цены","Бронирование",
            "Контакты","Помощь");
    private final String roomsMenuDescription = "Выберите комнату";
    private final List<String> roomsMenuButtons = Arrays.asList("Одноместный номер","Двухместный эконом",
            "Двухместный комфорт","Президентский люкс","<- Назад");
    private final String singleRoomDescription = "Небольшой, но очень уютный номер (23м²) с отдельным санузлом";
    private final String doubleEconomyRoomDescription="Небольшой номер на двоих с совмещенной кроватью.";
    private final String doubleComfortRoomDescription="Номер на двоих повышенной комфортности с увеличенной площадью," +
            " удобствами и прекрасным панорамным видом.";
    private final String luxRoomDescription="Роскошный королевский номер площадью более 150м²," +
            " с отдельной столовой, лоджией и личным лифтом.";
    private final String backString = "<- Назад";
    private final String prises = "Цены на наши номера:\n" +
            "Одноместный – 2 000 рублей/сутки\n" +
            "Двухместный эконом  –  3 000 рублей/сутки\n" +
            "Двухместный комфорт – 3 800 рублей/сутки\n" +
            "Президентский люкс – 20 000 рублей/сутки\n";
}
