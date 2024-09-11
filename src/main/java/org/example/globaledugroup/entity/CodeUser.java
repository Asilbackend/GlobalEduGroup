package org.example.globaledugroup.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.example.globaledugroup.entity.abs.BaseEntity;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
public class CodeUser extends BaseEntity {
    @ManyToOne
    private User user;
    private String code;
}
