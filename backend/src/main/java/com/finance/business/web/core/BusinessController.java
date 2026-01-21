package com.finance.business.web.core;

import com.finance.business.service.BusinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/finance/business")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService service;
    private final BusinessMapper mapper;

    @GetMapping
    public List<BusinessDto> getList() {
        return mapper.toDto(service.getAll());
    }

    @PostMapping
    public void add(@RequestBody BusinessDto dto) {
        service.save(mapper.toEntity(dto));
    }
}