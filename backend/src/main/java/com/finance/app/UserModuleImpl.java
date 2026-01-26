package com.finance.app;

import com.finance.app.auth.CustomUserDetails;
import com.finance.app.error.CustomAuthorityException;
import com.finance.person.PersonModule;
import com.finance.person.PersonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserModuleImpl implements UserModule {

    private final PersonModule personModule;

    public Long retrieveUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
                && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getId();
        }

        throw new IllegalStateException("User not found in context or wrong principal type");
    }

    @Override
    public void hasAuthority(Long personId) {
        if (personId == null) throw new CustomAuthorityException();
        var userId = retrieveUserId();
        if (userId == null) throw new CustomAuthorityException();
        var persons = personModule.getPersonsByUserId(userId);
        if (!persons.stream().map(PersonDto::id).toList().contains(personId)) {
            throw new CustomAuthorityException();
        };
    }
}
