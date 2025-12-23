package com.example.calculatorapp;

public class CalculatorEngine {

    public Double compute(double a, double b, String op) {
        switch (op) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/":
                if (b == 0.0) return null;
                return a / b;
            default:
                return null;
        }
    }

    public Double sqrt(double value) {
        if (value < 0) return null;
        return Math.sqrt(value);
    }

    public Double reciprocal(double value) {
        if (value == 0.0) return null;
        return 1.0 / value;
    }

    public double percent(double firstOperandOrIgnored, double value, boolean hasFirstOperand) {
        if (hasFirstOperand) {
            return firstOperandOrIgnored * (value / 100.0);
        }
        return value / 100.0;
    }
}
