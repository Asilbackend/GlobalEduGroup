package org.example.globaledugroup.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageReplyMarkup;
import com.pengrad.telegrambot.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.example.globaledugroup.entity.TelegramUser;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BotUtils {
    private final TelegramBot telegramBot;
    public static Map<Long, Integer> DELETE_MESSAGES = new HashMap<>();

    public InlineKeyboardMarkup generateInlineBinaryBtn(List<String> str) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        int size = str.size();
        boolean isOdd = size % 2 != 0;
        for (int i = 0; i < size; i += 2) {
            if (isOdd && i == size - 1) {
                // If the number of strings is odd and this is the last string
                keyboardMarkup.addRow(new InlineKeyboardButton(str.get(i)).callbackData(str.get(i)));
            } else {
                keyboardMarkup.addRow(
                        new InlineKeyboardButton(str.get(i)).callbackData(str.get(i)),
                        new InlineKeyboardButton(str.get(i + 1)).callbackData(str.get(i + 1))
                );
            }
        }
        return keyboardMarkup;
    }

    public ReplyKeyboardMarkup generateKeyboardBinaryBtn(List<String> str) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup("");
        int size = str.size();
        boolean isOdd = size % 2 != 0;
        for (int i = 0; i < size; i += 2) {
            if (isOdd && i == size - 1) {
                // If the number of strings is odd and this is the last string
                keyboardMarkup.addRow(new KeyboardButton(str.get(i)));
            } else {
                keyboardMarkup.addRow(
                        new KeyboardButton(str.get(i)),
                        new KeyboardButton(str.get(i + 1))
                );
            }
        }
        return keyboardMarkup.resizeKeyboard(true);
    }

    public Keyboard generateMarkingBtn(List<String> marks) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        for (int i = 0; i < marks.size(); i++) {
            keyboardMarkup.addRow(new InlineKeyboardButton(marks.get(i)).callbackData((marks.size() - i) + ""));
            System.out.println(marks.get(i) + " callBackData: " + (marks.size() - i));
        }
        keyboardMarkup.addRow(new InlineKeyboardButton(BOT_CONSTANT.Back).callbackData(BOT_CONSTANT.Back));
        return keyboardMarkup;
    }

    public void deleteMessages(long chatId) {
        for (Map.Entry<Long, Integer> longIntegerEntry : DELETE_MESSAGES.entrySet()) {
            if (longIntegerEntry.getKey().equals(chatId)) {
                DeleteMessage deleteMessage = new DeleteMessage(chatId, longIntegerEntry.getValue());
                telegramBot.execute(deleteMessage);
            }
        }
        DELETE_MESSAGES.remove(chatId);
    }
    public void editInlineButton(Long userChatId, Integer messageId, InlineKeyboardMarkup inlineKeyboardMarkup) {
        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(userChatId, messageId)
                .replyMarkup(inlineKeyboardMarkup);
        telegramBot.execute(editMessageReplyMarkup);
    }

    public Keyboard generateContactBtn() {
        return new ReplyKeyboardMarkup(
                new KeyboardButton(BOT_CONSTANT
                        .CONTACT)
                        .requestContact(true))
                .resizeKeyboard(true)
                .oneTimeKeyboard(true);
    }
}