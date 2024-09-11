package org.example.globaledugroup.repository;

import org.example.globaledugroup.entity.TelegramUser;
import org.example.globaledugroup.enums.BotRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TelegramUserRepo extends JpaRepository<TelegramUser, Long> {
    Optional<TelegramUser> findByChatId(Long id);

    List<TelegramUser> findByBotRole(BotRole botRole);
    List<TelegramUser> findByBotRoleAndIsAttendRatingFalse(BotRole botRole);


    List<TelegramUser> findByBotRoleAndIsAttendRatingTrue(BotRole botRole);
}
