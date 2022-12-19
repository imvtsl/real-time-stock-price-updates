package com.vatsal.project.realtimestockupdates.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

/**
 * DTO class for streaming stock price updates to the subscriber.
 * @author imvtsl
 * @since v1.0
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "currentPrice",
    "change",
    "percentChange",
    "high",
    "low",
    "open",
    "previousClose",
    "timestamp"
})

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuoteResponse {
    @JsonProperty("currentPrice")
    public Double currentPrice;

    @JsonProperty("change")
    public Double change;

    @JsonProperty("percentChange")
    public Double percentChange;

    @JsonProperty("high")
    public Double high;

    @JsonProperty("low")
    public Double low;

    @JsonProperty("open")
    public Double open;

    @JsonProperty("previousClose")
    public Double previousClose;

    @JsonProperty("timestamp")
    public Long timestamp;

}
