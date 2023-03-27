package young.myn.bot.languages;


import java.util.List;
import java.util.Map;

public abstract class Language {
    public abstract String getPriceDescription();
    public abstract String getRoomsDescription();
    public abstract String getReservationDescription();
    public abstract String getContactsDescription();
    public abstract String getHelpDescription();

    public abstract String getStartString();
    public abstract String getNotRecognisedCommandString();
    public abstract List<String> getMainMenuButtons();

    public abstract String getRoomsMenuDescription();

    public abstract List<String> getRoomsMenuButtons();
    public abstract String getPrises();
    public abstract String getBookingStartString();
    public abstract String getSingleRoomDescription();
    public abstract String getDoubleEconomyRoomDescription();
    public abstract String getDoubleComfortRoomDescription();
    public abstract String getLuxRoomDescription();
    public abstract Map<Integer, Map<String,Integer>> getAllMonthsWithDays();
    public abstract String getClearBookingString();
    public abstract String getBackString();
    public abstract String getSubmitBookingString();
    public abstract String getAvailableRoomsString();
}
