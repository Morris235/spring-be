package com.pive.springbe.service;

import com.pive.springbe.dto.CalculatorDTO;
import org.springframework.stereotype.Service;

@Service
public class CalculatorService {
    public int plusTwoNumbers(CalculatorDTO calculatorDTO) {
        return calculatorDTO.getNum1() + calculatorDTO.getNum2();
    }
}
