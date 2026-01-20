package com.finance.app.user;

import com.finance.app.UserInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserInterface {

    public Long retrieveUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserApp userDetails) {
            return userDetails.getId();
        }

        throw new IllegalStateException("User not found in context or wrong principal type");
    }
}
