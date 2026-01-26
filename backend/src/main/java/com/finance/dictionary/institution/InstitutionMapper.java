package com.finance.dictionary.institution;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InstitutionMapper {
    InstitutionDto toDto(Institution entity);
    Institution toEntity(InstitutionDto dto);
    List<InstitutionDto> toDto(List<Institution> list);
}
