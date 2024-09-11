package org.example.globaledugroup.entity.view;

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
public class University extends BaseEntity {
    private String name;
    private String place;
    @ManyToOne
    private Attachment attachment;
}