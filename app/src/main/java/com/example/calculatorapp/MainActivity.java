package com.example.calculatorapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private TextView txtDisplay;

    // Input state
    private StringBuilder currentInput;      // what user is typing now
    private Double firstOperand;             // stored operand for + - * /
    private String pendingOperator;          // "+", "-", "*", "/"
    private boolean isNewInput;              // if true: next digit starts new number
    private boolean isError;                 // if true: display shows Error, only C/CE works properly

    // Memory state
    private double memoryValue;
    private boolean hasMemory;

    // Format numbers nicely (avoid trailing .0 if possible)
    private final DecimalFormat numberFormat = new DecimalFormat("0.##########");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtDisplay = findViewById(R.id.txtDisplay);

        currentInput = new StringBuilder();
        resetAll();

        // ---- Digit buttons ----
        setDigitClick(R.id.btn0, "0");
        setDigitClick(R.id.btn1, "1");
        setDigitClick(R.id.btn2, "2");
        setDigitClick(R.id.btn3, "3");
        setDigitClick(R.id.btn4, "4");
        setDigitClick(R.id.btn5, "5");
        setDigitClick(R.id.btn6, "6");
        setDigitClick(R.id.btn7, "7");
        setDigitClick(R.id.btn8, "8");
        setDigitClick(R.id.btn9, "9");

        // Dot
        Button btnDot = findViewById(R.id.btnDot);
        btnDot.setOnClickListener(v -> onDotPressed());

        // Operators
        Button btnPlus = findViewById(R.id.btnPlus);
        Button btnMinus = findViewById(R.id.btnMinus);
        Button btnMultiply = findViewById(R.id.btnMultiply);
        Button btnDivide = findViewById(R.id.btnDivide);

        btnPlus.setOnClickListener(v -> onOperatorPressed("+"));
        btnMinus.setOnClickListener(v -> onOperatorPressed("-"));
        btnMultiply.setOnClickListener(v -> onOperatorPressed("*"));
        btnDivide.setOnClickListener(v -> onOperatorPressed("/"));

        // Equals
        Button btnEquals = findViewById(R.id.btnEquals);
        btnEquals.setOnClickListener(v -> onEqualsPressed());

        // Special required ops: Back, Clear, Sign, Sqrt
        Button btnBack = findViewById(R.id.btnBack);
        Button btnCE = findViewById(R.id.btnCE);
        Button btnC = findViewById(R.id.btnC);
        Button btnSign = findViewById(R.id.btnSign);
        Button btnSqrt = findViewById(R.id.btnSqrt);

        btnBack.setOnClickListener(v -> onBackPressedCalculator());
        btnCE.setOnClickListener(v -> onClearEntry());
        btnC.setOnClickListener(v -> resetAll());
        btnSign.setOnClickListener(v -> onSignChange());
        btnSqrt.setOnClickListener(v -> onSqrt());

        // Extra (in screenshot): % and 1/x
        Button btnPercent = findViewById(R.id.btnPercent);
        Button btnReciprocal = findViewById(R.id.btnReciprocal);
        btnPercent.setOnClickListener(v -> onPercent());
        btnReciprocal.setOnClickListener(v -> onReciprocal());

        // Memory buttons
        Button btnMC = findViewById(R.id.btnMC);
        Button btnMR = findViewById(R.id.btnMR);
        Button btnMS = findViewById(R.id.btnMS);
        Button btnMPlus = findViewById(R.id.btnMPlus);
        Button btnMMinus = findViewById(R.id.btnMMinus);

        btnMC.setOnClickListener(v -> onMemoryClear());
        btnMR.setOnClickListener(v -> onMemoryRecall());
        btnMS.setOnClickListener(v -> onMemoryStore());
        btnMPlus.setOnClickListener(v -> onMemoryAdd());
        btnMMinus.setOnClickListener(v -> onMemorySubtract());
    }


    private void setDigitClick(int buttonId, String digit) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> onDigitPressed(digit));
    }

    private void showError() {
        isError = true;
        txtDisplay.setText("Error");
    }

    private void setDisplayFromInput() {
        if (currentInput.length() == 0) {
            txtDisplay.setText("0");
        } else {
            txtDisplay.setText(currentInput.toString());
        }
    }

    private void resetAll() {
        currentInput.setLength(0);
        firstOperand = null;
        pendingOperator = null;
        isNewInput = false;
        isError = false;
        txtDisplay.setText("0");
    }

    private double getCurrentValueOrZero() {
        if (currentInput.length() == 0) return 0.0;
        try {
            return Double.parseDouble(currentInput.toString());
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }

    private void setCurrentValue(double value) {
        currentInput.setLength(0);
        currentInput.append(numberFormat.format(value));
        txtDisplay.setText(currentInput.toString());
        isNewInput = true; // next digit starts fresh
    }

    private void clearCurrentOnly() {
        currentInput.setLength(0);
        txtDisplay.setText("0");
        isNewInput = false;
        isError = false;
    }

    // -----------------------------
    // Input events (digits, dot)
    // -----------------------------

    private void onDigitPressed(String digit) {
        // If we are in error state, typing a digit should start fresh
        if (isError) {
            resetAll();
        }

        // If last action was "=" or an operator produced a displayed result,
        // starting a new digit should clear the input
        if (isNewInput) {
            currentInput.setLength(0);
            isNewInput = false;
        }

        // Avoid leading zeros like "0002"
        if (currentInput.length() == 1 && currentInput.charAt(0) == '0' && !currentInput.toString().contains(".")) {
            currentInput.setLength(0);
        }

        currentInput.append(digit);
        setDisplayFromInput();
    }

    private void onDotPressed() {
        if (isError) {
            resetAll();
        }

        if (isNewInput) {
            currentInput.setLength(0);
            isNewInput = false;
        }

        // Only one dot allowed
        if (currentInput.toString().contains(".")) {
            return;
        }

        // If empty, start with "0."
        if (currentInput.length() == 0) {
            currentInput.append("0");
        }

        currentInput.append(".");
        setDisplayFromInput();
    }

    // -----------------------------
    //  Operators and equals
    // -----------------------------

    private void onOperatorPressed(String operator) {
        if (isError) return;

        double currentValue = getCurrentValueOrZero();

        // Case A: first time pressing an operator
        if (firstOperand == null) {
            firstOperand = currentValue;
            pendingOperator = operator;
            isNewInput = true; // next digit starts new number
            return;
        }

        // Case B: we already have firstOperand and pendingOperator
        // If user has typed a new number (not just pressing operators repeatedly),
        // compute intermediate result.
        if (!isNewInput) {
            Double result = compute(firstOperand, currentValue, pendingOperator);
            if (result == null) {
                showError();
                return;
            }
            firstOperand = result;
            txtDisplay.setText(numberFormat.format(result));
        }

        // Update operator to the newly pressed one
        pendingOperator = operator;
        isNewInput = true;
    }

    private void onEqualsPressed() {
        if (isError) return;

        if (firstOperand == null || pendingOperator == null) {
            // Nothing to compute
            return;
        }

        double secondOperand = getCurrentValueOrZero();
        Double result = compute(firstOperand, secondOperand, pendingOperator);

        if (result == null) {
            showError();
            return;
        }

        txtDisplay.setText(numberFormat.format(result));

        // After equals: reset operator chain, but keep result as current input
        currentInput.setLength(0);
        currentInput.append(numberFormat.format(result));

        firstOperand = null;
        pendingOperator = null;
        isNewInput = true;
    }

    private Double compute(double a, double b, String op) {
        switch (op) {
            case "+":
                return a + b;
            case "-":
                return a - b;
            case "*":
                return a * b;
            case "/":
                if (b == 0.0) return null;
                return a / b;
            default:
                return null;
        }
    }

    // -----------------------------
    //  Required special operations
    // -----------------------------

    private void onBackPressedCalculator() {
        if (isError) {
            resetAll();
            return;
        }

        if (isNewInput) {
            // If result is shown and user hits back, treat it like editing the shown value
            isNewInput = false;
        }

        if (currentInput.length() > 0) {
            currentInput.deleteCharAt(currentInput.length() - 1);
        }
        setDisplayFromInput();
    }

    private void onClearEntry() {
        // CE clears only the current typed number
        clearCurrentOnly();
    }

    private void onSignChange() {
        if (isError) return;

        if (currentInput.length() == 0) {
            // If empty, toggling sign on 0 -> show "-0" is weird; keep 0
            txtDisplay.setText("0");
            return;
        }

        if (currentInput.charAt(0) == '-') {
            currentInput.deleteCharAt(0);
        } else {
            currentInput.insert(0, '-');
        }
        setDisplayFromInput();
    }

    private void onSqrt() {
        if (isError) return;

        double value = getCurrentValueOrZero();
        if (value < 0) {
            showError();
            return;
        }

        double result = Math.sqrt(value);
        setCurrentValue(result);
    }

    // -----------------------------
    //  Extra buttons from screenshot
    // -----------------------------

    private void onPercent() {
        if (isError) return;

        double value = getCurrentValueOrZero();

        // Classic calculator behavior:
        // if firstOperand exists: percent = firstOperand * (value/100)
        // else: value = value/100
        double result;
        if (firstOperand != null) {
            result = firstOperand * (value / 100.0);
        } else {
            result = value / 100.0;
        }

        setCurrentValue(result);
    }

    private void onReciprocal() {
        if (isError) return;

        double value = getCurrentValueOrZero();
        if (value == 0.0) {
            showError();
            return;
        }

        double result = 1.0 / value;
        setCurrentValue(result);
    }

    // -----------------------------
    //  Memory operations
    // -----------------------------

    private void onMemoryClear() {
        memoryValue = 0.0;
        hasMemory = false;
    }

    private void onMemoryRecall() {
        if (!hasMemory) {
            // If nothing stored, recall 0 (you can choose to do nothing instead)
            setCurrentValue(0.0);
            return;
        }
        setCurrentValue(memoryValue);
    }

    private void onMemoryStore() {
        if (isError) return;

        memoryValue = getCurrentValueOrZero();
        hasMemory = true;
        isNewInput = true;
    }

    private void onMemoryAdd() {
        if (isError) return;

        memoryValue += getCurrentValueOrZero();
        hasMemory = true;
        isNewInput = true;
    }

    private void onMemorySubtract() {
        if (isError) return;

        memoryValue -= getCurrentValueOrZero();
        hasMemory = true;
        isNewInput = true;
    }
}
