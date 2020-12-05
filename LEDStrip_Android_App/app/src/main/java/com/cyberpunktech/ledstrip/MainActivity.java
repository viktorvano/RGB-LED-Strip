package com.cyberpunktech.ledstrip;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.StrictMode;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
TextView textViewMessage;
Button buttonSend;
SeekBar seekBarRed, seekBarGreen, seekBarBlue;
int red = 0, green = 0, blue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        textViewMessage = findViewById(R.id.textViewMessage);

        buttonSend = findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendDataToServer(textViewMessage.getText().toString() + "\n");
            }
        });

        seekBarRed = findViewById(R.id.seekBarRed);
        seekBarGreen = findViewById(R.id.seekBarGreen);
        seekBarBlue = findViewById(R.id.seekBarBlue);
        seekBarRed.setMax(255);
        seekBarGreen.setMax(255);
        seekBarBlue.setMax(255);
        seekBarRed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                red = i;
                formatStringsForTextView();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBarGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                green = i;
                formatStringsForTextView();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBarBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                blue = i;
                formatStringsForTextView();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    private void formatStringsForTextView()
    {
        String stringRed, stringGreen, stringBlue;

        if(red < 10)
        {
            stringRed = "00" + red + ",";
        }else if(red < 100)
        {
            stringRed = "0" + red + ",";
        }else
        {
            stringRed = red + ",";
        }

        if(green < 10)
        {
            stringGreen = "00" + green + ",";
        }else if(green < 100)
        {
            stringGreen = "0" + green + ",";
        }else
        {
            stringGreen = green + ",";
        }

        if(blue < 10)
        {
            stringBlue = "00" + blue;
        }else if(blue < 100)
        {
            stringBlue = "0" + blue;
        }else
        {
            stringBlue = String.valueOf(blue);
        }

        textViewMessage.setText("SET_LED_RGB:" + stringRed + stringGreen + stringBlue);
    }

    private void sendDataToServer(String message)
    {
        try
        {
            // need host and port, we want to connect to the ServerSocket at port 7777
            Socket socket = new Socket();
            socket.setSoTimeout(800);
            socket.connect(new InetSocketAddress("192.168.2.239", 80), 800);
            System.out.println("Connected!");

            // get the output stream from the socket.
            OutputStream outputStream = socket.getOutputStream();
            // create a data output stream from the output stream so we can send data through it
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            System.out.println("Sending string to the ServerSocket");

            // write the message we want to send
            dataOutputStream.writeUTF(message);
            dataOutputStream.flush(); // send the message
            dataOutputStream.close(); // close the output stream when we're done.

            System.out.println("Closing socket.");
            socket.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}