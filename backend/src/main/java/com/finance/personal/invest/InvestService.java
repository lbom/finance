package com.finance.personal.invest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class InvestService {

    private final InvestRepo investRepo;

    public List<Invest> getPersonInvests(Long personId) {
        return investRepo.getInvestsByPersonId(personId);
    }

    public void addInvest(Invest investment) {
        investRepo.save(investment);
    }

    public BigDecimal calcProfit(List<Invest> investments) {
        return investments.stream()
                .map(Invest::getProfit)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void updateInvest(Long personId, Long investId, InvestDto investDto) {
        investRepo.findById(investId)
            .filter(invest -> invest.getPersonId().equals(personId))
            .ifPresent(invest -> {
                if (investDto.type() != null) {
                    invest.setType(investDto.type().name());
                }
                invest.setInstitutionId(investDto.institutionId());
                invest.setSymbolId(investDto.symbolId());
                invest.setReason(investDto.reason());
                invest.setAmount(investDto.amount());
                invest.setProfit(investDto.profit());
                invest.setStartDate(investDto.startDate());
                invest.setEndDate(investDto.endDate());
                investRepo.save(invest);
            });
    }

    public void deleteInvest(Long personId, Long investId) {
        investRepo.findById(investId)
            .filter(invest -> invest.getPersonId().equals(personId))
            .ifPresent(investRepo::delete);
    }
}