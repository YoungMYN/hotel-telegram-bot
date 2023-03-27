package young.myn.bot.service.widgets.calendar;

import org.hibernate.Session;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import young.myn.bot.entities.Reservation;
import young.myn.bot.entities.Room;
import young.myn.bot.languages.Language;
import young.myn.bot.util.HibernateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Calendar {
    public static HashMap<String, Date> booking = new HashMap<>();
    private static InlineKeyboardMarkup calendarKeyboard;
    public static InlineKeyboardMarkup getCalendarKeyboard(Language language){
        if(calendarKeyboard==null){
            calendarKeyboard = createNewCalendarKeyboard(language);
        }
        return calendarKeyboard;
    }
    public static void clearCalendarKeyboard(Language language){
        calendarKeyboard = createNewCalendarKeyboard(language);
    }
    private static InlineKeyboardMarkup createNewCalendarKeyboard(Language language){
        java.util.Calendar date = java.util.Calendar.getInstance();

        InlineKeyboardMarkup calendar = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = createCalendarKeyboard(
                String.valueOf(language.getAllMonthsWithDays().get(date.get(java.util.Calendar.MONTH)).keySet().toArray()[0])
                ,language);
        calendar.setKeyboard(rows);
        return calendar;
    }
    private static List<List<InlineKeyboardButton>> createCalendarKeyboard(String month,Language language){
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> firstRow = new ArrayList<>();

        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("<");
        back.setCallbackData("previous_month");
        InlineKeyboardButton forward = new InlineKeyboardButton();
        forward.setText(">");
        forward.setCallbackData("next_month");

        InlineKeyboardButton monthButton = new InlineKeyboardButton();
        monthButton.setText(month);
        monthButton.setCallbackData("m");

        firstRow.add(back);
        firstRow.add(monthButton);
        firstRow.add(forward);
        rows.add(firstRow);

        rows.addAll(generateDaysButtons(monthButton.getText(),language));

        List<InlineKeyboardButton> cleanRow = new ArrayList<>();

        InlineKeyboardButton cleanUp = new InlineKeyboardButton();
        cleanUp.setText(language.getClearBookingString());
        cleanUp.setCallbackData("clear_booking");
        cleanRow.add(cleanUp);
        rows.add(cleanRow);

        List<InlineKeyboardButton> lastRow = new ArrayList<>();

        InlineKeyboardButton backToMainMenu = new InlineKeyboardButton();
        backToMainMenu.setText(language.getBackString());
        backToMainMenu.setCallbackData("back_to_main_menu");

        lastRow.add(backToMainMenu);

        rows.add(lastRow);
        return rows;
    }
    private static List<List<InlineKeyboardButton>> generateDaysButtons(String month, Language language){
        List<List<InlineKeyboardButton>> days = new ArrayList<>();
        Integer quantityOfDays = null;
        for(Map i : language.getAllMonthsWithDays().values()){
            if(i.get(month)!= null){
                quantityOfDays = (int)i.get(month);
                break;
            }
        }
        int numberOfMonth = Integer.MIN_VALUE;
        for (int k = 0; k < 11; k++) {
            if(language.getAllMonthsWithDays().get(k).keySet().toArray()[0].equals(month)){
                numberOfMonth = k+1;
                break;
            }
        }
        System.out.println(quantityOfDays);
        java.util.Calendar instance = java.util.Calendar.getInstance();
        int year = (numberOfMonth>=instance.get(java.util.Calendar.MONTH)) ?
                instance.get(java.util.Calendar.YEAR) : instance.get(java.util.Calendar.YEAR)+1;

        ArrayList<InlineKeyboardButton> row = new ArrayList<>();
        for (int i = 0; i < quantityOfDays; i++) {
            InlineKeyboardButton j = new InlineKeyboardButton();
            j.setText(String.valueOf(i+1));
            j.setCallbackData(((i+1)<10?"0"+(i+1):(i+1))+"."+((numberOfMonth<10)?"0"+numberOfMonth:numberOfMonth) +"."+year);
            row.add(j);
            if((i+1)%7==0){
                days.add(row);
                row = new ArrayList<>();
            }
        }

        if(!row.isEmpty()&&row.size()!=7){
            int temp = row.size();
            for (int i = 0; i < 7- temp; i++) {
                InlineKeyboardButton j = new InlineKeyboardButton();
                j.setText(" ");
                j.setCallbackData("None");
                row.add(j);
            }
            days.add(row);
        }
        return days;
    }
    public static boolean checkValidValue(String stringDate){
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        Date date;
        try {
            date = format.parse(stringDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);//logs
        }
        if (date.before(java.util.Calendar.getInstance().getTime())){
            return false;
        }
        if(booking.get("FROM")==null){
            booking.put("FROM",date);
            return true;
        }
        if(booking.get("FROM").before(date)){
            booking.put("TO",date);
            return true;
        }
        return false;
    }
    public static boolean isBookingFull(){
        return booking.get("FROM") != null && booking.get("TO") != null;
    }
    public static InlineKeyboardMarkup switchMonth(int step,Language language){
        String currentMonth = calendarKeyboard.getKeyboard().get(0).get(1).getText();
        String previousMonth = null;
        String nextMonth = null;
        List<String> months = new ArrayList<>();
        for(Map<String,Integer> i :language.getAllMonthsWithDays().values()){
            months.add((String) i.keySet().toArray()[0]);
        }
        for (int i = 0; i < months.size(); i++) {
            if(months.get(i).equals(currentMonth)){
                if(i == months.size()-1){
                    previousMonth = months.get(months.size()-2);
                    nextMonth = months.get(0);
                }
                else if(i == 0){
                    nextMonth = months.get(1);
                    previousMonth = months.get(months.size()-1);
                }
                else{
                    previousMonth = months.get(i-1);
                    nextMonth = months.get(i+1);
                }
                break;
            }
        }
        InlineKeyboardMarkup calendar = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows =
                createCalendarKeyboard((step==1)?nextMonth:previousMonth,language);
        calendar.setKeyboard(rows);
        return calendar;

    }
    public static InlineKeyboardMarkup getAvailableRoomsKeyboard(){
        try(Session session = HibernateUtil.getSession()){
            session.beginTransaction();

            String sqlRooms = "From " + Room.class.getSimpleName();
            List<Room> rooms = session.createQuery(sqlRooms,Room.class).list();
            String sqlReservations = "From " + Reservation.class.getSimpleName();
            List<Reservation> reservations = session.createQuery(sqlReservations,Reservation.class).list();
            for(Room i:rooms){
                for(Reservation j: reservations){

                }
            }

        }
        return null;
    }
}
