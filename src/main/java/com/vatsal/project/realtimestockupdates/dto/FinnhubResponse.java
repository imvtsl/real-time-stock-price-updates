package com.vatsal.project.realtimestockupdates.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO class for reading stock price from finnhub.io.
 * @author imvtsl
 * @since v1.0
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "c",
    "d",
    "dp",
    "h",
    "l",
    "o",
    "pc",
    "t"
})

@Getter
@Setter
@ToString
public class FinnhubResponse {
    @JsonProperty("c")
    public Double currentPrice;

    @JsonProperty("d")
    public Double change;

    @JsonProperty("dp")
    public Double percentChange;

    @JsonProperty("h")
    public Double high;

    @JsonProperty("l")
    public Double low;

    @JsonProperty("o")
    public Double open;

    @JsonProperty("pc")
    public Double previousClose;

    @JsonProperty("t")
    public Long timestamp;

}
