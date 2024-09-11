package org.example.globaledugroup.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import org.example.globaledugroup.projections.UserFullNameProjection;
import org.example.globaledugroup.entity.TelegramUser;
import org.example.globaledugroup.entity.User;
import org.example.globaledugroup.enums.BotRole;
import org.example.globaledugroup.enums.Role;
import org.example.globaledugroup.enums.TELEGRAM_STATE;
import org.example.globaledugroup.repository.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    /*private static final Long ADMIN_URL = 1474065279L;*/
    private final BotUtils botUtils;
    private final BotService botService;
    private final TelegramBot telegramBot;
//    private final BotDataService botDataService;
//    private final UserRepository userRepository;
//    private final OperatorRepository operatorRepository;
//    private final AdministratorRepository administratorRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final MessageRepository messageRepository;
//    private final TelegramUserRepo telegramUserRepo;

    public void panel(TelegramUser telegramUser, Update update) {
        if (update.message() != null) {
            Message message = update.message();
            if (message.text() != null) {
                String text = message.text();
                if (text.equals("/start")) {
                    botService.adminMenu(telegramUser);
                } else {
                    if (telegramUser.getTelegramState().equals(TELEGRAM_STATE.ADMIN_MENU)) {
                        botService.askMenu(telegramUser, text);
                    } else {
                        switch (telegramUser.getTelegramState()) {
//                            case START -> {
//                            }
//                            case OPERATORS_OF_SURVEY -> {
//                            }
//                            case RATING_OF_SURVEY -> {
//                            }
//                            case THANKS_FOR_ATTEND -> {
//                            }
//                            case FINISHED_SURVEY -> {
//                            }
//                            case SEARCH -> {
//                            }
//                            case START_ADMIN -> {
//                            }
//                            case ADMIN_MENU -> {
//                            }
                            case WROTE_TEXT_FOR_SEND -> botService.controlWroteText(telegramUser, text);
                            case CHOSEN_FROM -> botService.controlSendMessage(telegramUser, text);
                            case VIEWED_USERS -> botService.controlUserDepartment(telegramUser, text);
                            case VIEWED_SETTINGS_DEPARTMENT -> {
                                if (text.equals(BOT_CONSTANT.Back)) {
                                    botUtils.deleteMessages(telegramUser.getChatId());
                                    botService.adminMenu(telegramUser);
                                } else {
                                    SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "siz settingsni ko'rdingiz");
                                    telegramBot.execute(sendMessage);
                                }
                            }
                            case VIEWED_ADMIN_DEPARTMENT -> botService.controlAdminDepartment(telegramUser, text);
                            case ASK_CHOSEN_FOR_DELETE_ADMIN -> {
                                botUtils.deleteMessages(telegramUser.getChatId());
                                if (text.equals(BOT_CONSTANT.Back)) {
                                    botService.adminDepartment(telegramUser);
                                }
                            }
                            case ASK_FLP_FOR_ADMIN -> botService.controlAddAdmin(telegramUser, text);
                            case IS_CONFiRM_ADMIN -> botService.controlIsConfirmAdmin(telegramUser, text);
                            case VIEWED_OPERATOR_DEPARTMENT -> botService.controlOperatorDepartment(telegramUser, text);
                            case ASK_FLP_FOR_OPERATOR -> botService.askFLPAndControlSelf(telegramUser, text);
                            case IS_CONFRIM_OPERATOR -> botService.controlIsConfirmOperator(telegramUser, text);
                            case ACCEPT_SURE_OR_NOT -> botService.controlSureOrNotForDeleteUser(telegramUser, text);
                            case ASK_CHOSEN_FOR_DELETE_OPERATOR -> {
                                if (text.equals(BOT_CONSTANT.Back)) {
                                    botUtils.deleteMessages(telegramUser.getChatId());
                                    botService.askMenu(telegramUser, BOT_CONSTANT.OPERATORS_DEPARTMENT);
                                }
                            }
                            case ACTIVATE_USER -> botService.conrolToActiveUser(telegramUser, text);
                        }
                    }
                }
            }
        } else if (update.callbackQuery() != null) {
            CallbackQuery callbackQuery = update.callbackQuery();
            String data = callbackQuery.data();
            if (data != null) {
                if (telegramUser.getTelegramState().equals(TELEGRAM_STATE.ASK_CHOSEN_FOR_DELETE_OPERATOR)) {
                    botUtils.deleteMessages(telegramUser.getChatId());
                    botService.controlChosenDeleteAnyUser(telegramUser, data);
                } else if (telegramUser.getTelegramState().equals(TELEGRAM_STATE.ASK_CHOSEN_FOR_DELETE_ADMIN)) {
                    botUtils.deleteMessages(telegramUser.getChatId());
                    botService.controlChosenDeleteAnyUser(telegramUser, data);
                }
            }
        } else if (update.inlineQuery() != null) {

        }
    }


}
