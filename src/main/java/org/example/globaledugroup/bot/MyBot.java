package org.example.globaledugroup.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.example.globaledugroup.entity.Operator;
import org.example.globaledugroup.entity.TelegramUser;
import org.example.globaledugroup.entity.User;
import org.example.globaledugroup.enums.BotRole;
import org.example.globaledugroup.enums.Gender;
import org.example.globaledugroup.enums.Role;
import org.example.globaledugroup.repository.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
public class MyBot implements CommandLineRunner {

    private final TelegramBot telegramBot;
    private final ExecutorService telegramBotThreadPool;
    private final BotUtils botUtils;
    private final BotService botService;
    private final UserRepository userRepository;
    private final OperatorRepository operatorRepository;
    private final AdministratorRepository administratorRepository;
    private final AdminService adminService;
    private final OperatorService operatorService;
    private final BotDataService botDataService;
    private final UserService userService;
    private final TelegramUserRepo telegramUserRepo;

    @Override
    public void run(String... args) {


        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                telegramBotThreadPool.execute(() -> {
                    updateHandler(update);
                });
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    @SneakyThrows
    private void updateHandler(@NotNull Update update) {
        Message message = update.message();
        String startSpecific = "";
        com.pengrad.telegrambot.model.User user = null;
        if (message != null) {
            startSpecific = message.text();
            user = message.from();
        } else if (update.callbackQuery() != null) {
            user = update.callbackQuery().from();
        }
        TelegramUser telegramUser = botDataService.getTelegramUser(user);
        //userIsEnabled(telegramUser);
        User user1 = botDataService.checkInvite(startSpecific);
        if (user1 != null && !Objects.equals(telegramUser.getBotRole(), BotRole.ADMIN)) {
            String role = "";
            if (user1.getRole().equals(Role.ADMINISTRATOR)) {
                role = Role.ADMINISTRATOR.name();
                telegramUser.setBotRole(BotRole.ADMIN);
            }

            if (user1.getRole().equals(Role.OPERATOR)) {
                role = Role.OPERATOR.name();
                telegramUser.setBotRole(BotRole.OPERATOR);
            }

            if (user1.getRole().equals(Role.USER)) {
                role = Role.USER.name();
                telegramUser.setBotRole(BotRole.USER);
            }
            telegramUserRepo.save(telegramUser);
            user1.setTelegramUser(telegramUser);
            userRepository.save(user1);
            SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "\uD83D\uDCAB\uD83D\uDCAB Assalomu alaykum %s tabriklaymiz sizning rolingiz %s ,\n\nbotdan to'liq foydalanish uchun /start ni bosing\n".formatted(telegramUser.getBotName(), role));
            telegramBot.execute(sendMessage);
        }
        switch (telegramUser.getBotRole()) {
            case ADMIN -> adminService.panel(telegramUser, update);
            case OPERATOR -> operatorService.panel(telegramUser, update);
            case USER -> userService.panel(update, telegramUser);
            default -> throw new IllegalStateException("Unexpected role: " + telegramUser.getBotRole());
        }
    }

    private void userIsEnabled(TelegramUser telegramUser) {
        if (userRepository.findByTelegramUserId(telegramUser.getId()).isPresent()) {
            User user2 = userRepository.findByTelegramUserId(telegramUser.getId()).get();
            if (!user2.isActive()) {
                throw new RuntimeException("this user is not enabled");
            }
        }
    }
}