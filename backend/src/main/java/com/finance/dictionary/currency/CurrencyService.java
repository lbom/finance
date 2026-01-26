package com.finance.dictionary.currency;

import com.finance.dictionary.core.DictionaryService;
import org.springframework.stereotype.Service;

@Service
public class CurrencyService extends DictionaryService<Currency> {
    public CurrencyService(CurrencyRepo repository) {
        super(repository);
    }
}
