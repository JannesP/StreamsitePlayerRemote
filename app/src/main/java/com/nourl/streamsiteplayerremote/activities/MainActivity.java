package com.nourl.streamsiteplayerremote.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.nourl.streamsiteplayerremote.R;
import com.nourl.streamsiteplayerremote.Util;
import com.nourl.streamsiteplayerremote.networking.NetworkInterface;
import com.nourl.streamsiteplayerremote.networking.NetworkMediaPlayerControl;
import com.nourl.streamsiteplayerremote.networking.UByte;
import com.nourl.streamsiteplayerremote.networking.messages.ControlNetworkMessage;
import com.nourl.streamsiteplayerremote.networking.tcp.TcpNetworkInterface;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, NumberPicker.OnValueChangeListener {

    private NetworkMediaPlayerControl mediaControl;
    MyMediaController mediaController;
    private NetworkInterface networkInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupEventListeners();
        createNetworkInterface();
        createMediaControl();
        setLayoutState(false);
    }

    private void setupEventListeners() {
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

        NumberPicker npStart = (NumberPicker) findViewById(R.id.numberPickerSkipStart);
        npStart.setMinValue(0);
        npStart.setMaxValue(600);
        npStart.setOnValueChangedListener(this);

        NumberPicker npEnd = (NumberPicker) findViewById(R.id.numberPickerSkipEnd);
        npEnd.setMinValue(0);
        npEnd.setMaxValue(600);
        npEnd.setOnValueChangedListener(this);

        Button b = (Button) findViewById(R.id.buttonToggleFullscreen);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                networkInterface.sendMessage(new ControlNetworkMessage(ControlNetworkMessage.ControlNetworkMessageType.TOGGLE_FULLSCREEN, new UByte(0)));
            }
        });

        SeekBar sb = (SeekBar) findViewById(R.id.seekBarVolume);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private long lastChange = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if ((lastChange + 250) < SystemClock.elapsedRealtime()) {
                    networkInterface.sendMessage(new ControlNetworkMessage(ControlNetworkMessage.ControlNetworkMessageType.VOLUME, new UByte(0), new byte[]{(byte) progress}));
                    lastChange = SystemClock.elapsedRealtime();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    public void setLayoutState(boolean connected) {
        mediaController.setRealVisibility(connected);
        findViewById(R.id.layoutConnected).setVisibility(connected ? View.VISIBLE : View.INVISIBLE);
        findViewById(R.id.layoutConnecting).setVisibility(!connected ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("onPause", "Stopping explicit refresh over network ...");
        mediaControl.stopNetworkRefresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onResume", "Starting explicit refresh over network ...");
        mediaControl.startNetworkRefresh();
    }

    @Override
    protected void onDestroy() {
        Log.d("onDestroy", "onDestroy called, shutting down ...");
        mediaControl.stopNetworkRefresh();
        networkInterface.stop();
        super.onDestroy();
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
            Intent intent = new Intent();
            intent.setClass(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createNetworkInterface() {
        if (networkInterface != null) {
            networkInterface.stop();
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String ip = prefs.getString("pref_tcp_ip", "127.0.0.1");
        int port = Integer.parseInt(prefs.getString("pref_tcp_port", "8003"));
        int timeout = Integer.parseInt(prefs.getString("pref_tcp_timeout", "1000"));
        networkInterface = new TcpNetworkInterface(ip, port, timeout);
        networkInterface.start();
    }

    private void createMediaControl() {
        mediaController = new MyMediaController(this, false);
        mediaController.setAnchorView(findViewById(R.id.mediaFrame));
        mediaControl = new NetworkMediaPlayerControl(mediaController, networkInterface, this);

        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(
            new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    mediaControl.show();
                }
            }
        );

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case "pref_tcp_ip":
                createNetworkInterface();
                break;
            case "pref_tcp_port":
                createNetworkInterface();
                break;
            case "pref_tcp_timeout":
                createNetworkInterface();
                break;
        }
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        switch (picker.getId()) {
            case R.id.numberPickerSkipStart:
                if (networkInterface.isWorking())
                    networkInterface.sendMessage(new ControlNetworkMessage(ControlNetworkMessage.ControlNetworkMessageType.SKIP_START, new UByte(0), Util.intToByteArray(picker.getValue())));
                break;
            case R.id.numberPickerSkipEnd:
                if (networkInterface.isWorking())
                    networkInterface.sendMessage(new ControlNetworkMessage(ControlNetworkMessage.ControlNetworkMessageType.SKIP_END, new UByte(0), Util.intToByteArray(picker.getValue())));
                break;
        }
    }

    public class MyMediaController extends MediaController {
        private boolean visible = true;
        private boolean isReadyToShow = false;

        public MyMediaController(Context context, boolean useFastForward) {
            super(context, useFastForward);
        }

        @Override
        public void show() {
            if (isReadyToShow) {
                super.show();
            }
        }

        @Override
        public void hide() {
            if (visible) {
                show();
            } else {
                super.hide();
            }
        }  //override to prevent automatic hiding

        public void setRealVisibility(boolean visible) {
            this.visible = visible;
            if (visible) {
                show();
            } else {
                hide();
            }
        }

        public void setReadyToShow() {
            isReadyToShow = true;
            if (visible) {
                show();
            }
        }
    }
}
