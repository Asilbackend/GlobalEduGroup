package org.example.globaledugroup.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.globaledugroup.entity.abs.BaseEntity;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FunUser extends BaseEntity {
    private String fullName;

    public FunUser(String fullName, String phone) {
        this.fullName = fullName;
        this.phone = phone;
    }

    private String phone;
    private boolean isCalled;
}
