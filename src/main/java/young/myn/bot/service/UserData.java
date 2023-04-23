package young.myn.bot.service;


import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import young.myn.bot.enums.RegistrationStage;
import young.myn.bot.enums.RoomType;
import young.myn.bot.languages.Language;

@Component
@Data
public class UserData {
    private String telegramId;
    private String verificationCode;
    private RegistrationStage stage;
    private Language language;
    private RoomType roomType;
    private String name;
    private String phone;
    private String email;

}
