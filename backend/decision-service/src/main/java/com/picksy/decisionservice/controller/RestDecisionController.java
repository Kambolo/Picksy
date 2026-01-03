package com.picksy.decisionservice.controller;

import com.picksy.decisionservice.model.PollDTO;
import com.picksy.decisionservice.service.DecisionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/decision")
@RequiredArgsConstructor
public class RestDecisionController {
    private final DecisionService decisionService;

    @Operation(
            summary = "Get voting results for a room",
            description = "Returns the final voting results for a room identified by its code."
    )
    @GetMapping("/secure/room/{roomCode}")
    public ResponseEntity<List<PollDTO>> getResults(
            @Parameter(description = "Room code", required = true)
            @PathVariable String roomCode
    ) {
        return ResponseEntity.ok(decisionService.getResults(roomCode));
    }

}