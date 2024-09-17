package org.example.globaledugroup.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.globaledugroup.entity.abs.BaseEntity;
import org.example.globaledugroup.enums.BotRole;
import org.example.globaledugroup.enums.Role;
import org.example.globaledugroup.enums.TELEGRAM_STATE;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TelegramUser extends BaseEntity {
    @Column(unique = true, nullable = false)
    private Long chatId;
    private String botName;
    private String nickName;
    private Timestamp birthDate;
    private String botPassword;
    private String phone;
    @Enumerated(value = EnumType.STRING)
    private BotRole botRole;
    @Enumerated(EnumType.STRING)
    private TELEGRAM_STATE telegramState = TELEGRAM_STATE.START;
    private boolean isAttendRating;
    private boolean isShareContactForFunUser;

    public TelegramUser(Long chatId, String botName, String nickName, BotRole botRole) {
        this.chatId = chatId;
        this.botName = botName;
        this.nickName = nickName;
        this.botRole = botRole;
    }
}