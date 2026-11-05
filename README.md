# Simple Calculator

A simple calculator app for Android, written in Java.

## Features
- Add, subtract, multiply and divide
- Decimal numbers, plus/minus (+/-) and percent (%)
- Clear (C) and backspace
- Running expression shown above the result
- Chained operations (2 + 3 + 4 = 9)
- "Cannot divide by 0" error handling
- Uses BigDecimal, so 0.1 + 0.2 gives exactly 0.3

## How to run
1. Open Android Studio -> File -> Open -> select this folder.
2. Let Gradle sync.
3. Press Run on an emulator or a phone (Android 7.0+ / API 24+).

## Project layout
- `Calculator.java`   - all the calculation logic (no Android code)
- `MainActivity.java` - connects the buttons to the calculator
- `activity_main.xml` - the keypad and display layout
