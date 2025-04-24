package com.pive.springbe.controller;

import com.pive.springbe.dto.CalculatorDTO;
import com.pive.springbe.service.CalculatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class CalculatorController {
    CalculatorService calculatorService;

    @Autowired
    public CalculatorController(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "I'm alive!!!";
    }

    @PostMapping("/plus")
    public ResponseEntity<CalculatorDTO> plusTwoNumbers(@RequestBody CalculatorDTO calculatorDTO) {
        int result = calculatorService.plusTwoNumbers(calculatorDTO);
        calculatorDTO.setSum(result);
        log.info("calculatorDTO: {}", calculatorDTO);

        return ResponseEntity
                .ok()
                .body(calculatorDTO);
    }
}