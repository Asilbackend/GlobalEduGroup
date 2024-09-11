package org.example.globaledugroup.bot.config;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {//6858875355:AAFZdO-w6J8x-LHfGyAAIzZd-IJ9TQd-ZpE donziki

    @Bean
    public TelegramBot telegramBot() {
        return new TelegramBot("6467382126:AAER27di_b40DYHaRjsU1SePqXNbg45R9wA");
    }
    //6467382126:AAER27di_b40DYHaRjsU1SePqXNbg45R9wA
}
