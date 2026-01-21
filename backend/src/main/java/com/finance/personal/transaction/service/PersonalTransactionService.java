package com.finance.personal.transaction.service;

import com.finance.app.UserInterface;
import com.finance.personal.transaction.persistence.PersonalTransaction;
import com.finance.personal.transaction.persistence.PersonalTransactionRepo;
import com.finance.personal.transaction.persistence.PersonalTransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonalTransactionService {

    private final UserInterface userInterface;
    private final PersonalTransactionRepo repository;

    public List<PersonalTransaction> getTransactionsByType(PersonalTransactionType type) {
        var userId = userInterface.retrieveUserId();
        return repository.getPersonalTransactionByUserIdAndType(userId, type);
    }
    public void save(PersonalTransaction transaction) {
        var userId = userInterface.retrieveUserId();
        transaction.setUserId(userId);
        repository.save(transaction);
    }
}
