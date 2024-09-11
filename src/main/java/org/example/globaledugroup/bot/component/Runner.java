package org.example.globaledugroup.bot.component;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.example.globaledugroup.entity.*;
import org.example.globaledugroup.enums.BotRole;
import org.example.globaledugroup.enums.Gender;
import org.example.globaledugroup.enums.Role;
import org.example.globaledugroup.enums.TELEGRAM_STATE;
import org.example.globaledugroup.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Runner implements CommandLineRunner {
    private final UserRepository userRepository;
    private final OperatorRepository operatorRepository;
    private final TelegramUserRepo telegramUserRepo;
    private final TelegramBot telegramBot;
    @Value("${spring.jpa.hibernate.ddl-auto}")
    String ddl;
    @Value("${supper.admin.chat.id}")
    Long superChatId;
    private final CodeUserRepository codeUserRepository;
    private final MessageRepository messageRepository;
    private final FunUserRepository funUserRepository;

    @Override
    public void run(String... args) throws Exception {
        codeUserRepository.deleteAll();
        List<Message> all = messageRepository.findAll();
        for (Message message : all) {
            message.getToUsers().clear();
            messageRepository.delete(message);
        }
        System.out.println(ddl);
        if (ddl.equals("create")) {
            TelegramUser telegramUser = telegramUserRepo.save(TelegramUser.builder().botName("Asilbek")
                    .botRole(BotRole.ADMIN).nickName("mathical_04")
                    .chatId(superChatId).botPassword("123")
                    .build());
            userRepository.save(User.builder().role(Role.ADMINISTRATOR).active(true)
                    .phone("+998919207150")
                    .telegramUser(telegramUser)
                    .firstName("Asilbek")
                    .lastName("O'ktamov")
                    .gender(Gender.MALE)
                    .build());
            /*User user1 = userRepository.save(User.builder().phone("123").firstName("Asadbek").lastName("Alimov").role(Role.ADMINISTRATOR).build());
            User user2 = userRepository.save(User.builder().phone("111").firstName("Sevinch").lastName("Normurodova").role(Role.OPERATOR).build());
            User user3 = userRepository.save(User.builder().phone("222").firstName("Zebiniso").lastName("Abitova").role(Role.OPERATOR).build());
            operatorRepository.save(Operator.builder().user(user2).averageRating(null).build());
            operatorRepository.save(Operator.builder().user(user3).averageRating(null).build());
            for (int i = 0; i < 10; i++) {
                telegramUserRepo.save(TelegramUser.builder().chatId(999444L + i).botRole(BotRole.USER).botName("name" + (i + 1)).nickName("asxfd" + i).telegramState(TELEGRAM_STATE.START).build());
            }
            for (int i = 0; i < 3; i++) {
                TelegramUser telegramUser = telegramUserRepo.save(TelegramUser.builder().chatId(9921124L + i).botRole(BotRole.OPERATOR).botName("menejer" + (i + 1)).nickName("JIOJOJ" + i).telegramState(TELEGRAM_STATE.START).build());
                if (i == 2) {
                    user2.setTelegramUser(telegramUser);
                    userRepository.save(user2);
                }
                if (i == 1) {
                    user3.setTelegramUser(telegramUser);
                    userRepository.save(user3);
                }
            }
            for (int i = 0; i < 5; i++) {
                telegramUserRepo.save(TelegramUser.builder().chatId(9432444L + i).botRole(BotRole.ADMIN).botName("ADMIN" + (i + 1)).nickName("ADBNKNKJ" + i).telegramState(TELEGRAM_STATE.START).build());
            }*/
            List<FunUser> funUsers = new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                funUsers.add(new FunUser("Ahmad Azamov" + (i), "+99891234458" + i));
            }
            funUserRepository.saveAll(funUsers);
        }
    }
}