package org.example.globaledugroup.bot;

import com.pengrad.telegrambot.model.*;
import lombok.RequiredArgsConstructor;
import org.example.globaledugroup.entity.FunUser;
import org.example.globaledugroup.entity.TelegramUser;
import org.example.globaledugroup.enums.TELEGRAM_STATE;
import org.example.globaledugroup.repository.FunUserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final BotService botService;
    private final BotDataService botDataService;
    private final BotUtils botUtils;
    private final FunUserRepository funUserRepository;

    public void panel(Update update, TelegramUser telegramUser) {
        if (update.message() != null) {
            Message message = update.message();
            String text = message.text();
            if (text != null) {
                /// T  E  X  T
                if (text.length() > 6) {
                    String data = text.substring(7);
                    if (data.equals("rating")) {
                        botService.acceptSurveyAndGenerateBtn(telegramUser);
                        botDataService.setAndPutState(telegramUser, TELEGRAM_STATE.RATING_OF_SURVEY);
                    }
                }
                if (text.equals("/start")) {
                    botService.acceptStartSendData(telegramUser);
                } else {
                    if (telegramUser.getTelegramState().equals(TELEGRAM_STATE.OPERATORS_OF_SURVEY)) {
                        botService.acceptSurveyAndGenerateBtn(telegramUser);
                        botDataService.setAndPutState(telegramUser, TELEGRAM_STATE.RATING_OF_SURVEY);
                    } else if (telegramUser.getTelegramState().equals(TELEGRAM_STATE.ASK_CONTACT_FOR_USER)) {
                        if (!telegramUser.isShareContactForFunUser()){
                            botService.askContactUser(telegramUser, text);
                        }
                    }
                }
            } else if (message.contact() != null) {
                if (telegramUser.getTelegramState().equals(TELEGRAM_STATE.ASK_CONTACT_FOR_USER)) {
                    if (!telegramUser.isShareContactForFunUser()){
                        System.out.println("phone: "+message.contact().phoneNumber());
                        botService.askContactUser(telegramUser, message.contact().phoneNumber());
                    }

                }
            } else if (message.location() != null) {
                /// L  O  C  A  T  I  O  N
                Location location = update.message().location();
                botDataService.receiveAndForwardLocation(location);
            }
        } else if (update.callbackQuery() != null) {
            CallbackQuery callbackQuery = update.callbackQuery();
            String data = callbackQuery.data();
            if (data != null) {
                switch (telegramUser.getTelegramState()) {
                    case RATING_OF_SURVEY -> {
                        botUtils.deleteMessages(telegramUser.getChatId());
                        botService.acceptChosenOperatorAskRate(telegramUser, data);
                        botDataService.setAndPutState(telegramUser, TELEGRAM_STATE.THANKS_FOR_ATTEND);
                    }
                    case THANKS_FOR_ATTEND -> {
                        botUtils.deleteMessages(telegramUser.getChatId());
                        if (data.equals(BOT_CONSTANT.Back)) {
                            botService.acceptSurveyAndGenerateBtn(telegramUser);
                            botDataService.setAndPutState(telegramUser, TELEGRAM_STATE.RATING_OF_SURVEY);
                        } else {
                            botService.saveRatingAndThanks(telegramUser, data);
                            botDataService.setAndPutState(telegramUser, TELEGRAM_STATE.FINISHED_SURVEY);
                        }
                    }
                }
            }
        }
    }
}