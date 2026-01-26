package com.finance.dictionary.symbol;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/finance/dictionary/symbol")
@RequiredArgsConstructor
public class SymbolController {

    private final SymbolService service;
    private final SymbolMapper mapper;

    @GetMapping
    public List<SymbolDto> getSymbols() {
        var list = service.getList();
        return mapper.toDto(list);
    }

    @PostMapping
    public void add(@Validated @RequestBody SymbolDto dto) {
        var data = mapper.toEntity(dto);
        service.save(data);
    }
}