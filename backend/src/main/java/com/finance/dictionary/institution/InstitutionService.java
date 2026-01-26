package com.finance.dictionary.institution;

import com.finance.dictionary.core.DictionaryService;
import org.springframework.stereotype.Service;

@Service
public class InstitutionService extends DictionaryService<Institution> {
    public InstitutionService(InstitutionRepo repository) {
        super(repository);
    }
}
