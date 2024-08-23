package com.example.connectionalarm.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.speech.tts.TextToSpeech;
import android.widget.EditText;
import android.widget.TextView;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private TextView editTextStatus;
    private TextToSpeech textToSpeech;

    public NetworkChangeReceiver(TextView editTextStatus,TextToSpeech textToSpeech) {
        this.editTextStatus = editTextStatus;
        this.textToSpeech = textToSpeech;
    }
    @Override
    public void onReceive(final Context context, final Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

//         String statusMessage;

        if (activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
            if (activeNetwork.isConnected()) {
                // Check if internet is available
                new Thread(() -> {
                    boolean hasInternet = hasInternetAccess();
                    editTextStatus.post(() -> {
                        if (hasInternet) {
                            editTextStatus.setText("Wi-Fi is connected with internet");
                            speakOut("Wi-Fi is connected with internet");
                        } else {
                            editTextStatus.setText( "Wi-Fi is connected but no internet");
                            speakOut("Wi-Fi is connected with internet");

                        }


                    });
                }).start();
            } else {

                editTextStatus.setText("Wi-Fi is disconnected");
                speakOut("Wi-Fi is disconnected");
            }
        } else {

            editTextStatus.setText("Wi-Fi is disconnected");
            speakOut("Wi-Fi is disconnected");
        }
    }

    private boolean hasInternetAccess() {
        try {
            Process process = Runtime.getRuntime().exec("ping -c 1 8.8.8.8");
            int returnVal = process.waitFor();
            return (returnVal == 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void speakOut(String message) {
        if (textToSpeech != null && !textToSpeech.isSpeaking()) {
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }
}