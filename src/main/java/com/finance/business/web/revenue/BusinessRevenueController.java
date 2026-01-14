package com.finance.business.web.revenue;

import com.finance.business.service.BusinessRevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/business/revenue")
@RequiredArgsConstructor
public class BusinessRevenueController {

    private final BusinessRevenueService service;
    private final BusinessRevenueMapper mapper;

    @GetMapping
    public List<BusinessRevenueDto> getList() {
        return mapper.toDto(service.getAll());
    }

    @PostMapping
    public void add(@RequestBody BusinessRevenueDto dto) {
        service.save(mapper.toEntity(dto));
    }
}