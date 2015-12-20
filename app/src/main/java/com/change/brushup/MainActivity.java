package com.change.brushup;

import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, DialogInterface.OnClickListener {

    private TextView mTextTime;
    private Handler mCurrentHandler;
    private MediaPlayer mediaPlayer;
    private ToggleButton toggleButton;
    private EditText mTextInputFromUser;
    private SpeechlyTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mediaPlayer = new MediaPlayer();
        mTextTime = (TextView) findViewById(R.id.textView);
        AssetManager assetManager = getAssets();
        Typeface customFont = Typeface.createFromAsset(assetManager, "fonts/source_sans_pro.light.ttf");
        mTextTime.setTypeface(customFont);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        toggleButton.setOnCheckedChangeListener(this);
        mCurrentHandler = new Handler();
        timer = new SpeechlyTimer(mCurrentHandler) {
            @Override
            public void onTimerStopped() {
                mTextTime.setText(R.string.defaultTime);
                //stopSound();
            }

            @Override
            public void onPlayNotification() {
                Log.d("SOUND", "on 30 seconds sound played");
                playSound();
            }

            @Override
            public void onTimerFinished() {
                toggleButton.setChecked(false);
            }

            @Override
            public void updateUI(long timeRemaining) {
                mTextTime.setText(SpeechlyTimer.convertToString(timeRemaining));
            }
        };

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void playSound() {
        try {
            AssetFileDescriptor assetFileDescriptor = getAssets().openFd("sounds/bellSoundRing.mp3");
            mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopSound() {
        mediaPlayer.stop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            View view = layoutInflater.inflate(R.layout.user_layout, null);
            mTextInputFromUser = (EditText) view.findViewById(R.id.text_input);
//            Toast.makeText(this, "ON", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Please enter the time");
            builder.setView(view);
            builder.setCancelable(false);
            builder.setPositiveButton("OK", this);
            builder.setNegativeButton("Cancel", this);
            builder.show();
        } else {
//            Toast.makeText(this, "OFF", Toast.LENGTH_SHORT).show();
            timer.stop();
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                String input = mTextInputFromUser.getText().toString();
                if (SpeechlyTimer.isValidInput(input)) {
                    long milliseconds = SpeechlyTimer.convertToMilliseconds(input);
                    timer.setTimeRemaining(milliseconds);
                    timer.start();
                } else {
                    Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
                    toggleButton.setChecked(false);
                }
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                toggleButton.setChecked(false);
                break;
        }
    }
}
