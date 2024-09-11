package org.example.globaledugroup.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.*;
import org.example.globaledugroup.entity.abs.BaseEntity;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Administrator extends BaseEntity {
    @OneToOne
    private User user;
}