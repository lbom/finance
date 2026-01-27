package com.finance.fx;

import java.util.List;

public interface FxModule {
    List<FxModuleRateDto> getForexRates(Long baseCurrencyId);
    List<FxModuleRateDto> getCryptoRates(Long baseCurrencyId);
}
