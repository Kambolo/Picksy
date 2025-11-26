package com.picksy.decisionservice.controller;

import com.picksy.decisionservice.model.PollDTO;
import com.picksy.decisionservice.service.DecisionService;
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

    @GetMapping("/secure/room/{roomCode}")
    public ResponseEntity<List<PollDTO>> getResults(@PathVariable String roomCode) {
        return ResponseEntity.ok().body(decisionService.getResults(roomCode));
    }

}