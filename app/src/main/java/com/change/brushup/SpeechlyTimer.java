package com.change.brushup;

import android.util.Log;
import android.os.Handler;

import java.util.StringTokenizer;


/**
 * Created by harekamsingh on 1/25/16.
 */
public abstract class SpeechlyTimer implements Runnable {
    private long timeRemaining;
    private Handler handler;
    private boolean isKilled = false;

    SpeechlyTimer(Handler handler) {
        this.handler = handler;
        this.timeRemaining = 0;
    }

    SpeechlyTimer(Handler handler, long timeRemaining) {
        this.handler = handler;
        this.timeRemaining = timeRemaining;
    }

    public static boolean isValidInput(String timeInput) {
        if (timeInput == null || timeInput.isEmpty())
            return false;
        timeInput = timeInput.trim();
        int index = timeInput.indexOf(":");
        if (timeInput.length() == 5 && index == 2) {
            try {
                return extractTotalDuration(timeInput) > 30;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    public static long convertToMilliseconds(String timeInput) {
        try {
            return extractTotalDuration(timeInput) * 1000;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static int extractMinutes(String timeInput) {
        return Integer.parseInt(timeInput.substring(0, 2));
    }

    private static int extractSeconds(String timeInput) {
        return Integer.parseInt(timeInput.substring(3, timeInput.length()));
    }

    private static long extractTotalDuration(String timeInput) {
        return extractMinutes(timeInput) * 60 + extractSeconds(timeInput);
    }


    public static String convertToString(long timeInput) {
        int totalSeconds = (int) (timeInput / 1000);
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        String minutesString = (minutes < 10) ? "0" + minutes : minutes + "";
        String secondsString = (seconds < 10) ? "0" + seconds : seconds + "";
        return minutesString + ":" + secondsString;
    }

    public void start() {
        isKilled = false;
        handler.postDelayed(this, 1000);
    }

    public void setTimeRemaining(long timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    public void stop() {
        isKilled = true;
        onTimerStopped();
    }

    public abstract void onTimerStopped();

    @Override
    public void run() {
        if (!isKilled) {
            Log.d("CHANGE", "run called | time remaining" + timeRemaining);
            updateUI(timeRemaining);
            if (timeRemaining == 30000)
                onPlayNotification();
            timeRemaining -= 1000;
            if (timeRemaining >= 0)
                handler.postDelayed(this, 1000);
            else
                onTimerFinished();

        }
    }

    public abstract void onPlayNotification();

    public abstract void onTimerFinished();

    public abstract void updateUI(long timeRemaining);
}
