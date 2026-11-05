package com.example.simplecalculator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * All the calculator logic lives here (no Android code), so it is easy to read and test.
 * Operators are stored as ASCII chars: '+', '-', '*', '/'.
 */
public class Calculator {

    private static final int MAX_DIGITS = 15;
    private static final String DIVIDE_BY_ZERO = "Cannot divide by 0";

    private String current = "0";        // number shown on the big display
    private String expression = "";     // small line above, e.g. "12 + 5"
    private BigDecimal stored = null;    // left-hand number waiting for an operator
    private char operator = 0;           // pending operator (0 = none)
    private boolean startNew = false;    // true -> the next digit starts a new number
    private boolean error = false;

    // ------------------------------------------------------------ getters
    public String getDisplay() { return current; }
    public String getExpression() { return expression; }
    public char getOperator() { return operator; }
    public String getStored() { return stored == null ? null : stored.toPlainString(); }
    public boolean isStartNew() { return startNew; }
    public boolean isError() { return error; }

    /** Restores a saved state (used after the screen is recreated). */
    public void restore(String current, String expression, String stored,
                        char operator, boolean startNew, boolean error) {
        this.current = current;
        this.expression = expression;
        this.stored = (stored == null) ? null : new BigDecimal(stored);
        this.operator = operator;
        this.startNew = startNew;
        this.error = error;
    }

    // ------------------------------------------------------------ actions
    public void clear() {
        current = "0";
        expression = "";
        stored = null;
        operator = 0;
        startNew = false;
        error = false;
    }

    public void inputDigit(char digit) {
        if (error) clear();
        if (startNew) {
            if (operator == 0) expression = "";   // a fresh calculation after "="
            current = String.valueOf(digit);
            startNew = false;
        } else if (current.equals("0")) {
            current = String.valueOf(digit);
        } else if (digitCount(current) < MAX_DIGITS) {
            current += digit;
        }
    }

    public void inputDecimal() {
        if (error) clear();
        if (startNew) {
            if (operator == 0) expression = "";
            current = "0.";
            startNew = false;
        } else if (!current.contains(".")) {
            current += ".";
        }
    }

    public void inputOperator(char newOperator) {
        if (error) return;
        BigDecimal value = parse(current);
        if (operator != 0 && !startNew) {
            // chained calculation: 2 + 3 then "+" -> show 5 first
            try {
                stored = compute(stored, value, operator);
            } catch (ArithmeticException e) {
                showError();
                return;
            }
        } else if (operator == 0) {
            stored = value;
        }
        // (if an operator was already waiting we simply replace it)
        operator = newOperator;
        current = format(stored);
        expression = current + " " + symbol(operator);
        startNew = true;
    }

    public void equals() {
        if (error || operator == 0) return;
        BigDecimal value = parse(current);
        try {
            BigDecimal result = compute(stored, value, operator);
            expression = format(stored) + " " + symbol(operator) + " " + format(value) + " =";
            current = format(result);
        } catch (ArithmeticException e) {
            showError();
            return;
        }
        stored = null;
        operator = 0;
        startNew = true;
    }

    public void backspace() {
        if (error) {
            clear();
            return;
        }
        if (startNew) return;
        if (current.length() <= 1 || (current.length() == 2 && current.startsWith("-"))) {
            current = "0";
        } else {
            current = current.substring(0, current.length() - 1);
        }
    }

    public void percent() {
        if (error) return;
        current = format(parse(current).divide(BigDecimal.valueOf(100)));
    }

    public void plusMinus() {
        if (error || current.equals("0")) return;
        current = current.startsWith("-") ? current.substring(1) : "-" + current;
    }

    // ------------------------------------------------------------ helpers
    private BigDecimal compute(BigDecimal a, BigDecimal b, char op) {
        switch (op) {
            case '+': return a.add(b);
            case '-': return a.subtract(b);
            case '*': return a.multiply(b);
            case '/':
                if (b.compareTo(BigDecimal.ZERO) == 0) {
                    throw new ArithmeticException(DIVIDE_BY_ZERO);
                }
                return a.divide(b, 10, RoundingMode.HALF_UP);
            default:
                return b;
        }
    }

    private void showError() {
        current = DIVIDE_BY_ZERO;
        expression = "";
        stored = null;
        operator = 0;
        startNew = true;
        error = true;
    }

    private static BigDecimal parse(String text) {
        try {
            return new BigDecimal(text);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    /** 5.000 -> "5", 0.50 -> "0.5"; very long results are rounded. */
    private static String format(BigDecimal value) {
        BigDecimal clean = value.stripTrailingZeros();
        String plain = clean.toPlainString();
        if (plain.length() > MAX_DIGITS) {
            return clean.round(new MathContext(10)).stripTrailingZeros().toString();
        }
        return plain;
    }

    private static int digitCount(String text) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (Character.isDigit(text.charAt(i))) count++;
        }
        return count;
    }

    private static String symbol(char op) {
        switch (op) {
            case '*': return "\u00D7";   // multiplication sign
            case '/': return "\u00F7";   // division sign
            case '-': return "\u2212";   // minus sign
            default:  return String.valueOf(op);
        }
    }
}
