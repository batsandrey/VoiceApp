package com.batsandrey.voiceapp;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

public class FlashlightActivity extends AppCompatActivity {

    private static final String SCREEN_BRIGHTNESS_VALUE_PREFIX = "Current device screen brightness value is ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashlight);

        setTitle("Screen Brightness");

        final TextView screenBrightnessValueTextView = findViewById(R.id.change_screen_brightness_value_text_view);


//         Get the seekbar instance.
        SeekBar seekBar = findViewById(R.id.change_screen_brightness_seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                Context context = getApplicationContext();

                boolean canWriteSettings = false;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    canWriteSettings = Settings.System.canWrite(context);
                }


                if(canWriteSettings) {

                    // Because max screen brightness value is 255
                    // But max seekbar value is 100, so need to convert.
                    //        float brightness = brightness / (float)255;


                    int screenBrightnessValue = i*255/100;

                    // Set seekbar adjust screen brightness value in the text view.
                    screenBrightnessValueTextView.setText(SCREEN_BRIGHTNESS_VALUE_PREFIX + screenBrightnessValue);

                    // Change the screen brightness change mode to manual.
                    Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                    // Apply the screen brightness value to the system, this will change the value in Settings ---> Display ---> Brightness level.
                    // It will also change the screen brightness for the device.
                    Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, screenBrightnessValue);
                }else
                {
                    // Show Can modify system settings panel to let user add WRITE_SETTINGS permission for this app.
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    context.startActivity(intent);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Getting Current screen brightness.
        int currBrightness = Settings.System.getInt(getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,0);
        // Set current screen brightness value in the text view.
        screenBrightnessValueTextView.setText( SCREEN_BRIGHTNESS_VALUE_PREFIX + currBrightness);
        // Set current screen brightness value to seekbar progress.
        seekBar.setProgress(currBrightness);
    }

//    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//    WindowManager.LayoutParams params = getWindow().getAttributes();
//    params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
//    getWindow().setAttributes(params);
}
