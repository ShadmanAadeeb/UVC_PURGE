package com.example.robotcontollerversion3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    WebView webView;
    Switch uvSwitch,superUserSwitch;
    TextView lastPressedButtonTextView;
    EditText debugEditText;
    Button forwardButton,leftButton, backwardButton,rightButton,stopButton,clearButton;
    SeekBar pwmSeekBar;
    TextView pwmValueTextView;
    Boolean sendGetRequestFunctionLock=false;
    TextView batteryTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.linkTheXMLElements();
        this.setUpTheWebView();
        this.setListenersToTheButtons();
        this.setListenerToTheSwitchAndTheSeekBar();
        enableTheBatteryTextView();
        //this.startTheTimer();
        /*This is for getting the battery percentage*/
        lastPressedButtonTextView.setText("S");
        sendGetRequest();

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.aboutMenuItem:
                Toast.makeText(MainActivity.this,"Going to about page",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(MainActivity.this,AboutUsActivity.class);
                startActivity(intent);
                return true;
        }
        return  false;
    }
    void enableTheBatteryTextView(){
        batteryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendGetRequest();
            }
        });
    }

    void linkTheXMLElements(){
        webView=findViewById(R.id.webView);
        superUserSwitch=findViewById(R.id.superUserSwitch);
        uvSwitch=findViewById(R.id.uvSwitch);
        lastPressedButtonTextView=findViewById(R.id.lastPressedButtonTextView);
        debugEditText=findViewById(R.id.responseEditText);
        forwardButton=findViewById(R.id.forwardButton);
        backwardButton =findViewById(R.id.backButton);
        leftButton=findViewById(R.id.leftButton);
        rightButton=findViewById(R.id.rightButton);
        stopButton=findViewById(R.id.stopButton);
        clearButton=findViewById(R.id.clearButton);
        pwmSeekBar=findViewById(R.id.pwmSeekBar);
        pwmValueTextView=findViewById(R.id.pwmValueTextView);
        batteryTextView=findViewById(R.id.batteryTextView);
    }
    void setUpTheWebView(){
        //String path="https://www.youtube.com/";
        String path="http://192.168.0.100:8000/index.html";
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        webView.loadUrl(path);
    }


    /*ENABLING THE BUTTONS*/
    void setListenersToTheButtons(){
        /*SETTING LISTENER TO THE FORWARD BUTTON*/
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lastPressedButtonTextView.setText("F");
                sendGetRequest();
            }
        });
        /*SETTING LISTENER TO THE BACKWARD BUTTON*/
        backwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lastPressedButtonTextView.setText("B");
                sendGetRequest();
            }
        });
        /*SETTING LISTENER TO THE LEFT BUTTON*/
        ;leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lastPressedButtonTextView.setText("L");
                sendGetRequest();

            }
        });
        /*SETTING LISTENER TO THE RIGHT BUTTON*/
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lastPressedButtonTextView.setText("R");
                sendGetRequest();
            }
        });

        /*SETTING LISTENER TO THE RIGHT BUTTON*/
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lastPressedButtonTextView.setText("S");
                sendGetRequest();
            }
        });
        /*SETTING LISTENER TO THE CLEAR BUTTON*/
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                debugEditText.setText("");
            }
        });

    }

    /*ENABLING THE UV LIGHT SWITCH AND THE PWM BAR*/
    void setListenerToTheSwitchAndTheSeekBar(){
        uvSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sendGetRequest();
            }
        });
        superUserSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(MainActivity.this,"SuperUserMode:"+b,Toast.LENGTH_SHORT).show();
                if(b){
                    sendSuperModeGetRequest();
                }else{
                    sendGetRequest();
                }
            }
        });
        pwmSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                pwmValueTextView.setText("Speed:"+i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sendGetRequest();
            }
        });
    }
    void sendSuperModeGetRequest(){
        String url="";
        url="http://192.168.0.101/"+"supermode"+"?pwm=";
        url+=String.valueOf( pwmValueTextView.getText()).replaceAll("[^0-9]", "");
        url+="&uvl=";
        if(uvSwitch.isChecked()){
            url+="1";
        }else{
            url+="0";
        }


        /*Checking the lock*/
        if(sendGetRequestFunctionLock==false){//There is no lock i.e it is open
            //Proceed
            Log.d("TAG1","You are free to go. But I am locking it");
            sendGetRequestFunctionLock=true;
        }else{//the lock is closed
            Log.d("TAG1","You are still locked");
            return;
        }
        /*URL TESTING CODE*/
        debugEditText.setText(url);
        final RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        //url="http://scratchpads.eu/explore/sites-list";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        debugEditText.setText("Response is: "+ response);
                        queue.getCache().clear();
                        sendGetRequestFunctionLock=false;


                        /*Code to be used in response part for battery*/
                        String responseString=response;
                        String batteryString="...";
                        if(responseString.length()>=4){
                            batteryString=responseString.substring(1,4);
                        }
                        batteryTextView.setText("Battery:"+batteryString);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                debugEditText.setText("That did not work");
                queue.getCache().clear();
                sendGetRequestFunctionLock=false;
            }
        });
        queue.add(stringRequest);
    }

    void sendGetRequest(){
        /*Setting up the last button pressed text view*/
        String directionString="";
        String lastPressedButtonTextViewString= String.valueOf(lastPressedButtonTextView.getText());

        if(lastPressedButtonTextViewString.equals("F")){
            directionString="forward";
        }else if(lastPressedButtonTextViewString.equals("B")){
            directionString="backward";
        }else if(lastPressedButtonTextViewString.equals("L")){
            directionString="left";
        }else if(lastPressedButtonTextViewString.equals("R")){
            directionString="right";
        }else if(lastPressedButtonTextViewString.equals("S")){
            directionString="stop";
        }
        /*Constructing the url*/
        String url="";
        url="http://192.168.0.101/"+directionString+"?pwm=";
        url+=String.valueOf( pwmValueTextView.getText()).replaceAll("[^0-9]", "");
        url+="&uvl=";
        if(uvSwitch.isChecked()){
            url+="1";
        }else{
            url+="0";
        }

        /*Checking the lock*/
        if(sendGetRequestFunctionLock==false){//There is no lock i.e it is open
            //Proceed
            Log.d("TAG1","You are free to go. But I am locking it");
            sendGetRequestFunctionLock=true;
        }else{//the lock is closed
            Log.d("TAG1","You are still locked");
            return;
        }
        /*URL TESTING CODE*/
        debugEditText.setText(url);
        final RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        //url="http://scratchpads.eu/explore/sites-list";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        debugEditText.setText("Response is: "+ response);
                        queue.getCache().clear();
                        sendGetRequestFunctionLock=false;


                        /*Code to be used in response part for battery*/
                        String responseString=response;
                        String batteryString="...";
                        if(responseString.length()==9){
                            batteryString=responseString.substring(1,4);
                        }
                        batteryTextView.setText("Battery:"+batteryString);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                debugEditText.setText("That did not work");
                queue.getCache().clear();
                sendGetRequestFunctionLock=false;
            }
        });
        queue.add(stringRequest);
    }

    private void startTheTimer(){
        new Timer().scheduleAtFixedRate(new TimerTask() {
            int time=0;
            @Override
            public void run() {
                sendGetRequest();
            }
        },0,30000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lastPressedButtonTextView.setText("S");
        sendGetRequest();
    }
}