package com.vatsal.project.realtimestockupdates.rest.client;

import com.vatsal.project.realtimestockupdates.dto.FinnhubResponse;
import io.netty.handler.logging.LogLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

@Component
@Slf4j
public class Rest {
    @Autowired
    private Configuration configuration;

    public FinnhubResponse sendRequestFinnhubQuote(String symbol, String token) {
        var builder = getWebClientBuilder();

        // host and port
        WebClient client = builder.baseUrl(configuration.getFinnhubBaseUrl()).build();
        log.info(configuration.getFinnhubBaseUrl());

        // http method
        WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec = client.method(configuration.getFinnhubQuoteMethod());
        log.info(configuration.getFinnhubQuoteMethod().toString());

        // uri
        WebClient.RequestBodySpec bodySpec = uriSpec.uri(uriBuilder -> uriBuilder
                .path(configuration.getFinnhubQuoteUri())
                .queryParam("symbol", symbol)
                .queryParam("token", token).build());
        log.info(configuration.getFinnhubQuoteUri());

        // headers
        bodySpec = bodySpec.accept(MediaType.APPLICATION_JSON);

        FinnhubResponse result;
        try {
            result = bodySpec.retrieve().bodyToMono(FinnhubResponse.class).block();
        } catch (WebClientRequestException e) {
            log.error(e.getMessage());
            throw e;
        } catch (WebClientResponseException e) {
            var responseBody = e.getResponseBodyAsString();
            var httpStatus = e.getStatusCode();
            log.error(httpStatus.toString());
            log.error(responseBody);
            throw e;
        }
        return result;
    }

    private WebClient.Builder getWebClientBuilder() {
        var httpClient = HttpClient
                .create()
                .wiretap("reactor.netty.http.client.HttpClient",
                        LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL);

        return WebClient
                .builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient));
    }
}
