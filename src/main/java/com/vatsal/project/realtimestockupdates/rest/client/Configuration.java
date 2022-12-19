package com.vatsal.project.realtimestockupdates.rest.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class Configuration {
    @Value("${app.finnhub.baseUrl}")
    private String finnhubBaseUrl;

    @Value("${app.finnhub.quote.uri}")
    private String finnhubQuoteUri;

    @Value("${app.finnhub.quote.method}")
    private HttpMethod finnhubQuoteMethod;

    @Value("${app.finnhub.quote.headers}")
    private String finnhubQuoteHeaders;
}
