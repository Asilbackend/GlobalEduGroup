package org.example.globaledugroup.bot;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import org.example.globaledugroup.entity.TelegramUser;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OperatorService {
    public void panel(TelegramUser telegramUser, Update update) {
        if (update.message() != null) {
            Message message = update.message();
            if (message.text() != null) {
                String text = message.text();
                if (text.equals("/start")) {

                } else {

                }
            }
        } else if (update.callbackQuery() != null) {


        } else if (update.inlineQuery() != null) {

        }
    }
}
