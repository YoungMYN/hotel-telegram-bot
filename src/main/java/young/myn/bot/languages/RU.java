package young.myn.bot.languages;

import lombok.Data;

import java.util.*;

@Data
public class RU extends Language{
    private Map<Integer, Map<String,Integer>> allMonthsWithDays = new TreeMap<>();
    {
        allMonthsWithDays.put(0,new HashMap<>(){{
            put("ЯНВАРЬ",31);
        }
        });
        allMonthsWithDays.put(1,new HashMap<>(){{
            put("ФЕВРАЛЬ",28);
        }
        });
        allMonthsWithDays.put(2,new HashMap<>(){{
            put("МАРТ",31);
        }
        });
        allMonthsWithDays.put(3,new HashMap<>(){{
            put("АПРЕЛЬ",30);
        }
        });
        allMonthsWithDays.put(4,new HashMap<>(){{
            put("МАЙ",31);
        }
        });
        allMonthsWithDays.put(5,new HashMap<>(){{
            put("ИЮНЬ",30);
        }
        });
        allMonthsWithDays.put(6,new HashMap<>(){{
            put("ИЮЛЬ",31);
        }
        });
        allMonthsWithDays.put(7,new HashMap<>(){{
            put("АВГУСТ",31);
        }
        });
        allMonthsWithDays.put(8,new HashMap<>(){{
            put("СЕНТЯБРЬ",30);
        }
        });
        allMonthsWithDays.put(9,new HashMap<>(){{
            put("ОКТЯБРЬ",31);
        }
        });
        allMonthsWithDays.put(10,new HashMap<>(){{
            put("НОЯБРЬ",30);
        }
        });
        allMonthsWithDays.put(11,new HashMap<>(){{
            put("ДЕКАБРЬ",31);
        }
        });
    }
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
    private final String bookingStartString = "Выберите планируемые даты заезда и отъезда:";
    private final String clearBookingString = "Очистить";
    private final String submitBookingString = "Подтвердить";
    private final String availableRoomsString = "Номера, свободные на выбранные даты:";

}
