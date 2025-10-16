package com.akabazan.scheduler.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.akabazan.service.TradeService;

@Component
public class TradeAutoCancelJob {

    private static final Logger log = LoggerFactory.getLogger(TradeAutoCancelJob.class);

    private final TradeService tradeService;

    @Value("${app.trade.auto-cancel-enabled:true}")
    private boolean autoCancelEnabled;

    public TradeAutoCancelJob(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @Scheduled(fixedDelayString = "${app.trade.auto-cancel-interval-ms:60000}")
    public void cancelExpiredTrades() {
        if (!autoCancelEnabled) {
            return;
        }
        int cancelled = tradeService.autoCancelExpiredTrades();
        if (cancelled > 0) {
            log.info("Auto-cancelled {} expired trades", cancelled);
        }
    }
}
