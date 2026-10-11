package com.finance.fx.pair;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/finance/dictionary/pair")
@RequiredArgsConstructor
public class PairController {

    private final PairService service;
    private final PairMapper mapper;

    @GetMapping
    public List<PairDto> getPairs() {
        var list = service.getList();
        return mapper.toDto(list);
    }

    @PostMapping
    public PairDto add(@Validated @RequestBody PairDto dto) {
        var data = mapper.toEntity(dto);
        var saved = service.save(data);
        return mapper.toDto(saved);
    }
}
