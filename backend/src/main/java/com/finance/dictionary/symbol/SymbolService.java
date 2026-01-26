package com.finance.dictionary.symbol;

import com.finance.dictionary.core.DictionaryService;
import org.springframework.stereotype.Service;

@Service
public class SymbolService extends DictionaryService<Symbol> {
    public SymbolService(SymbolRepo repository) {
        super(repository);
    }
}
