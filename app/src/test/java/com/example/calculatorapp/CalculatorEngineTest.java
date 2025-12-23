package com.example.calculatorapp;

import org.junit.Test;
import static org.junit.Assert.*;

public class CalculatorEngineTest {

    private final CalculatorEngine engine = new CalculatorEngine();

    @Test
    public void addition_worksCorrectly() {
        Double result = engine.compute(2.0, 3.0, "+");
        assertNotNull(result);
        assertEquals(5.0, result, 0.0001);
    }

    @Test
    public void divisionByZero_returnsNull() {
        Double result = engine.compute(10.0, 0.0, "/");
        assertNull(result);
    }

    @Test
    public void sqrt_negativeNumber_returnsNull() {
        Double result = engine.sqrt(-4.0);
        assertNull(result);
    }

    @Test
    public void reciprocal_zero_returnsNull() {
        Double result = engine.reciprocal(0.0);
        assertNull(result);
    }

    @Test
    public void percent_withoutFirstOperand() {
        double result = engine.percent(0.0, 50.0, false);
        assertEquals(0.5, result, 0.0001);
    }

    @Test
    public void percent_withFirstOperand() {
        double result = engine.percent(200.0, 10.0, true);
        assertEquals(20.0, result, 0.0001);
    }
}
