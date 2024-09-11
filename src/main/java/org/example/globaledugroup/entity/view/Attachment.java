package org.example.globaledugroup.entity.view;

import jakarta.persistence.Entity;
import lombok.*;
import org.example.globaledugroup.entity.abs.BaseEntity;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Attachment extends BaseEntity {
    private String fileName;
    private String url;
}
