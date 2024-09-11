package org.example.globaledugroup.bot.botDto;

import kotlin.jvm.Volatile;
import org.example.globaledugroup.enums.BotRole;
import org.example.globaledugroup.enums.Gender;
import org.example.globaledugroup.enums.TELEGRAM_STATE;

import java.sql.Timestamp;

public interface OperatorProjection {
    Integer getCountTelegramUsers(); // Ensure it matches the query alias
    Float getAverageRating();        // Ensure it matches the query alias
    String getFirstName();           // Ensure it matches the query alias
    String getLastName();            // Ensure it matches the query alias
    String getPhone();               // Ensure it matches the query alias
}
