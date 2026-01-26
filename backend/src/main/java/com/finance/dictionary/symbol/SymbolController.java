package com.finance.dictionary.symbol;

import com.finance.person.PersonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/finance/dictionary/symbol")
@RequiredArgsConstructor
public class SymbolController {

    private final SymbolService service;
    private final SymbolMapper mapper;

    @GetMapping
    public List<PersonDto> getPersons() {
        var persons = service.getList();
        return mapper.toDto(persons);
    }

    @PostMapping
    public void add(@RequestBody PersonDto dto) {
        var data = mapper.toEntity(dto);
        service.save(data);
    }
}