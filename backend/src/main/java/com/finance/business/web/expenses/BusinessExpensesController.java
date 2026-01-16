package com.finance.business.web.expenses;

import com.finance.business.service.BusinessExpensesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/finance/business/expenses")
@RequiredArgsConstructor
public class BusinessExpensesController {

    private final BusinessExpensesService service;
    private final BusinessExpensesMapper mapper;

    @GetMapping
    public List<BusinessExpensesDto> getList() {
        return mapper.toDto(service.getAll());
    }

    @PostMapping
    public void add(@RequestBody BusinessExpensesDto dto) {
        service.save(mapper.toEntity(dto));
    }
}