package com.example.simplecalculator;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_CURRENT = "current";
    private static final String KEY_EXPRESSION = "expression";
    private static final String KEY_STORED = "stored";
    private static final String KEY_OPERATOR = "operator";
    private static final String KEY_START_NEW = "startNew";
    private static final String KEY_ERROR = "error";

    private final Calculator calculator = new Calculator();
    private TextView tvDisplay;
    private TextView tvExpression;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDisplay = findViewById(R.id.tvDisplay);
        tvExpression = findViewById(R.id.tvExpression);

        if (savedInstanceState != null) {
            calculator.restore(
                    savedInstanceState.getString(KEY_CURRENT, "0"),
                    savedInstanceState.getString(KEY_EXPRESSION, ""),
                    savedInstanceState.getString(KEY_STORED),
                    (char) savedInstanceState.getInt(KEY_OPERATOR, 0),
                    savedInstanceState.getBoolean(KEY_START_NEW),
                    savedInstanceState.getBoolean(KEY_ERROR));
        }

        // One listener for every key. Each button's android:tag says which key it is.
        View.OnClickListener listener = v -> {
            handleKey((String) v.getTag());
            refresh();
        };
        attachListener(findViewById(R.id.keypad), listener);

        refresh();
    }

    /** Walks through the keypad and gives every tagged button the same click listener. */
    private void attachListener(View view, View.OnClickListener listener) {
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                attachListener(group.getChildAt(i), listener);
            }
        } else if (view.getTag() != null) {
            view.setOnClickListener(listener);
        }
    }

    private void handleKey(String key) {
        switch (key) {
            case "C":    calculator.clear(); break;
            case "back": calculator.backspace(); break;
            case "%":    calculator.percent(); break;
            case "neg":  calculator.plusMinus(); break;
            case ".":    calculator.inputDecimal(); break;
            case "=":    calculator.equals(); break;
            case "+":
            case "-":
            case "*":
            case "/":
                calculator.inputOperator(key.charAt(0));
                break;
            default:     // "0" to "9"
                calculator.inputDigit(key.charAt(0));
                break;
        }
    }

    private void refresh() {
        tvDisplay.setText(calculator.getDisplay());
        tvExpression.setText(calculator.getExpression());
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CURRENT, calculator.getDisplay());
        outState.putString(KEY_EXPRESSION, calculator.getExpression());
        outState.putString(KEY_STORED, calculator.getStored());
        outState.putInt(KEY_OPERATOR, calculator.getOperator());
        outState.putBoolean(KEY_START_NEW, calculator.isStartNew());
        outState.putBoolean(KEY_ERROR, calculator.isError());
    }
}
