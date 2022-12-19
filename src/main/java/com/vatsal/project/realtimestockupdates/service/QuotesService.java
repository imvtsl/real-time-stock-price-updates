package com.vatsal.project.realtimestockupdates.service;

import com.vatsal.project.realtimestockupdates.model.ModelMapper;
import com.vatsal.project.realtimestockupdates.model.SymbolExecutor;
import com.vatsal.project.realtimestockupdates.rest.client.Rest;
import com.vatsal.project.realtimestockupdates.dto.FinnhubResponse;
import com.vatsal.project.realtimestockupdates.dto.QuoteResponse;
import com.vatsal.project.realtimestockupdates.dto.StatusResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A service class having business logic for streaming live stock updates.
 *
 * @author imvtsl
 * @since v1.0
 */

@Service
@Slf4j
public class QuotesService {
    private Map<String, List<SymbolExecutor>> symbolExecutorMap;

    @Autowired
    private Rest rest;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Assigns memory to map after this bean is initialised.
     */
    @PostConstruct
    private void instantiate() {
        symbolExecutorMap = new HashMap<>();
    }

    /**
     * Subscribes to the incoming request.
     * @param userName String
     * @return StatusResponse
     */
    public synchronized SseEmitter submitRequest(String userName, String symbol, String token) {
        SseEmitter emitter = new SseEmitter(0L);
        List<SymbolExecutor> symbolExecutorList;
        if (symbolExecutorMap.containsKey(userName)) {
            symbolExecutorList = symbolExecutorMap.get(userName);
            boolean symbolExists = symbolExecutorList.stream().anyMatch(s -> s.getSymbol().equalsIgnoreCase(symbol));
            if (symbolExists) {
                log.error("Symbol is already subscribed by the given user.");
                emitter.completeWithError(new Exception());
                return emitter;
            }
            streamEvents(userName, symbol, token, emitter, symbolExecutorList);
        } else {
            symbolExecutorList = new ArrayList<>();
            streamEvents(userName, symbol, token, emitter, symbolExecutorList);
        }
        return emitter;
    }

    /**
     * Removes the user from the map. If user is not found in the map, it returns failure.
     *
     * @param userName String
     * @return StatusResponse
     */
    public synchronized StatusResponse deregisterUser(String userName) {
        if (symbolExecutorMap.containsKey(userName)) {
            symbolExecutorMap.remove(userName);
            return new StatusResponse(true);
        } else {
            return new StatusResponse(false, "user:" + userName + " not found.");
        }

    }

    /**
     * Stops streaming the symbol for the given user. If user or symbol is not found, it returns failure.
     * @param userName String
     * @param symbol String
     * @return StatusResponse
     */
    public synchronized StatusResponse deregisterSymbol(String userName, String symbol) {
        if (symbolExecutorMap.containsKey(userName)) {
            List<SymbolExecutor> symbolExecutorList = symbolExecutorMap.get(userName);
            boolean result = symbolExecutorList.removeIf(s -> s.getSymbol().equalsIgnoreCase(symbol));
            if (result) {
                return new StatusResponse(result);
            } else {
                return new StatusResponse(false, "symbol:" + symbol + " not found.");
            }
        } else {
            return new StatusResponse(false, "user:" + userName + " not found.");
        }
    }

    /**
     * Updates the map with the incoming request.
     *
     * @param symbolExecutorList List<SymbolExecutor>
     * @param symbolExecutor     SymbolExecutor
     * @param userName           String
     */
    private synchronized void updateMap(List<SymbolExecutor> symbolExecutorList, SymbolExecutor symbolExecutor, String userName) {
        symbolExecutorList.add(symbolExecutor);
        symbolExecutorMap.put(userName, symbolExecutorList);
    }

    /**
     * Streams the events to the user.
     * @param userName String
     * @param symbol String
     * @param token String
     * @param emitter SseEmitter
     * @param symbolExecutorList List<SymbolExecutor>
     */
    private void streamEvents(String userName, String symbol, String token, SseEmitter emitter, List<SymbolExecutor> symbolExecutorList) {
        SymbolExecutor symbolExecutor = new SymbolExecutor(symbol);
        updateMap(symbolExecutorList, symbolExecutor, userName);
        ScheduledExecutorService scheduledExecutorService = symbolExecutor.getScheduledExecutorService();

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            FinnhubResponse finnhubResponse = rest.sendRequestFinnhubQuote(symbol, token);
            QuoteResponse quoteResponse = modelMapper.mapFinnhubResponseToQuoteResponse(finnhubResponse);
            try {
                emitter.send(quoteResponse);
            } catch (IOException e) {
                emitter.completeWithError(e);
            }

        }, 15, 15, TimeUnit.SECONDS);
    }
}
