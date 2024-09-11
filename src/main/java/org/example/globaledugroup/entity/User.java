package org.example.globaledugroup.entity;

import jakarta.persistence.*;
import lombok.*;

import org.example.globaledugroup.entity.abs.BaseEntity;
import org.example.globaledugroup.enums.Gender;
import org.example.globaledugroup.enums.Role;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
public class User extends BaseEntity implements UserDetails {
    private String firstName;
    private String lastName;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String phone;
    @OneToOne
    private TelegramUser telegramUser;
    @Builder.Default
    private boolean active = true;
    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    private User createdBy;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }

    @Override
    public String getUsername() {
        return phone;
    }
}
// phone, f,l,