package org.example.globaledugroup.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.globaledugroup.entity.abs.BaseEntity;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class DeletingUser extends BaseEntity {
    @ManyToOne
    private User fromUser;
    @ManyToOne
    private User deleteUser;
}
