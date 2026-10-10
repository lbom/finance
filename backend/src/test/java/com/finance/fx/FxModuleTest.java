package com.finance.fx;

import com.finance.fx.rate.FxBinanceRateService;
import com.finance.fx.rate.FxFrankfurterRateService;
import com.finance.fx.rate.FxNationalBankKztRateService;
import com.finance.fx.pair.PairRepo;
import com.finance.fx.rate.FxRateRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FxModuleTest {

    @Mock
    private FxRateRepo fxRateRepo;

    @Mock
    private PairRepo fxPairRepo;

    @Mock
    private FxModuleMapper fxModuleMapper;

    @Mock
    private FxFrankfurterRateService fxFrankfurterRateService;

    @Mock
    private FxBinanceRateService fxBinanceRateService;

    @Mock
    private FxNationalBankKztRateService fxNationalBankKztRateService;

    @InjectMocks
    private FxModuleImpl fxModule;

    @Test
    void refreshRates_delegatesToAllServices() {
        fxModule.refreshRates();

        verify(fxFrankfurterRateService).refreshRates();
        verify(fxBinanceRateService).refreshRates();
        verify(fxNationalBankKztRateService).refreshRates();
    }
}
