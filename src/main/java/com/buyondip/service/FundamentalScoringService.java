package com.buyondip.service;

import com.buyondip.dto.FundamentalsDto;
import org.springframework.stereotype.Service;

@Service
public class FundamentalScoringService {

    /**
     * Returns a score in [0.0, 1.0] based on 5 binary criteria, each worth 0.2.
     * ROE/ROCE thresholds assume Yahoo Finance returns values as ratios (0.15 = 15%).
     * DebtToEquity assumes Yahoo returns as ratio (1.0 = 100% D/E).
     * EpsGrowth: > 0 means positive earnings growth.
     * PromoterHolding assumes percentage (>= 50 means >= 50%).
     */
    public double score(FundamentalsDto f) {
        double s = 0.0;
        if (f.getRoe() != null && f.getRoe().doubleValue() >= 0.15) s += 0.2;
        if (f.getRoce() != null && f.getRoce().doubleValue() >= 0.15) s += 0.2;
        if (f.getDebtToEquity() != null && f.getDebtToEquity().doubleValue() <= 1.0) s += 0.2;
        if (f.getEpsGrowth() != null && f.getEpsGrowth().doubleValue() > 0) s += 0.2;
        if (f.getPromoterHolding() != null && f.getPromoterHolding().doubleValue() >= 50) s += 0.2;
        return s;
    }
}
