package com.finance.fx;

import com.finance.fx.pair.Pair;
import com.finance.fx.pair.PairRepo;
import com.finance.fx.rate.FxRate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FxModuleMapper {

    private final PairRepo pairRepo;

    public List<FxModuleRateDto> toDto(List<FxRate> list) {
        var rateIds = list.stream().map(FxRate::getPairId).toList();
        var pairs = pairRepo.findAllById(rateIds)
                .stream()
                .collect(Collectors.toMap(Pair::getId, v -> v));
        var dtoList = new ArrayList<FxModuleRateDto>();
        list.forEach(rate -> {
            var pairId = rate.getPairId();
            dtoList.add(new FxModuleRateDto(
                rate.getPairId(),
                pairs.get(pairId).getBaseCurrencyId(),
                pairs.get(pairId).getQuoteCurrencyId(),
                pairs.get(pairId).getSymbol(),
                rate.getCandleType(),
                rate.getCandleDate(),
                rate.getPrice()
            ));
        });
        return dtoList;
    }
}
