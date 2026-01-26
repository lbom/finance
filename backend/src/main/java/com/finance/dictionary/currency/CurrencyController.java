package com.finance.dictionary.currency;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/finance/dictionary/symbol")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService service;
    private final CurrencyMapper mapper;

    @GetMapping
    public List<CurrencyDto> getPersons() {
        var list = service.getList();
        return mapper.toDto(list);
    }

    @PostMapping
    public void add(@RequestBody CurrencyDto dto) {
        var data = mapper.toEntity(dto);
        service.save(data);
    }
}