package org.example.globaledugroup.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import org.example.globaledugroup.enums.BotRole;
import org.example.globaledugroup.projections.UserFullNameProjection;
import org.example.globaledugroup.bot.botDto.OperatorProjection;
import org.example.globaledugroup.entity.*;
import org.example.globaledugroup.enums.Role;
import org.example.globaledugroup.enums.TELEGRAM_STATE;
import org.example.globaledugroup.repository.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@RequiredArgsConstructor
public class BotService {
    public static final String PHONE_REGEX = "^\\+998\\d{9}$";
    public static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);
    private final TelegramBot telegramBot;
    private final BotUtils botUtils;
    private final UserRepository userRepository;
    private final RatingOperatorRepository ratingOperatorRepository;
    private final OperatorRepository operatorRepository;
    private final TelegramUserRepo telegramUserRepo;
    private final BotDataService botDataService;
    private final DeletingUserRepository deletingUserRepository;
    private final AdministratorRepository administratorRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageRepository messageRepository;
    private final CodeUserRepository codeUserRepository;
    private final FunUserRepository funUserRepository;

    public void acceptStartSendData(TelegramUser telegramUser) {
        mySendMessage(telegramUser.getChatId(), """
                Assalomu alaykum %s Global Edu Group ga xush kelibsiz 😊
                """.formatted(telegramUser.getBotName()));
        mySendMessage(telegramUser.getChatId(), """
                "Global Edu Group" education consultancy\s
                                            
                🏢Nufuzli oliygohlar;
                                            
                🎓70 da ortiq yo'nalishlar;
                                            
                📋Litsenziya;
                                            
                💻Hemis dasturi;
                                            
                🎓Ōzbekiston va Xalqaro diplom;
                                            
                🌐Qōshma dasturlar:(2+2),(3+1);
                                            
                📌 O'qish davomida amaliyot;
                                            
                💰Hamyonbop kontraktlar;
                                            
                Biz bilan ishonchli tarzda talaba bo'ling...
                                            
                Murojaat uchun:
                ☎️: +998881075551
                ☎️: +998881075552
                                            
                                            
                📱: instagram.com/global_edu_group
                """);
        leaveContact(telegramUser);
    }

    public void leaveContact(TelegramUser telegramUser) {
        SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "Biz bilan bog'lanish uchun kontakt yuboring tugmasini bosing yoki raqamingizni qoldiring 😊 \n\n Namuna: +998901234567");
        sendMessage.replyMarkup(botUtils.generateContactBtn());
        SendResponse sendResponse = telegramBot.execute(sendMessage);
        BotUtils.DELETE_MESSAGES.put(telegramUser.getChatId(), sendResponse.message().messageId());
        botDataService.setAndPutState(telegramUser, TELEGRAM_STATE.ASK_CONTACT_FOR_USER);
    }


    public void acceptSurveyAndGenerateBtn(TelegramUser telegramUser) {
        if (telegramUser.isAttendRating()) {
            mySendMessage(telegramUser.getChatId(), "Hurmatli " + telegramUser.getBotName() + ", siz avval baholashda ishtirok etgansiz😉\n\nHar bir foydalanuvchi faqat bir marta ishtirok etishi mumkin🤷‍♂️");
            return;
        }
        mySendMessage(telegramUser.getChatId(), "Xizmat sifatini yaxshilash uchun kelganingizdan xursandmiz \uD83D\uDE0A");
        SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "Menejer tanlang ⬇\uFE0F");
        List<String> fullNames = new ArrayList<>();
        List<UserFullNameProjection> usersName = userRepository.findAllByRoleAndActiveTrue(Role.OPERATOR);
        for (UserFullNameProjection userFullNameProjection : usersName) {
            fullNames.add(userFullNameProjection.getFirstName() + " " + userFullNameProjection.getLastName());
        }
        sendMessage.replyMarkup(botUtils.generateInlineBinaryBtn(fullNames));
        SendResponse sendResponse = telegramBot.execute(sendMessage);
        BotUtils.DELETE_MESSAGES.put(telegramUser.getChatId(), sendResponse.message().messageId());
    }

    @Transactional
    public void acceptChosenOperatorAskRate(TelegramUser telegramUser, String data) {
        String[] split = data.split(" ");
        String fName = split[0];
        String lName = split[1];
        Optional<User> userOptional = userRepository.findByFirstNameAndLastNameAndActiveTrue(fName, lName);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Operator operator = operatorRepository.findByUserId(user.getId()).orElseThrow(() -> new RuntimeException("operator of user not found"));
            if (ratingOperatorRepository.findByTelegramUserId(telegramUser.getId()).isEmpty()) {
                ratingOperatorRepository.save(RatingOperator.builder().operator(operator).telegramUser(telegramUser).build());
            } else {
                RatingOperator ratingOperator = ratingOperatorRepository.findByTelegramUserId(telegramUser.getId()).get();
                if (!ratingOperator.getOperator().getId().equals(operator.getId())) {
                    ratingOperator.setOperator(operator);
                    ratingOperatorRepository.save(ratingOperator);
                }
            }
            SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), """
                    ✨Menejer %sni baholang""".formatted(operator.getUser().getFirstName() + " " + operator.getUser().getLastName()));
            sendMessage.replyMarkup(botUtils.generateMarkingBtn(new ArrayList<>(List.of(BOT_CONSTANT.FIVE, BOT_CONSTANT.FOUR, BOT_CONSTANT.THREE, BOT_CONSTANT.TWO, BOT_CONSTANT.ONE))));
            SendResponse sendResponse = telegramBot.execute(sendMessage);
            BotUtils.DELETE_MESSAGES.put(telegramUser.getChatId(), sendResponse.message().messageId());
        } else {
            mySendMessage(telegramUser.getChatId(), """
                                      
                    Bunday Menejer topilmadi 🤷‍♂️
                                       
                    """);
        }
    }

    @Transactional
    public void saveRatingAndThanks(TelegramUser telegramUser, String data) {
        telegramUser.setAttendRating(true);
        telegramUserRepo.save(telegramUser);
        Integer num = Integer.valueOf(data);
        RatingOperator ratingOperator = ratingOperatorRepository.findByTelegramUserId(telegramUser.getId()).orElseThrow(() -> new RuntimeException("rating not found for this user: " + telegramUser));
        ratingOperator.setRating(num);
        ratingOperatorRepository.save(ratingOperator);
        Operator operator = ratingOperator.getOperator();
        Float sumOfRatings = ratingOperatorRepository.findSumOfRatingsByOperatorId(operator.getId());
        operator.setAverageRating(sumOfRatings);
        operator.setCountTelegramUsers(operator.getCountTelegramUsers() + 1);
        operatorRepository.save(operator);
        mySendMessage(telegramUser.getChatId(), "Qabul qilindi✔ \n\nishtirokingiz uchun raahmat 😊");
    }

    private SendResponse mySendMessage(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage(chatId, message);
        return telegramBot.execute(sendMessage);
    }

    public void adminMenu(TelegramUser telegramUser) {
        System.out.println("di");
        SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), BOT_CONSTANT.MENU);
        sendMessage.replyMarkup(botUtils.generateKeyboardBinaryBtn(new ArrayList<>(List.of(
                BOT_CONSTANT.OPERATORS_DEPARTMENT, BOT_CONSTANT.ADMINS_DEPARTMENT, BOT_CONSTANT.USERS, BOT_CONSTANT.MESSAGES, BOT_CONSTANT.USER_FROM_WEBSITE, BOT_CONSTANT.DELETED_OR_NO_CONFIRM_USERS
        ))));
        telegramBot.execute(sendMessage);
        botDataService.setAndPutState(
                telegramUser, TELEGRAM_STATE.ADMIN_MENU
        );
    }

    public void askMenu(TelegramUser telegramUser, String text) {
        switch (text) {
            case BOT_CONSTANT.MESSAGES -> {
                botDataService.sendSearchSticker(telegramUser.getChatId());
                SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "Xabarni kiriting: ");
                sendMessage.replyMarkup(new ReplyKeyboardMarkup(BOT_CONSTANT.Back).resizeKeyboard(true));
                telegramBot.execute(sendMessage);
                botDataService.setAndPutState(telegramUser, TELEGRAM_STATE.WROTE_TEXT_FOR_SEND);
            }
            case BOT_CONSTANT.USERS -> {
                botDataService.sendSearchSticker(telegramUser.getChatId());
                SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "kerakli tugmani bosing: ");
                sendMessage.replyMarkup(botUtils.generateKeyboardBinaryBtn(new ArrayList<>(List.of(BOT_CONSTANT.ATTENDED_USERS, BOT_CONSTANT.BASIC_USERS, BOT_CONSTANT.Back))));
                telegramBot.execute(sendMessage);
                botDataService.setAndPutState(telegramUser, TELEGRAM_STATE.VIEWED_USERS);
            }
            case BOT_CONSTANT.OPERATORS_DEPARTMENT -> operatorDepartment(telegramUser);
            case BOT_CONSTANT.ADMINS_DEPARTMENT -> adminDepartment(telegramUser);
            case BOT_CONSTANT.USER_FROM_WEBSITE -> showWebsiteUser(telegramUser);
            case BOT_CONSTANT.SETTINGS -> {
                SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "Ushbu bo'limda tuzatish ishlari borilyapti");
                sendMessage.replyMarkup(botUtils.generateKeyboardBinaryBtn(new ArrayList<>(List.of(BOT_CONSTANT.Back))));
                telegramBot.execute(sendMessage);
                botDataService.setAndPutState(telegramUser, TELEGRAM_STATE.VIEWED_SETTINGS_DEPARTMENT);
            }
            case BOT_CONSTANT.DELETED_OR_NO_CONFIRM_USERS -> {
                controlActiveUser(telegramUser);
            }
            case BOT_CONSTANT.Back -> {
                botUtils.deleteMessages(telegramUser.getChatId());
                adminMenu(telegramUser);
            }
        }
    }

    public void showWebsiteUser(TelegramUser telegramUser) {
        List<FunUser> all = funUserRepository.findAll(Sort.by(Sort.Direction.ASC, "createdAt"));
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        for (FunUser funUser : all) {
            if (i % 50 == 0) {
                mySendMessage(telegramUser.getChatId(), stringBuilder.toString());
                stringBuilder = new StringBuilder();
            }
            stringBuilder.append("""
                    ➖%s➖➖➖➖➖➖➖➖➖➖
                    🖇 Ism Familiya: %s
                                                
                    ☎️ Telefon nomer: %s \n
                    """.formatted((++i), funUser.getFullName(), funUser.getPhone()));
        }
        mySendMessage(telegramUser.getChatId(), stringBuilder.toString());

    }

    public void controlActiveUser(TelegramUser telegramUser) {
        StringBuilder stringBuilder = new StringBuilder();
        List<User> allByActiveFalse = userRepository.findAllByActiveFalse();
        if (allByActiveFalse.isEmpty()) {
            SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "\uD83E\uDD37\u200D♂\uFE0F Ushbu kategoriyada userlar topilmadi");
            telegramBot.execute(sendMessage);
        } else {
            int i = 0;
            for (User user : allByActiveFalse) {
                if (i % 40 == 0) {
                    SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), stringBuilder.toString());
                    telegramBot.execute(sendMessage);
                    stringBuilder = new StringBuilder();
                }
                stringBuilder.append("""
                        ➖%s➖➖➖➖➖➖➖➖
                        Nomi: %s
                        Telefon raqami: %s
                        Role: %s
                        Active: %s
                        Aktivlashtirish kodi: %s
                                                   
                        """.formatted(++i, user.getFirstName() + " " + user.getLastName(), user.getPhone(), user.getRole() == null ? "null" : user.getRole().name(), user.isActive(), user.getId()));
            }
            SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), stringBuilder.append("\n\n\uD83D\uDEA8Foydalanuvchini tiklash uchun aktivlashtirish kodini yuboring ").toString());
            sendMessage.replyMarkup(new ReplyKeyboardMarkup(BOT_CONSTANT.Back).resizeKeyboard(true).oneTimeKeyboard(true));
            telegramBot.execute(sendMessage);
            botDataService.setAndPutState(telegramUser, TELEGRAM_STATE.ACTIVATE_USER);
        }
    }

    public void adminDepartment(TelegramUser telegramUser) {
        botDataService.sendSearchSticker(telegramUser.getChatId());
        StringBuilder stringBuilder = new StringBuilder();
        List<UserFullNameProjection> allByRoleAndActiveTrue = userRepository.findAllByRoleAndActiveTrue(Role.ADMINISTRATOR);
        int i = 0;
        for (UserFullNameProjection userFullNameProjection : allByRoleAndActiveTrue) {
            if (i % 50 == 0) {
                SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), stringBuilder.toString());
                telegramBot.execute(sendMessage);
                stringBuilder = new StringBuilder();
            }
            stringBuilder.append("\n⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶⫶\n").append(++i).append("\uD83D\uDC68\u200D\uD83D\uDCBB  ").append(userFullNameProjection.getFirstName()).append(" ").append(userFullNameProjection.getLastName()).append(" ").append(userFullNameProjection.getPhone());
        }
        SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), stringBuilder.toString());
        sendMessage.replyMarkup(botUtils.generateKeyboardBinaryBtn(new ArrayList<>(List.of(BOT_CONSTANT.ADD_ADMIN, BOT_CONSTANT.DELETE_ADMIN, BOT_CONSTANT.Back))));
        telegramBot.execute(sendMessage);
        botDataService.setAndPutState(telegramUser, TELEGRAM_STATE.VIEWED_ADMIN_DEPARTMENT);
    }

    public void operatorDepartment(TelegramUser telegramUser) {
        botDataService.sendSearchSticker(telegramUser.getChatId());
        List<OperatorProjection> operatorDto = ratingOperatorRepository.findAllOperatorDto();
        System.out.println(operatorDto);
        String s;
        if (operatorDto.isEmpty()) {
            s = "Operatorlar yo'q";
        } else {
            s = botDataService.formatOperators(operatorDto, telegramUser.getChatId());
        }
        SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), s);
        sendMessage.replyMarkup(botUtils.generateKeyboardBinaryBtn(new ArrayList<>(List.of(BOT_CONSTANT.ADD_DEPARTMENT, BOT_CONSTANT.DELETE_DEPARTMENT, BOT_CONSTANT.Back))));
        telegramBot.execute(sendMessage);
        botDataService.setAndPutState(telegramUser, TELEGRAM_STATE.VIEWED_OPERATOR_DEPARTMENT);
    }

    public void askFLP_forOperator(TelegramUser telegramUser) {
        SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "Ism familiya telefon raqam kiriting: \nNmauna: Asilbek O'ktamov +998919207150");
        sendMessage.replyMarkup(botUtils.generateKeyboardBinaryBtn(new ArrayList<>(List.of(BOT_CONSTANT.Back))));
        telegramBot.execute(sendMessage);
        botDataService.setAndPutState(telegramUser, TELEGRAM_STATE.ASK_FLP_FOR_OPERATOR);
    }

    public void increasedLength(TelegramUser telegramUser) {
        SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "Probeller oshib ketti. Iltimos qayta urinib ko'ring");
        telegramBot.execute(sendMessage);
    }

    public void decreasedLength(TelegramUser telegramUser) {
        SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "Probeller kamayib ketti. Iltimos qayta urinib ko'ring");
        telegramBot.execute(sendMessage);
    }

    @Transactional
    public void askFLPAndControlSelf(TelegramUser telegramUser, String text) {
        if (text.equals(BOT_CONSTANT.Back)) {
            askMenu(telegramUser, BOT_CONSTANT.OPERATORS_DEPARTMENT);
        } else {
            //har balo kelshi mumkin
            String[] split = text.split(" ");
            if (split.length > 3) {
                increasedLength(telegramUser);
                askFLP_forOperator(telegramUser);
            } else if (split.length < 3) {
                decreasedLength(telegramUser);
                askFLP_forOperator(telegramUser);
            } else if (!PHONE_PATTERN.matcher(split[2]).matches()) {
                SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "Telefon raqam xato formatda kiritildi. Iltimos qayta urnab ko'ring");
                telegramBot.execute(sendMessage);
                askFLP_forOperator(telegramUser);
            } else {
                System.out.println("tel raqa  keldi");
                String phone = split[2];
                Optional<User> userOptional = userRepository.findByPhone(phone);
                if (userOptional.isPresent()) {
                    foundAlreadyExistUser(telegramUser, userOptional.get());
                    askFLP_forOperator(telegramUser);
                } else {
                    System.out.println("yagona tel raqam keldi");
                    User user = userRepository.findByTelegramUserId(telegramUser.getId()).get();
                    String firstName = split[0];
                    String lastName = split[1];
                    User operUser = userRepository.save(User.builder().phone(phone)
                            .firstName(firstName)
                            .lastName(lastName)
                            .active(false)
                            .role(Role.OPERATOR)
                            .createdBy(user)
                            .build());
                    operatorRepository.save(Operator.builder().user(operUser).build());
                    areYouSure(telegramUser);
                    botDataService.setAndPutState(telegramUser, TELEGRAM_STATE.IS_CONFRIM_OPERATOR);
                }
                // mani userim bo'lishi kerak man admin
            }
        }
    }

    public void areYouSure(TelegramUser telegramUser) {
        SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "amalni tasdiqlaysmi");
        sendMessage.replyMarkup(botUtils.generateKeyboardBinaryBtn(new ArrayList<>(List.of(BOT_CONSTANT.CONFIRM, BOT_CONSTANT.CANCEL))));
        telegramBot.execute(sendMessage);
    }

    @Transactional
    public void cancelOperator(TelegramUser telegramUser, User user) {
        Optional<Operator> operatorOptional = operatorRepository.findByUserId(user.getId());
        operatorOptional.ifPresent(operator -> operatorRepository.deleteById(operator.getId()));
        userRepository.deleteById(user.getId());
        SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "Bekor qqilindi ✔");
        telegramBot.execute(sendMessage);
        operatorDepartment(telegramUser);
        //askMenu(telegramUser, BOT_CONSTANT.OPERATORS_DEPARTMENT);
    }

    public User checkDeleteResponse(String data, TelegramUser telegramUser) {
        String[] split = data.split(" ");
        User user = null;
        if (split.length == 3) {
            String phone = split[split.length - 1];
            Optional<User> byPhone = userRepository.findByPhone(phone);
            if (byPhone.isPresent()) {
                if (!Objects.equals(byPhone.get().getPhone(), userRepository.findByTelegramUserId(telegramUser.getId()).get().getPhone())) {
                    user = byPhone.get();
                } else {
                    SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "O'zini o'zi yo'q qilish ta'qiqlanadi ❗\uFE0F");
                    telegramBot.execute(sendMessage);
                    SendMessage sendMessage1 = new SendMessage(telegramUser.getChatId(), "❗\uFE0F");
                    telegramBot.execute(sendMessage1);
                    adminMenu(telegramUser);
                }
            }
        }
        return user;
    }

    @Transactional
    public void controlChosenDeleteAnyUser(TelegramUser telegramUser, String data) {
        User user = checkDeleteResponse(data, telegramUser);
        if (user == null) {
            SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "Biz o'chirish yuborgan inline buttonda nimadir xatolik sodir qildingiz 🤷‍♂️");
            telegramBot.execute(sendMessage);
        } else {
            User tgUser = userRepository.findByTelegramUserId(telegramUser.getId()).orElseThrow(() -> new RuntimeException("bu telegram userning Useri yo'q"));
            if (deletingUserRepository.findByFromUserId(tgUser.getId()).isEmpty()) {
                user.setActive(false);
                userRepository.save(user);
                deletingUserRepository.save(new DeletingUser(tgUser, user));
            }
            areYouSure(telegramUser);
            botDataService.setAndPutState(telegramUser, TELEGRAM_STATE.ACCEPT_SURE_OR_NOT);

        }
    }

    @Transactional
    public void controlSureOrNotForDeleteUser(TelegramUser telegramUser, String text) {
        Role role = null;

        List<DeletingUser> delets = deletingUserRepository.findByFromUserId(userRepository.findByTelegramUserId(telegramUser.getId()).get().getId());
        if (text.equals(BOT_CONSTANT.CONFIRM)) {
            if (!delets.isEmpty()) {
                DeletingUser deletingUser = delets.get(delets.size() - 1);
                User deleteUser = deletingUser.getDeleteUser();
                role = deleteUser.getRole();
                deleteUser.setActive(false);

                //new

                TelegramUser telegramUser1 = deleteUser.getTelegramUser();
                if (telegramUser1 != null) {
                    telegramUser1.setBotRole(BotRole.USER);
                    telegramUserRepo.save(telegramUser1);
                    deleteUser.setTelegramUser(telegramUser1);
                }
                //new

                userRepository.save(deleteUser);


                SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "Muvaffaqiyatli O'CHIRILDI ✔✔✔✔");
                telegramBot.execute(sendMessage);
                deletingUserRepository.deleteById(deletingUser.getId());
            } else {
                SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "Sizda o'chiradigan user yo'q, Nimadir xatolik yuz berganga o'xshaydi\uD83E\uDD14");
                telegramBot.execute(sendMessage);
            }
        } else if (text.equals(BOT_CONSTANT.CANCEL)) {
            if (!delets.isEmpty()) {
                DeletingUser deletingUser = delets.get(delets.size() - 1);
                User deleteUser = deletingUser.getDeleteUser();
                role = deleteUser.getRole();
            }
            for (DeletingUser delet : delets) {
                deletingUserRepository.deleteById(delet.getId());
            }
            SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "✔✔✔✔Muvaffaqqiyatli BEKOR qilindi");
            telegramBot.execute(sendMessage);
        }
        if (Objects.equals(role, Role.OPERATOR)) {
            operatorDepartment(telegramUser);
        } else if (Objects.equals(role, Role.ADMINISTRATOR)) {
            adminDepartment(telegramUser);
        } else {
            //shu yerdan automatik yonaltirish kerak
            adminMenu(telegramUser);
        }
    }

    public void ASK_FLP_forAdmin(TelegramUser telegramUser) {
        SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "Ism familiya telefon raqam kiritng\n\nNamuna: Asilbek O'ktamov +998919207150\n");
        sendMessage.replyMarkup(botUtils.generateKeyboardBinaryBtn(new ArrayList<>(List.of(BOT_CONSTANT.Back))));
        telegramBot.execute(sendMessage);
        botDataService.setAndPutState(telegramUser, TELEGRAM_STATE.ASK_FLP_FOR_ADMIN);
    }

    public void cancelAdmin(TelegramUser telegramUser, User user) {
        Optional<Administrator> administratorOptional = administratorRepository.findByUserId(user.getId());
        administratorOptional.ifPresent(administrator -> administratorRepository.deleteById(administrator.getId()));
        userRepository.deleteById(user.getId());
        SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "Bekor qqilindi ✔");
        telegramBot.execute(sendMessage);
        adminDepartment(telegramUser);
        //askMenu(telegramUser, BOT_CONSTANT.OPERATORS_DEPARTMENT);
    }

    public void foundAlreadyExistUser(TelegramUser telegramUser, User user) {
        SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), botDataService.formatUser(user));
        telegramBot.execute(sendMessage);
    }

    @Transactional
    public void controlIsConfirmOperator(TelegramUser telegramUser, String text) {
        if (text.equals(BOT_CONSTANT.CONFIRM)) {
            List<User> users = userRepository.findAllByCreatedByIdAndActiveFalse(userRepository.findByTelegramUserId(telegramUser.getId()).get().getId());
            if (users.isEmpty()) {
                SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "Sizdan tasdiqlash so'raydigan user topilmadi");
                telegramBot.execute(sendMessage);
            } else {
                //createdi eng yaqin bolgan 1 ta user olish kerak
                User user = users.get(users.size() - 1);
                user.setActive(true);
                user.setRole(Role.OPERATOR);
                if (user.getPassword() == null) {
                    user.setPassword(passwordEncoder.encode("123"));
                }
                userRepository.save(user);
                sendOfferLink(telegramUser.getChatId(), user);
                SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), " Muvaffaqiyatli saqlandi ✨✨\n✅✅✅✅✅✅✅✅✅");
                telegramBot.execute(sendMessage);
                askMenu(telegramUser, BOT_CONSTANT.OPERATORS_DEPARTMENT);
            }
        } else if (text.equals(BOT_CONSTANT.CANCEL)) {
            List<User> users = userRepository.findAllByCreatedByIdAndActiveFalse(userRepository.findByTelegramUserId(telegramUser.getId()).get().getId());
            if (!users.isEmpty()) {
                cancelOperator(telegramUser, users.get(users.size() - 1));
            }
        }
    }

    public void sendOfferLink(Long chatId, User user) {
        String linkOffer = botDataService.createLinkOffer(user);
        SendMessage sendMessage = new SendMessage(chatId, linkOffer);
        telegramBot.execute(sendMessage);
    }

    public void controlOperatorDepartment(TelegramUser telegramUser, String text) {
        if (text.equals(BOT_CONSTANT.Back)) {
            botUtils.deleteMessages(telegramUser.getChatId());
            adminMenu(telegramUser);
        } else {
            if (text.equals(BOT_CONSTANT.ADD_DEPARTMENT)) {
                askFLP_forOperator(telegramUser);
            } else if (text.equals(BOT_CONSTANT.DELETE_DEPARTMENT)) {
                //delete
                List<UserFullNameProjection> users = userRepository.findAllByRoleAndActiveTrue(Role.OPERATOR);
                List<String> resString = new ArrayList<>();
                for (UserFullNameProjection user : users) {
                    resString.add(user.getFirstName() + " " + user.getLastName() + " " + user.getPhone());
                }
                //resString.add(BOT_CONSTANT.Back);
                SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "\uD83D\uDD34");
                sendMessage.replyMarkup(botUtils.generateInlineBinaryBtn(resString));
                SendResponse execute = telegramBot.execute(sendMessage);
                BotUtils.DELETE_MESSAGES.put(telegramUser.getChatId(), execute.message().messageId());
                SendMessage sendMessage1 = new SendMessage(telegramUser.getChatId(), "O'chirish uchun user tanlang: ");
                sendMessage1.replyMarkup(new ReplyKeyboardMarkup(BOT_CONSTANT.Back).resizeKeyboard(true));
                telegramBot.execute(sendMessage1);
                botDataService.setAndPutState(telegramUser, TELEGRAM_STATE.ASK_CHOSEN_FOR_DELETE_OPERATOR);
            } else {
                SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "siz menejerlarni ko'rdingiz");
                telegramBot.execute(sendMessage);
            }
        }
    }

    public void conrolToActiveUser(TelegramUser telegramUser, String text) {
        if (text.equals(BOT_CONSTANT.Back)) {
            adminMenu(telegramUser);
        } else {
            String resp;
            try {
                Long i = Long.valueOf(text);
                Optional<User> optionalUser = userRepository.findById(i);
                if (optionalUser.isEmpty()) {
                    resp = "Xato kod kiritdingiz";
                } else {
                    User user = optionalUser.get();
                    user.setActive(true);
                    resp = "✅ User muvaffaqqiyatli aktivlashtirildi";
                    userRepository.save(user);
                    Role role = user.getRole();

                    TelegramUser telegramUser1 = user.getTelegramUser();
                    activeRoleOfUser(telegramUser1, role);
                    sendOfferLink(telegramUser.getChatId(), user);
                }
                SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), resp);
                telegramBot.execute(sendMessage);
            } catch (Exception e) {
                SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "Xato kod kiritdingiz");
                telegramBot.execute(sendMessage);
            }
            controlActiveUser(telegramUser);
        }
    }

    private void activeRoleOfUser(TelegramUser telegramUser1, Role role) {
        if (telegramUser1 != null && role != null) {
            if (role.equals(Role.ADMINISTRATOR)) {
                telegramUser1.setBotRole(BotRole.ADMIN);
            } else if (role.equals(Role.USER)) {
                telegramUser1.setBotRole(BotRole.USER);
            } else if (role.equals(Role.OPERATOR)) {
                telegramUser1.setBotRole(BotRole.OPERATOR);
            }
            telegramUserRepo.save(telegramUser1);
        }
    }

    public void controlSendMessage(TelegramUser telegramUser, String text) {
        boolean isEmpty = false;
        SendMessage sendMessage0 = new SendMessage(telegramUser.getChatId(), "```xabaringiz yuborilyapti...```");
        if (text.equals(BOT_CONSTANT.SEND_TO_BASIC_USERS)) {
            telegramBot.execute(sendMessage0);
            List<org.example.globaledugroup.entity.Message> messages = messageRepository.findAllByFromIdAndOkFalse(telegramUser.getId());
            org.example.globaledugroup.entity.Message messageLast = messages.get(messages.size() - 1);
            List<TelegramUser> toUsers = telegramUserRepo.findByBotRoleAndIsAttendRatingFalse(BotRole.USER);
            isEmpty = toUsers.isEmpty();
            if (!isEmpty) {
                sendMessages(toUsers, messageLast);
                messageLast.setOk(true);
                messageRepository.save(messageLast);
                SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), " '' " + messageLast.getText() + " '' xabarini " + BOT_CONSTANT.SEND_TO_BASIC_USERS + " muvaffaqqiyatli yakunlandi ✔✔");
                telegramBot.execute(sendMessage);
            }
        } else if (text.equals(BOT_CONSTANT.SEND_TO_RATING_USER)) {
            telegramBot.execute(sendMessage0);
            List<org.example.globaledugroup.entity.Message> messages = messageRepository.findAllByFromIdAndOkFalse(telegramUser.getId());
            org.example.globaledugroup.entity.Message messageLast = messages.get(messages.size() - 1);
            List<TelegramUser> toUsers = telegramUserRepo.findByBotRoleAndIsAttendRatingTrue(BotRole.USER);
            isEmpty = toUsers.isEmpty();
            if (!isEmpty) {
                sendMessages(toUsers, messageLast);
                messageLast.setOk(true);
                messageRepository.save(messageLast);
                SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), messageLast.getText() + " xabarini " + BOT_CONSTANT.SEND_TO_RATING_USER + " muvaffaqqiyatli yakunlandi ✔✔");
                telegramBot.execute(sendMessage);
            }
        } else {
            SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "Negadir xabaringiz hechkimga yuborilmadi 🤷‍♂️🤷‍♂️");
            telegramBot.execute(sendMessage);
        }
        if (isEmpty) {
            SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "Sizda hali tanlangan kategoriyadagi userlar bo'lmagani sababli xabar yuborilmadi 🙁 ");
            telegramBot.execute(sendMessage);
        }
        adminMenu(telegramUser);
    }

    private void sendMessages(List<TelegramUser> toUsers, org.example.globaledugroup.entity.Message messageLast) {
        for (TelegramUser telegramUser1 : toUsers) {
            SendMessage sendMessage = new SendMessage(telegramUser1.getChatId(), messageLast.getText());
            telegramBot.execute(sendMessage);
        }
    }

    public void controlAddAdmin(TelegramUser telegramUser, String text) {
        if (text.equals(BOT_CONSTANT.Back)) {
            botUtils.deleteMessages(telegramUser.getChatId());
            askMenu(telegramUser, BOT_CONSTANT.ADMINS_DEPARTMENT);
        } else {
            //har balo kelshi mumkin
            String[] split = text.split(" ");
            if (split.length > 3) {
                increasedLength(telegramUser);
                ASK_FLP_forAdmin(telegramUser);
            } else if (split.length < 3) {
                decreasedLength(telegramUser);
                ASK_FLP_forAdmin(telegramUser);
            } else if (!BotService.PHONE_PATTERN.matcher(split[2]).matches()) {
                SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "Telefon raqam xato formatda kiritildi. Iltimos qayta urnab ko'ring");
                telegramBot.execute(sendMessage);
                ASK_FLP_forAdmin(telegramUser);
            } else {
                System.out.println("tel raqa  keldi");
                String phone = split[2];
                Optional<User> userOptional = userRepository.findByPhone(phone);
                if (userOptional.isPresent()) {
                    foundAlreadyExistUser(telegramUser, userOptional.get());
                    ASK_FLP_forAdmin(telegramUser);
                } else {
                    System.out.println("yagona tel raqam keldi");
                    User user = userRepository.findByTelegramUserId(telegramUser.getId()).get();
                    String firstName = split[0];
                    String lastName = split[1];
                    User operUser = userRepository.save(User.builder().phone(phone)
                            .firstName(firstName)
                            .lastName(lastName)
                            .active(false)
                            .role(Role.ADMINISTRATOR)
                            .createdBy(user)
                            .build());
                    administratorRepository.save(Administrator.builder().user(operUser).build());
                    areYouSure(telegramUser);
                    botDataService.setAndPutState(telegramUser, TELEGRAM_STATE.IS_CONFiRM_ADMIN);
                }
                // mani userim bo'lishi kerak man admin
            }
        }
    }

    @Transactional
    public void controlIsConfirmAdmin(TelegramUser telegramUser, String text) {
        if (text.equals(BOT_CONSTANT.CONFIRM)) {
            List<User> users = userRepository.findAllByCreatedByIdAndActiveFalse(userRepository.findByTelegramUserId(telegramUser.getId()).get().getId());
            if (users.isEmpty()) {
                SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "Sizdan tasdiqlash so'raydigan user topilmadi");
                telegramBot.execute(sendMessage);
            } else {
                //createdi eng yaqin bolgan 1 ta user olish kerak
                User user = users.get(users.size() - 1);
                user.setActive(true);
                user.setRole(Role.ADMINISTRATOR);
                user.setPassword(passwordEncoder.encode("123"));
                userRepository.save(user);
                sendOfferLink(telegramUser.getChatId(), user);
                SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "Muvaffaqiyatli saqlandi ✨✨\n✅✅✅✅✅✅✅✅✅");
                telegramBot.execute(sendMessage);
                askMenu(telegramUser, BOT_CONSTANT.ADMINS_DEPARTMENT);
            }
        } else if (text.equals(BOT_CONSTANT.CANCEL)) {
            List<User> users = userRepository.findAllByCreatedByIdAndActiveFalse(userRepository.findByTelegramUserId(telegramUser.getId()).get().getId());
            if (!users.isEmpty()) {
                cancelAdmin(telegramUser, users.get(users.size() - 1));
            }
        }
    }

    public void controlAdminDepartment(TelegramUser telegramUser, String text) {
        if (text.equals(BOT_CONSTANT.Back)) {
            botUtils.deleteMessages(telegramUser.getChatId());
            adminMenu(telegramUser);
        } else {
            if (text.equals(BOT_CONSTANT.ADD_ADMIN)) {
                ASK_FLP_forAdmin(telegramUser);
            } else if (text.equals(BOT_CONSTANT.DELETE_ADMIN)) {
                List<String> resultAdmin = new ArrayList<>();
                List<UserFullNameProjection> allByRoleAndActiveTrue = userRepository.findAllByRoleAndActiveTrue(Role.ADMINISTRATOR);
                for (UserFullNameProjection userFullNameProjection : allByRoleAndActiveTrue) {
                    resultAdmin.add(userFullNameProjection.getFirstName() + " " + userFullNameProjection.getLastName() + " " + userFullNameProjection.getPhone());
                }
                SendMessage sendMessage1 = new SendMessage(telegramUser.getChatId(), "⭕\uFE0F");
                sendMessage1.replyMarkup(new ReplyKeyboardMarkup(BOT_CONSTANT.Back).resizeKeyboard(true).oneTimeKeyboard(true));
                telegramBot.execute(sendMessage1);
                SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "\uD83D\uDD34 O'chirish uchun admin tanlang");
                sendMessage.replyMarkup(botUtils.generateInlineBinaryBtn(resultAdmin));
                SendResponse execute = telegramBot.execute(sendMessage);
                BotUtils.DELETE_MESSAGES.put(telegramUser.getChatId(), execute.message().messageId());
                botDataService.setAndPutState(telegramUser, TELEGRAM_STATE.ASK_CHOSEN_FOR_DELETE_ADMIN);
            } else {
                SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "siz adminlarni ko'rdingiz");
                telegramBot.execute(sendMessage);
            }
        }
    }

    public void controlUserDepartment(TelegramUser telegramUser, String text) {
        if (text.equals(BOT_CONSTANT.Back)) {
            botUtils.deleteMessages(telegramUser.getChatId());
            adminMenu(telegramUser);
        } else {
            if (text.equals(BOT_CONSTANT.ATTENDED_USERS)) {
                List<TelegramUser> telegramUsers = telegramUserRepo.findByBotRoleAndIsAttendRatingTrue(BotRole.USER);
                SendMessage sendMessage = basicUserResponse(telegramUser, telegramUsers);
                if (telegramUsers.isEmpty()) {
                    sendMessage = new SendMessage(telegramUser.getChatId(), "Hali hech kim baholashda ishtirok etmagan \uD83E\uDD37\u200D♂\uFE0F");
                }
                telegramBot.execute(sendMessage);
            } else if (text.equals(BOT_CONSTANT.BASIC_USERS)) {
                List<TelegramUser> telegramUsers = telegramUserRepo.findByBotRoleAndIsAttendRatingFalse(BotRole.USER);
                SendMessage sendMessage = basicUserResponse(telegramUser, telegramUsers);
                if (telegramUsers.isEmpty()) {
                    sendMessage = new SendMessage(telegramUser.getChatId(), "foydalanuvchilar mavjud emas \uD83E\uDD37\u200D♂\uFE0F");
                }
                telegramBot.execute(sendMessage);
            } else {
                SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "siz userlarni ko'rdingiz");
                telegramBot.execute(sendMessage);
            }
        }
    }

    @NotNull
    private SendMessage basicUserResponse(TelegramUser telegramUser, List<TelegramUser> telegramUsers) {
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        for (TelegramUser user : telegramUsers) {
            if (i % 40 == 0) {
                SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), stringBuilder.toString());
                telegramBot.execute(sendMessage);
                stringBuilder = new StringBuilder();
            }
            stringBuilder.append("""
                                       
                    ➖ %s ➖➖➖➖➖➖➖➖➖➖
                      nomi: %s
                      username: @%s
                      so'rovnomada qatnashganmi: %s
                      telefon raqami: %s
                      """.formatted(++i, user.getBotName(), user.getNickName(), user.isAttendRating() ? "ha" : "yo'q", user.getPhone()));
        }
        return new SendMessage(telegramUser.getChatId(), stringBuilder.toString());
    }

    public void controlWroteText(TelegramUser telegramUser, String text) {
        if (text.equals(BOT_CONSTANT.Back)) {
            adminMenu(telegramUser);
            messageRepository.deleteByOkTrue();
        } else {
            org.example.globaledugroup.entity.Message messageNew = new org.example.globaledugroup.entity.Message();
            messageNew.setFrom(telegramUser);
            messageNew.setText(text);
            messageRepository.save(messageNew);
            SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "Kimga yubormoqchisiz? ");
            sendMessage.replyMarkup(botUtils.generateKeyboardBinaryBtn(new ArrayList<>(List.of(
                    BOT_CONSTANT.SEND_TO_RATING_USER, BOT_CONSTANT.SEND_TO_BASIC_USERS, BOT_CONSTANT.Back
            ))));
            telegramBot.execute(sendMessage);
            botDataService.setAndPutState(telegramUser, TELEGRAM_STATE.CHOSEN_FROM);
        }
    }


    @Transactional
    @Scheduled(cron = "0 0 12 * * ?")
    public void deleteGarbage() {
        codeUserRepository.deleteAll();
        List<Message> all = messageRepository.findAll();
        for (Message message : all) {
            message.getToUsers().clear();
            messageRepository.delete(message);
        }
    }

    public void askContactUser(TelegramUser telegramUser, String text) {
        if (text.toCharArray()[0] != '+') {
            text = "+" + text;
        }
        Matcher matcher = PHONE_PATTERN.matcher(text);
        if (matcher.matches()) {
            funUserRepository.save(new FunUser(telegramUser.getBotName(), text));
            telegramUser.setShareContactForFunUser(true);
            telegramUserRepo.save(telegramUser);
            thanksForContactMessage(telegramUser);
            botDataService.setAndPutState(telegramUser, TELEGRAM_STATE.ASKED_CONTACT);
            botUtils.deleteMessages(telegramUser.getChatId());
        } else {
            leaveContact(telegramUser);
        }
    }

    public void thanksForContactMessage(TelegramUser telegramUser) {
        SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "Yaqinda menejerlarimiz siz bilan bog'lanishadi \uD83E\uDD1D \n\nBizga ishonganingiz uchun tashakkur \uD83D\uDE0A.");
        telegramBot.execute(sendMessage);
    }
}
//531,659