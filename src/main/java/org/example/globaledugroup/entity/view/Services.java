package org.example.globaledugroup.entity.view;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.globaledugroup.entity.abs.BaseEntity;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Services extends BaseEntity {
    private String name;
    private String description;
    @ManyToOne
    private Attachment attachment;
}
