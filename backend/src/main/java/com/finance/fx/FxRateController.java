package com.finance.fx;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/finance/fx/rate")
@RequiredArgsConstructor
public class FxRateController {

    private final FxModule fxModule;

    @PostMapping("/refresh")
    public void refreshRates() {
        fxModule.refreshRates();
    }
}
