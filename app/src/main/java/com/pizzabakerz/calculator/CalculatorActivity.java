package com.pizzabakerz.calculator;

import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.content.*;
import android.hardware.*;


import java.util.regex.Pattern;

public class CalculatorActivity extends AppCompatActivity implements SensorEventListener {

    //variable declaration
    private TextView display;
    private String input = "";
    private String operator = "";
    private String resultStatus = "";

    //sensor variable
    private static final int shake = 1000;
    long lastUpdate = 0;
    private SensorManager sensorManager;
    private Sensor senAccl;
    private float last_x, last_y, last_z;

    //vibrate
    public Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        //calculator text view
        display = (TextView) findViewById(R.id.display);

        //sensor associates
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccl = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, senAccl, SensorManager.SENSOR_DELAY_UI);

        //vibrate support
        vibrator = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
    }

    //functions to be performed
    private void updateDisplay() {
        display.setText(input);//to update display
    }

    private void clear() {        //to clear display
        input = "";
        operator = "";
        updateDisplay();
        resultStatus = "";
    }

    private double operation(String ipOne, String ipTwo, String operator) {
        switch (operator) {
            case "+":
                return Double.valueOf(ipOne) + Double.valueOf(ipTwo);
            case "-":
                return Double.valueOf(ipOne) - Double.valueOf(ipTwo);
            case "/":
                try {
                    return Double.valueOf(ipOne) / Double.valueOf(ipTwo);
                } catch (Exception e) {
                    Log.d("calc", e.getMessage());
                }
            case "x":
                return Double.valueOf(ipOne) * Double.valueOf(ipTwo);
            default:
                return -1;
        }
    }

    private boolean operator_is(char operator) {
        switch (operator) {
            case '+':
            case '-':
            case 'x':
            case '/':
                return true;
            default:
                return false;
        }
    }

    private boolean getResult() {
        if (operator == "") return false;
        String[] operationPerformed = input.split(Pattern.quote(operator));
        if (operationPerformed.length < 2) return false;
        resultStatus = String.valueOf(operation(operationPerformed[0], operationPerformed[1], operator));
        return true;
    }

    //on click listeners
    public void onClickNumber(View v) {
        if (resultStatus != "") {
            clear();
            updateDisplay();
        }
        Button button = (Button) v;
        input += button.getText();
        updateDisplay();
    }

    public void onClickOperator(View v) {
        if (input == "") return;

        Button button = (Button) v;

        if (resultStatus != "") {
            String result = resultStatus;
            clear();
            input = result;
        }

        if (operator != "") {
            Log.d("calc", "" + input.charAt(input.length() - 1));
            if (operator_is(input.charAt(input.length() - 1))) {
                input = input.replace(input.charAt(input.length() - 1), button.getText().charAt(0));
                updateDisplay();
                return;
            } else {
                getResult();
                input = resultStatus;
                resultStatus = "";
            }
            operator = button.getText().toString();
        }
        input += button.getText();
        operator = button.getText().toString();
        updateDisplay();
    }

    /* omitted can be used for testing
    public void onClickClear(View v){
        clear();
        updateDisplay();
    }
    */

    public void onClickEqual(View v) {
        if (input == "") return;
        if (!getResult()) return;
        display.setText(input + "\n" + String.valueOf(resultStatus));
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySen = sensorEvent.sensor;
        if (mySen.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            long time = System.currentTimeMillis();
            if ((time - lastUpdate) > 100) {
                long diftime = (time - lastUpdate);
                lastUpdate = time;
                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diftime * 10000;
                if (speed > shake) {
                    clear();
                    updateDisplay();
                    vibrator.vibrate(1000);
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //not used
    }
}
