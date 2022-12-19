package com.vatsal.project.realtimestockupdates.controller;

import com.vatsal.project.realtimestockupdates.service.QuotesService;
import com.vatsal.project.realtimestockupdates.dto.StatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.constraints.Pattern;

/**
 * A REST controller for streaming live stock updates.
 * @author imvtsl
 * @since v1.0
 */

@Slf4j
@Controller
@RequestMapping(value = "/quotes/v1")
public class QuotesController {
    @Autowired
    private QuotesService quotesService;

    @Operation(summary = "Subscribe to the updates for the given symbol")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stream of updates",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StatusResponse.class))})})
    @GetMapping(value = "/subscribe/{username}/symbols/{symbol}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> submitRequest(@PathVariable("username") @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "'${validatedValue}' is not a valid user name. Valid user name matches regex: ^[A-Za-z0-9_]+$") String userName, @PathVariable("symbol") String symbol, @RequestHeader("Token") String token) {
        return ResponseEntity.ok(quotesService.submitRequest(userName, symbol, token));
    }

    @Operation(summary = "Deregister the given symbol for the given user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unsubscribed to the given symbol for the given user",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StatusResponse.class))}),
            @ApiResponse(responseCode = "404", description = "The given user or the given symbol not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StatusResponse.class))})
    })
    @DeleteMapping(value = "/deregister/{userName}/{symbol}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StatusResponse> deregisterUser(@PathVariable @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "'${validatedValue}' is not a valid user name. Valid user name matches regex: ^[A-Za-z0-9_]+$") String userName, @PathVariable String symbol) {
        StatusResponse statusResponse = quotesService.deregisterSymbol(userName, symbol);
        if (Boolean.TRUE.equals(statusResponse.getStatus())) {
            return ResponseEntity.ok(statusResponse);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(statusResponse);
        }
    }

    @Operation(summary = "Deregister the given user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted the given user",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StatusResponse.class))}),
            @ApiResponse(responseCode = "404", description = "The given user not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StatusResponse.class))})
    })
    @DeleteMapping(value = "/deregister/{userName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StatusResponse> deregisterUser(@PathVariable @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "'${validatedValue}' is not a valid user name. Valid user name matches regex: ^[A-Za-z0-9_]+$") String userName) {
        StatusResponse statusResponse = quotesService.deregisterUser(userName);
        if (Boolean.TRUE.equals(statusResponse.getStatus())) {
            return ResponseEntity.ok(statusResponse);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(statusResponse);
        }
    }
}
