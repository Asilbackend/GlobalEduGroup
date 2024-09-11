package org.example.globaledugroup.dto;

import java.io.Serializable;

/**
 * DTO for {@link org.example.globaledugroup.entity.FunUser}
 */
public record UserFunDto(String fullName, String phone) implements Serializable {
}