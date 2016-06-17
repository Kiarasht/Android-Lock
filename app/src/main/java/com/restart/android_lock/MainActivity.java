package com.restart.android_lock;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;

@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity {
    private String TAG = ".MainActivity";
    private SharedPreferences sharedPref;
    private TextView password;
    private String savepassword = "My super secret password!";
    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        password = (TextView) findViewById(R.id.textView);
        password.setVisibility(View.INVISIBLE);
        sharedPref = getSharedPreferences("savefile", MODE_PRIVATE);

        mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
        mPinLockView.setPinLockListener(mPinLockListener);
        mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);
        mPinLockView.attachIndicatorDots(mIndicatorDots);
    }

    protected void onResume() {
        super.onResume();
        password.setVisibility(View.INVISIBLE);
        password.setText(savepassword);
    }

    protected void onStart() {
        super.onStart();
        if (sharedPref.getBoolean(getString(R.string.setUp), true)) {
            Toast.makeText(getApplicationContext(), "No PIN on record. Set one!", Toast.LENGTH_SHORT).show();
        }
    }

    protected void onStop() {
        super.onStop();
        password.setText("Now you see me, now you don't!");
        password.setVisibility(View.INVISIBLE);
    }

    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            mPinLockView.resetPinLockView();
            if (sharedPref.getBoolean(getString(R.string.setUp), true)) {
                sharedPref.edit().putString(getString(R.string.pinNumber), pin).apply();
                sharedPref.edit().putBoolean(getString(R.string.setUp), false).apply();
                Toast.makeText(getApplicationContext(), "PIN Set! The text is now protected.", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "Returning to the app now requires the PIN to view the text.", Toast.LENGTH_LONG).show();
                password.setVisibility(View.VISIBLE);
                return;
            } else if (sharedPref.getString(getString(R.string.pinNumber), "00000").equals(pin)) {
                Toast.makeText(getApplicationContext(), "PIN Correct!", Toast.LENGTH_SHORT).show();
                password.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(getApplicationContext(), "PIN Incorrect!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onEmpty() {
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
        }
    };
}
