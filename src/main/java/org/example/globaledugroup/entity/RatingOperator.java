package org.example.globaledugroup.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.globaledugroup.entity.abs.BaseEntity;
import org.hibernate.annotations.Check;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Check(constraints = "rating >= 1 AND rating <= 5")
public class RatingOperator extends BaseEntity {
    @OneToOne
    private TelegramUser telegramUser;
    @ManyToOne
    private Operator operator;
    private Integer rating;
}