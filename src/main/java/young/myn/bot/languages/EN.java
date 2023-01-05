package young.myn.bot.languages;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class EN extends Language {

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
}
