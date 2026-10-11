package com.finance.fx;

import com.finance.fx.pair.Pair;
import com.finance.fx.pair.PairRepo;
import com.finance.fx.pair.PairType;
import com.finance.fx.rate.FxBinanceRateService;
import com.finance.fx.rate.FxFrankfurterRateService;
import com.finance.fx.rate.FxNationalBankKztRateService;
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
    private final FxFrankfurterRateService fxFrankfurterRateService;
    private final FxBinanceRateService fxBinanceRateService;
    private final FxNationalBankKztRateService fxNationalBankKztRateService;

    @Override
    public List<FxModuleRateDto> getForexRates(Long baseCurrencyId) {
        return getRatesByTypeAndCurrencyId(PairType.FOREX, baseCurrencyId);
    }

    @Override
    public List<FxModuleRateDto> getCryptoRates(Long baseCurrencyId) {
        return getRatesByTypeAndCurrencyId(PairType.CRYPTO, baseCurrencyId);
    }

    @Override
    public void refreshRates() {
        fxFrankfurterRateService.refreshRates();
        fxBinanceRateService.refreshRates();
        fxNationalBankKztRateService.refreshRates();
    }

    private List<FxModuleRateDto> getRatesByTypeAndCurrencyId(PairType type, Long currencyId) {
        var pairs = currencyId == null
                ? fxPairRepo.findAllByType(type)
                : fxPairRepo.findAllByTypeAndCurrencyId(type, currencyId);
        var pairIds = pairs.stream()
                .map(Pair::getId)
                .toList();
        var entities = fxRateRepo.findAllById(pairIds);
        return fxModuleMapper.toDto(entities);
    }
}
