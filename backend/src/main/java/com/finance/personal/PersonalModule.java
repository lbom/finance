package com.finance.personal;

import java.util.List;

public interface PersonalModule {
    List<PersonModuleDto> getPersonsByUserId(Long userId);
}
