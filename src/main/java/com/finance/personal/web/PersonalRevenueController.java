package com.finance.personal.web;

import com.finance.personal.service.PersonalRevenueService;
import com.finance.personal.web.mapper.PersonalRevenueDto;
import com.finance.personal.web.mapper.PersonalRevenueMapper;
import com.finance.trades.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/personal/revenue")
@RequiredArgsConstructor
public class PersonalRevenueController {

    private final PersonalRevenueService service;
    private final PersonalRevenueMapper mapper;

    @GetMapping
    public List<PersonalRevenueDto> getList() {
        return mapper.toDto(service.getAll());
    }

    @PostMapping
    public void add(@RequestBody PersonalRevenueDto dto) {
        service.save(mapper.toEntity(dto));
    }
}