package com.example.jeromekelman.InstaGraph;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;

import static java.lang.Thread.sleep;


public class DisplayMessageActivity extends ActionBarActivity {
    public final static int DOT = 150;
    public final static int DASH = 3 * DOT;
    public final static int SPACE = 5 * DOT;
    public final static int A = 65;
    public final static String[] LETTERS = {
            ".-",   // A
            "-...", // B
            "-.-.", // C
            "-..",  // D
            ".",    // E
            "..-.", // F
            "--.",  // G
            "....", // H
            "..",   // I
            ".---", // J
            "-.-",  // K
            ".-..", // L
            "--",   // M
            "-.",   // N
            "---",  // O
            ".--.", // P
            "--.-", // Q
            ".-.",  // R
            "...",  // S
            "-",    // T
            "..-",  // U
            "...-", // V
            ".--",  // W
            "-..-", // X
            "-.--", // Y
            "--.."};// Z
    public final static String[] NUMS = {
            "-----", // 0
            ".----", // 1
            "..---", // 2
            "...--", // 3
            "....-", // 4
            ".....", // 5
            "-....", // 6
            "--...", // 7
            "---..", // 8
            "----."};// 9

    private boolean hasFlash;
    private Camera camera;
    Parameters params;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!hasFlash) {
            // device doesn't support flash
            // Show alert message and close the application
            AlertDialog alert = new AlertDialog.Builder(DisplayMessageActivity.this)
                    .create();
            alert.setTitle("Error");
            alert.setMessage("Sorry, your device doesn't support flash light!");
            alert.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // closing the application
                    finish();
                }
            });
            alert.show();
            return;
        }

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        getCamera();

        TextView textView = new TextView(this);
        textView.setTextSize(50);
        textView.setText(message);
        setContentView(textView);


        doTheThing(message);
    }

    private void doTheThing(String message) {
        message = convertToMorse(message);
        if (camera == null || params == null) return;

        for (char nxtChar : message.toCharArray()) {
            try {
                switch (nxtChar) {
                    case '.':
                        params = camera.getParameters();
                        params.setFlashMode(Parameters.FLASH_MODE_TORCH);
                        camera.setParameters(params);
                        camera.startPreview();
                        Thread.sleep(DOT);
                        break;
                    case '-':
                        params = camera.getParameters();
                        params.setFlashMode(Parameters.FLASH_MODE_TORCH);
                        camera.setParameters(params);
                        camera.startPreview();
                        Thread.sleep(DASH);
                        break;
                    case ' ':
                        Thread.sleep(DOT);
                        break;
                    case '/':
                        Thread.sleep(SPACE);
                        break;
                    default:
                }
                params = camera.getParameters();
                params.setFlashMode(Parameters.FLASH_MODE_OFF);
                camera.setParameters(params);
                camera.stopPreview();
                Thread.sleep(DOT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        camera.release();
    }

    private String convertToMorse(String message) {
        // replace all the letters
        message = message.replaceAll(" ", "/").toUpperCase();
        for (char i = 'A'; i <= 'Z'; i++) {
            message = message.replaceAll("" + i, LETTERS[i - A] + " ");
        }

        // replace the numbers
        for (int i = 0; i <= 9; i++) {
            message = message.replaceAll("" + i, NUMS[i] + " ");
        }

        return message;
    }

    private void getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                params = camera.getParameters();
            } catch (RuntimeException e) {
                Log.e("Camera Error. Failed to Open. Error: ", e.getMessage());
            }
        }
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

    protected void onStop() {
        super.onStop();

        // on stop release the camera
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }
}
