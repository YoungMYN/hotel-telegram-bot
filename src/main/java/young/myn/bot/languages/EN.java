package young.myn.bot.languages;

import lombok.Data;

import java.util.*;

@Data
public class EN extends Language {
    private Map<Integer, Map<String,Integer>> allMonthsWithDays = new TreeMap<>();
    {

        allMonthsWithDays.put(0,new HashMap<>(){{
            put("JANUARY",31);
        }
        });
        allMonthsWithDays.put(1,new HashMap<>(){{
            put("FEBRUARY",28);
        }
        });
        allMonthsWithDays.put(2,new HashMap<>(){{
            put("MARCH",31);
        }
        });
        allMonthsWithDays.put(3,new HashMap<>(){{
            put("APRIL",30);
        }
        });
        allMonthsWithDays.put(4,new HashMap<>(){{
            put("MAY",31);
        }
        });
        allMonthsWithDays.put(5,new HashMap<>(){{
            put("JUNE",30);
        }
        });
        allMonthsWithDays.put(6,new HashMap<>(){{
            put("JULY",31);
        }
        });
        allMonthsWithDays.put(7,new HashMap<>(){{
            put("AUGUST",31);
        }
        });
        allMonthsWithDays.put(8,new HashMap<>(){{
            put("SEPTEMBER",30);
        }
        });
        allMonthsWithDays.put(9,new HashMap<>(){{
            put("OCTOBER",31);
        }
        });
        allMonthsWithDays.put(10,new HashMap<>(){{
            put("NOVEMBER",30);
        }
        });
        allMonthsWithDays.put(11,new HashMap<>(){{
            put("DECEMBER",31);
        }
        });
    }
    private final String priceDescription = "current prices for our rooms";
    private final String roomsDescription ="description&photos of our rooms";
    private final String reservationDescription ="calendar of available rooms/booking";
    private final String contactsDescription = "ways to contact us";
    private final String helpDescription ="detailed instructions for using the bot";

    private final String startString = "Hi, %s! Welcome to Holiday Hotel!\n" +
            "This bot will help you book a room, show available dates, as well as our prices and much more.";
    private final String notRecognisedCommandString = "Unfortunately, I don't understand the command.\n"+
            " Please select an item from the menu.";
    private final List<String> mainMenuButtons = Arrays.asList("Room types","Prices","Booking",
            "Contacts","Help");
    private final String roomsMenuDescription = "Choose a room";
    private final List<String> roomsMenuButtons = Arrays.asList("Single room","Double room \"Economy\"",
            "Double room \"Comfort\"","President Lux","<- Back");
    private final String singleRoomDescription = "Small but very cozy room (23m²) with private bathroom";
    private final String doubleEconomyRoomDescription="Small room for two with a combined bed.";
    private final String doubleComfortRoomDescription="Superior double room with increased area, " +
            "amenities and beautiful panoramic views.";
    private final String luxRoomDescription="Luxurious royal room of over 150m² with separate dining room," +
            " loggia and private elevator.";
    private final String backString = "<- Back";
    private final String prises = "Prices for our rooms:\n" +
            "Single - 2,000 rubles / day\n" +
            "Double economy - 3,000 rubles / day\n" +
            "Double comfort - 3 800 rubles / day\n" +
            "Presidential suite - 20,000 rubles / day  \n";
    private final String bookingStartString = "Select your planned arrival and departure dates:";
    private final String clearBookingString = "Clear";
    private final String submitBookingString = "Submit";
    private final String shareContactString = "We need your contact information to confirm your booking.\n " +
            "Please send your phone number:";
    private final String availableRoomsString = "Rooms available for the selected dates:";
    private final String confirmPhoneString = "Please double check your phone number. \n" +
            "%s will be called by our administrator shortly to confirm the booking";
    private final String emailRequestString = "Please enter your email address:";
    private final String nameRequest = "What is your name?";
}
