package com.finance.personal.web;

import com.finance.personal.service.PersonalExpensesService;
import com.finance.personal.web.mapper.PersonalExpenseDto;
import com.finance.personal.web.mapper.PersonalExpensesMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/finance/personal/expenses")
@RequiredArgsConstructor
public class PersonalExpensesController {

    private final PersonalExpensesService service;
    private final PersonalExpensesMapper mapper;

    @GetMapping
    public List<PersonalExpenseDto> getList() {
        return mapper.toDto(service.getAll());
    }

    @PostMapping
    public void add(@RequestBody PersonalExpenseDto dto) {
        service.save(mapper.toEntity(dto));
    }
}