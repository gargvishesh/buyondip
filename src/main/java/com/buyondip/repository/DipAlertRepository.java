package com.buyondip.repository;

import com.buyondip.model.DipAlert;
import com.buyondip.model.DipSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DipAlertRepository extends JpaRepository<DipAlert, Long> {

    List<DipAlert> findByActiveOrderByDipPercentAsc(boolean active);

    List<DipAlert> findByActiveAndSourceOrderByDipPercentAsc(boolean active, DipSource source);

    List<DipAlert> findByActiveAndSourceOrderByCompositeScoreDesc(boolean active, DipSource source);

    Optional<DipAlert> findTopBySymbolAndActiveOrderByDetectedAtDesc(String symbol, boolean active);

    Optional<DipAlert> findTopBySymbolAndActiveAndSourceOrderByDetectedAtDesc(String symbol, boolean active, DipSource source);

    List<DipAlert> findBySource(DipSource source);

    void deleteBySymbol(String symbol);
}
