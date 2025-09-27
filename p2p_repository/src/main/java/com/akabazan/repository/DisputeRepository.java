package com.akabazan.repository;

import com.akabazan.repository.entity.Dispute;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DisputeRepository extends JpaRepository<Dispute, Long> {
    List<Dispute> findByTradeId(Long tradeId);
}
