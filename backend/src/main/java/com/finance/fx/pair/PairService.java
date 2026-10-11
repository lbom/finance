package com.finance.fx.pair;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PairService {

    private final PairRepo pairRepo;

    public List<Pair> getList() {
        return pairRepo.findAll();
    }

    public Pair save(Pair pair) {
        if (pair.getType() == null) {
            pair.setType(PairType.FOREX);
        }
        return pairRepo.save(pair);
    }
}
