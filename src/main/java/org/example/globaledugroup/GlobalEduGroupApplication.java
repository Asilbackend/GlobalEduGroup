package org.example.globaledugroup;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableAsync
public class GlobalEduGroupApplication {
    public static void main(String[] args) {
        SpringApplication.run(GlobalEduGroupApplication.class, args);
        //6241711304  dm
    }
}