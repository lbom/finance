package com.finance.fx;

import com.finance.fx.pair.Pair;
import com.finance.fx.pair.PairRepo;
import com.finance.fx.pair.PairType;
import com.finance.fx.rate.FxRateRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FxModuleImpl implements FxModule {

    private final FxRateRepo fxRateRepo;
    private final PairRepo fxPairRepo;
    private final FxModuleMapper fxModuleMapper;

    @Override
    public List<FxModuleRateDto> getForexRates(Long baseCurrencyId) {
        return getRatesByTypeAndBaseCurrencyId(PairType.FOREX, baseCurrencyId);
    }

    @Override
    public List<FxModuleRateDto> getCryptoRates(Long baseCurrencyId) {
        return getRatesByTypeAndBaseCurrencyId(PairType.CRYPTO, baseCurrencyId);
    }

    private List<FxModuleRateDto> getRatesByTypeAndBaseCurrencyId(PairType type, Long baseCurrencyId){
        var pairIds = fxPairRepo.findAllByTypeAndBaseCurrencyId(type, baseCurrencyId)
                .stream()
                .map(Pair::getId)
                .toList();
        var entities = fxRateRepo.findAllById(pairIds);
        return fxModuleMapper.toDto(entities);
    }
}
