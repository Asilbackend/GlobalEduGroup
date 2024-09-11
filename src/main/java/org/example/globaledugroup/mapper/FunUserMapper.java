package org.example.globaledugroup.mapper;

import org.example.globaledugroup.entity.FunUser;
import org.example.globaledugroup.dto.UserFunDto;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface FunUserMapper {
    FunUser toEntity(UserFunDto userFunDto);

    UserFunDto toDto(FunUser funUser);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    FunUser partialUpdate(UserFunDto userFunDto, @MappingTarget FunUser funUser);
}