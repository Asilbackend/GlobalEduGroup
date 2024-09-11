package org.example.globaledugroup.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Location;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendLocation;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.example.globaledugroup.bot.botDto.OperatorProjection;
import org.example.globaledugroup.entity.CodeUser;
import org.example.globaledugroup.entity.TelegramUser;
import org.example.globaledugroup.entity.User;
import org.example.globaledugroup.enums.BotRole;
import org.example.globaledugroup.enums.Role;
import org.example.globaledugroup.enums.TELEGRAM_STATE;
import org.example.globaledugroup.repository.CodeUserRepository;
import org.example.globaledugroup.repository.TelegramUserRepo;
import org.example.globaledugroup.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BotDataService {
    @Value("${bot.link}")
    String botLink;
    private final TelegramUserRepo telegramUserRepo;
    private final TelegramBot telegramBot;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CodeUserRepository codeUserRepository;

    public TelegramUser getTelegramUser(@NonNull com.pengrad.telegrambot.model.User from) {
        Optional<TelegramUser> telegramUserOpt = telegramUserRepo.findByChatId(from.id());
        if (telegramUserOpt.isEmpty()) {
            String botName = (from.firstName() == null ? "" : from.firstName()) + (from.lastName() == null ? "" : from.lastName());
            TelegramUser telegramUser = new TelegramUser(from.id(), botName, from.username(), BotRole.USER);
            telegramUserRepo.save(telegramUser);
            /// bu yerda sayt bolsa saytdan ulanadi...
//            UUID uuid = UUID.randomUUID();
//            userRepository.save(User.builder().phone(uuid + "").telegramUser(telegramUser).active(true).role(Role.ADMINISTRATOR).build());
            return telegramUser;
        } else {
            return telegramUserOpt.get();
        }
    }


    @Async
    public void setAndPutState(TelegramUser telegramUser, TELEGRAM_STATE state) {
        telegramUser.setTelegramState(state);
        telegramUserRepo.save(telegramUser);
    }

    private void sendLocationButton(TelegramUser telegramUser) {
        KeyboardButton locationButton = new KeyboardButton("Send Location").requestLocation(true);
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(locationButton);
        SendMessage request = new SendMessage(telegramUser.getChatId(), "Lokatsiya yuborish ")
                .replyMarkup(keyboardMarkup);
        telegramBot.execute(request);
    }


    public void receiveAndForwardLocation(Location location) {
        // Send the received location to the target chat
        SendLocation sendLocation = new SendLocation(1474065279, location.latitude(), location.longitude());
        telegramBot.execute(sendLocation);

        // Optionally, notify the user that their location has been forwarded
        SendMessage notification = new SendMessage(1474065279, "Your location has been sent.");
        telegramBot.execute(notification);
    }

    public String formatOperators(List<OperatorProjection> operators, Long chatId) {
        int i = 0;
        StringBuilder formattedOperators = new StringBuilder();
        for (OperatorProjection operator : operators) {
            if (i % 40 == 0) {
                SendMessage sendMessage = new SendMessage(chatId, formattedOperators.toString());
                telegramBot.execute(sendMessage);
                formattedOperators = new StringBuilder();
            }
            formattedOperators.append(formatOperator(operator, ++i))
                    .append("\n\n"); // Separate each operator with two new lines
        }
        return formattedOperators.toString();
    }

    public String formatOperator(OperatorProjection operator, int i) {
        return String.format(
                "➖➖%s➖➖➖➖➖➖\n" +
                        "\uD83D\uDC68\u200D\uD83C\uDFEB Menejer: %s %s\n" +
                        "\uD83D\uDCC8 O'rtacha koeffitsienti: %s\n" +
                        "\uD83D\uDC65 Baholagan userlar soni: %s\n" +
                        "\uD83D\uDCDE Telefon raqami: %s\n",
                i,
                operator.getFirstName(), operator.getLastName(),
                operator.getAverageRating(),
                operator.getCountTelegramUsers(),
                operator.getPhone()
        );
    }

    public String formatUser(User user) {
        return String.format(
                "\uD83D\uDC68\u200D\uD83C\uDFEB User: %s %s\n" +
                        "\uD83D\uDCDE Telefon raqami: %s\n" +
                        "Role: %s \n" +
                        "Active: %s\n",
                user.getFirstName(), user.getLastName(),
                user.getPhone(), user.getRole().name(), user.isActive() ? "true" : "false"
        );
    }

    public void sendSearchSticker(Long chatId) {
        SendMessage sendMessage = new SendMessage(chatId, "\uD83D\uDD0E");
        telegramBot.execute(sendMessage);
    }

    public String createLinkOffer(User user) {
        String code = UUID.randomUUID().toString();
        codeUserRepository.save(CodeUser.builder().code(code).user(user).build());
        return """
                📥 Taklif havolasi:
                                
                %s
                                
                🟢 Ushbu havolani faqat yangi qo'shmoqchi bolgan odamingiziga jo'nating.
                """.formatted("%s?start=".formatted(botLink) + code);
    }

    public User checkInvite(String startSpecific) {
        try {
            if (startSpecific.length() <= 6) {
                return null;
            }
            String data = startSpecific.substring(7);
            System.out.println("\nencoded: " + data + "\n\n");
            Optional<CodeUser> optionalUser = codeUserRepository.findByCode(data);
            CodeUser codeUser = optionalUser.get();
            codeUserRepository.deleteById(codeUser.getId());
            return codeUser.getUser();
        } catch (RuntimeException e) {
            return null;
        }
    }
}