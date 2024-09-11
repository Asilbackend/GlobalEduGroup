package org.example.globaledugroup.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.globaledugroup.entity.abs.BaseEntity;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Operator extends BaseEntity {
    @OneToOne
    private User user;
    private Float averageRating;
    private long countTelegramUsers = 0;
}