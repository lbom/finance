package com.finance.person;

import java.util.List;

public interface PersonModule {
    List<PersonDto> getPersonsByUserId(Long userId);
}
