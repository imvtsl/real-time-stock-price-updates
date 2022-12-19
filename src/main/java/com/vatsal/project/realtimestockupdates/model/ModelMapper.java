package com.vatsal.project.realtimestockupdates.model;

import com.vatsal.project.realtimestockupdates.dto.FinnhubResponse;
import com.vatsal.project.realtimestockupdates.dto.QuoteResponse;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting from one object to another.
 * @author imvtsl
 * @since v1.0
 */

@Component
public class ModelMapper {
    /**
     * Maps response from finnhub.io to a more human-readable format for the user
     * @param finnhubResponse FinnhubResponse
     * @return QuoteResponse
     */
    public QuoteResponse mapFinnhubResponseToQuoteResponse(FinnhubResponse finnhubResponse) {
        QuoteResponse quoteResponse = new QuoteResponse();
        quoteResponse.setCurrentPrice(finnhubResponse.getCurrentPrice());
        quoteResponse.setChange(finnhubResponse.getChange());
        quoteResponse.setPercentChange(finnhubResponse.getPercentChange());
        quoteResponse.setHigh(finnhubResponse.getHigh());
        quoteResponse.setLow(finnhubResponse.getLow());
        quoteResponse.setOpen(finnhubResponse.getOpen());
        quoteResponse.setPreviousClose(finnhubResponse.getPreviousClose());
        quoteResponse.setTimestamp(finnhubResponse.getTimestamp());
        return quoteResponse;
    }
}
