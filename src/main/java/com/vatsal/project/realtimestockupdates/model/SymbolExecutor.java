package com.vatsal.project.realtimestockupdates.model;

import lombok.Getter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Model class for storing symbols and executor to poll symbol at fixed intervals for a user.
 * @author imvtsl
 * @since v1.0
 */

@Getter
public class SymbolExecutor {
    /**
     * A queue to store incoming requests.
     */
    private String symbol;

    /**
     * Executor that polls requests from queue at fixed intervals.
     */
    private ScheduledExecutorService scheduledExecutorService;

    public SymbolExecutor(String symbol) {
        this.symbol = symbol;
        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
    }

    /**
     * Stops the thread pool.
     */
    public void stop() {
        scheduledExecutorService.shutdownNow();
    }
}
