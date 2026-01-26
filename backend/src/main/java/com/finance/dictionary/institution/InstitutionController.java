package com.finance.dictionary.institution;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/finance/dictionary/institution")
@RequiredArgsConstructor
public class InstitutionController {

    private final InstitutionService service;
    private final InstitutionMapper mapper;

    @GetMapping
    public List<InstitutionDto> getInstitutions() {
        var list = service.getList();
        return mapper.toDto(list);
    }

    @PostMapping
    public void add(@Validated @RequestBody InstitutionDto dto) {
        var data = mapper.toEntity(dto);
        service.save(data);
    }
}