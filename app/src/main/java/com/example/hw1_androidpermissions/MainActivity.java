package com.example.hw1_androidpermissions;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.KeyguardManager;
import android.bluetooth.BluetoothAdapter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private TextInputEditText main_ET_password;
    private MaterialButton main_BTN_login;
    private MaterialTextView main_LBL_constraints;
    private String txt;
    private int result = 1;
    private static final int BATTERY_NOT_MATCHES = 3;
    private static final int BT_TURNED_OFF = 5;
    private static final int PHONE_ON_MUTE = 7;
    private static final int PHONE_NOT_SECURED = 11;
    private static final int NUMBER_FORMAT_EXCEPTION = 13;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        setConstraintText();
        main_BTN_login.setOnClickListener(v-> loginUser());
    }

    private void loginUser() {
        String password = main_ET_password.getText().toString().trim();
        result = 1;
        result = checkParametes(password);
        if(result == 1)
            Toast.makeText(this,"Success! ", Toast.LENGTH_SHORT).show();
        else{
            ArrayList<String> failureReasons = decodeLoginFailuresReasons(result);
            displayLoginFailureDialog(failureReasons);
        }

    }

    private void displayLoginFailureDialog(ArrayList<String> failureReasons) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Login Failed");

        StringBuilder message = new StringBuilder();
        for (String reason : failureReasons)
            message.append(reason).append("\n");

        builder.setMessage(message.toString());
        builder.setPositiveButton("OK",(dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private ArrayList<String> decodeLoginFailuresReasons(int result) {
        ArrayList<String> reasons = new ArrayList<>();
        if(result % BATTERY_NOT_MATCHES == 0)
            reasons.add(" Password does not match battery level. ");
        if(result % BT_TURNED_OFF == 0)
            reasons.add(" Bluetooth is turned off. ");
        if(result % PHONE_ON_MUTE == 0)
            reasons.add(" Phone is on mute. ");
        if(result % PHONE_NOT_SECURED == 0)
            reasons.add(" Phone is not secured ");
        if(result % NUMBER_FORMAT_EXCEPTION == 0)
            reasons.add(" Password must be a numeric value ");

        return reasons;
    }

    private int checkParametes(String password) {
        int batteryLevel = getBattryLevel();
        boolean isBluetooth = getBluetoothStatus();
        boolean isPhoneNotMute = getPhoneAudioStatus();
        boolean isDeviceSecured = getDeviceSecurityStatus();

        try {
            int passwordAsNumber = Integer.parseInt(password);
            if(passwordAsNumber != batteryLevel)
                result *= BATTERY_NOT_MATCHES;
            if(!isBluetooth)
                result*=BT_TURNED_OFF;
            if(!isPhoneNotMute)
                result*=PHONE_ON_MUTE;
            if(!isDeviceSecured)
                result*=PHONE_NOT_SECURED;
        }
        catch (NumberFormatException e){
            result *=NUMBER_FORMAT_EXCEPTION;
        }
        return result;
    }

    private boolean getDeviceSecurityStatus() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        return keyguardManager.isDeviceSecure();
    }

    private boolean getPhoneAudioStatus() {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        return audioManager != null && audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0;
    }

    private boolean getBluetoothStatus() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }


    private int getBattryLevel() {
        BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }

    private void setConstraintText() {
        txt = "Constraints:\n 1. Password matches battery level.\n 2. Device's Bluetooth is turned on. \n 3. Device's not in mute \n 4. Device is secured with a screen lock.";
        main_LBL_constraints.setText(txt);
    }

    private void findViews() {
        main_ET_password = findViewById(R.id.main_ET_password);
        main_BTN_login = findViewById(R.id.main_BTN_login);
        main_LBL_constraints = findViewById(R.id.main_LBL_constraints);
    }
}