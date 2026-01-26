package com.finance.app.auth;

import com.finance.app.UserModule;
import com.finance.personal.PersonalModule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("securityService")
@RequiredArgsConstructor
public class SecurityService {

    private final PersonalModule personalModule;
    private final UserModule userModule;

    public boolean hasPersonAuthority(Long personId) {
        if (personId == null) return false;
        var userId = userModule.retrieveUserId();
        if (userId == null) return false;
        return personalModule.getPersonsByUserId(userId).stream()
                .anyMatch(p -> p.id().equals(personId));
    }
}