package org.example.globaledugroup.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.globaledugroup.bot.BOT_CONSTANT;
import org.example.globaledugroup.entity.abs.BaseEntity;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class Message extends BaseEntity {
    @ManyToOne
    private TelegramUser from;
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    private List<TelegramUser> toUsers;
    private String text;
    private boolean ok;
}