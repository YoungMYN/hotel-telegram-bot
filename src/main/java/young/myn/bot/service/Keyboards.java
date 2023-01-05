package young.myn.bot.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import young.myn.bot.languages.Language;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Keyboards {
    public InlineKeyboardMarkup getLanguageKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton ruButton = new InlineKeyboardButton();
        ruButton.setText("RU");
        ruButton.setCallbackData("ru");

        InlineKeyboardButton enButton = new InlineKeyboardButton();
        enButton.setText("EN");
        enButton.setCallbackData("en");

        row.add(enButton);
        row.add(ruButton);

        rows.add(row);

        markup.setKeyboard(rows);
        return markup;
    }
    public InlineKeyboardMarkup getMainMenuKeyboard(Language language) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<String> callBackData = Arrays.asList("rooms", "prices", "reserve", "contacts","help");
        for (int i = 0; i < 5; i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(language.getMainMenuButtons().get(i));
            button.setCallbackData(callBackData.get(i));
            row.add(button);
            rows.add(row);
        }
        markup.setKeyboard(rows);
        return markup;
    }

    public InlineKeyboardMarkup getRoomsKeyboard(Language language) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<String> callBackData = Arrays.asList("single_room", "double_room", "double_room+", "lux","back_to_main_menu");
        for (int i = 0; i < 3; i+=2) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(language.getRoomsMenuButtons().get(i));
            button.setCallbackData(callBackData.get(i));
            row.add(button);

            InlineKeyboardButton button2 = new InlineKeyboardButton();
            button2.setText(language.getRoomsMenuButtons().get(i+1));
            button2.setCallbackData(callBackData.get(i+1));
            row.add(button2);

            rows.add(row);
        }
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText(language.getRoomsMenuButtons().get(4));
        backButton.setCallbackData(callBackData.get(4));
        row.add(backButton);
        rows.add(row);

        markup.setKeyboard(rows);
        return markup;
    }
    public InlineKeyboardMarkup getBackToRoomsKeyboard(Language language) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(language.getBackString());
        button.setCallbackData("back_to_rooms");
        row.add(button);

        rows.add(row);

        markup.setKeyboard(rows);
        return markup;
    }
}
