package org.example.globaledugroup.bot.botDto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.example.globaledugroup.enums.BotRole;
import org.example.globaledugroup.enums.TELEGRAM_STATE;

import java.sql.Timestamp;

public record OperatorDto(
        String firstName,
        Float rating,
        Integer countUser,
        Long chatId,
        String botName,
        String nickName,
        Timestamp birthDate,
        String botPassword,
        BotRole botRole,
        TELEGRAM_STATE telegramState,
        boolean isAttendRating
) {
}
